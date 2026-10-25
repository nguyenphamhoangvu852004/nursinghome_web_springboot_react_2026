import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { test } from "node:test";
import { runInNewContext } from "node:vm";
import ts from "typescript";

// Exercise the service against a stub HTTP transport without a running database.
function loadService(apiClient) {
  const source = readFileSync(new URL("../src/services/care-plan/care-plan-v2-services.ts", import.meta.url), "utf8");
  const { outputText } = ts.transpileModule(source, {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2023 },
  });
  const exports = {};
  runInNewContext(outputText, {
    exports,
    require: (name) => {
      if (name === "@/common/config/api-url") return { CARE_PLAN_API_BASE_URL: "/api/v2" };
      assert.equal(name, "@/lib/api-client");
      return { apiClient };
    },
  });
  return exports;
}

test("admission picker reads every page and excludes old, discharged and future admissions", async () => {
  const pages = [];
  const row = (id, overrides = {}) => ({
    id, residentId: id + 100, residentName: `Resident ${id}`,
    admissionDate: "2020-01-01", dischargeDate: null, status: "ACTIVE", ...overrides,
  });
  const service = loadService({
    get: async (url, config) => {
      assert.equal(config.baseURL, undefined);
      if (url === "/admissions/select-active") {
        return { data: { statusCode: 200, data: [1, 3, 4, 5].map((id) => ({ id })) } };
      }
      assert.equal(url, "/admissions");
      pages.push(config.params.page);
      const data = config.params.page === 0
        ? [row(1), row(2), row(3, { dischargeDate: "2020-02-01", status: "DISCHARGED" })]
        : [row(4, { admissionDate: "2999-01-01" }), row(5)];
      return { data: { statusCode: 200, data, metadata: { hasNext: config.params.page === 0 } } };
    },
  });
  const result = await service.getCarePlanAdmissions();
  assert.deepEqual(pages, [0, 1]);
  assert.deepEqual(Array.from(result, (item) => item.id), [1, 5]);
});

test("create posts only resident and admission to v2 and returns the created ID", async () => {
  const payload = { residentId: 25, admissionId: 802 };
  const service = loadService({
    post: async (url, body, config) => {
      assert.equal(url, "/care-plans");
      assert.equal(config.baseURL, "/api/v2");
      assert.deepEqual(body, payload);
      assert.equal("creatorId" in body, false);
      return { data: { statusCode: 200, data: { id: 1001 } } };
    },
  });
  assert.equal((await service.createCarePlanV2(payload)).id, 1001);
});

test("HTTP 200 containing a business error does not count as successful creation", async () => {
  const service = loadService({
    post: async () => ({ data: { statusCode: 409, message: "Care plan already exists", data: null } }),
  });
  await assert.rejects(service.createCarePlanV2({ residentId: 25, admissionId: 802 }), /already exists/);
});

test("a missing created ID does not show false success", async () => {
  const service = loadService({
    post: async () => ({ data: { statusCode: 200, data: {} } }),
  });
  await assert.rejects(service.createCarePlanV2({ residentId: 25, admissionId: 802 }), /did not return a care plan ID/);
});

test("list uses the v2 namespace, filter parameters and abort signal", async () => {
  const params = { page: 2, size: 10, search: "Ada", state: "OPEN", versionStatus: "DRAFT" };
  const signal = new AbortController().signal;
  const response = { statusCode: 200, data: { list: [], statistics: { total: 0 } }, metadata: { totalElements: 0 } };
  const service = loadService({ get: async (url, config) => {
    assert.equal(url, "/care-plans");
    assert.equal(config.baseURL, "/api/v2");
    assert.equal(config.params, params);
    assert.equal(config.signal, signal);
    return { data: response };
  } });
  assert.equal(await service.getCarePlansV2(params, signal), response);
});

test("list business errors are surfaced instead of an empty result", async () => {
  const service = loadService({ get: async () => ({ data: { statusCode: 400, message: "Unsupported filter" } }) });
  await assert.rejects(service.getCarePlansV2({ page: 0, size: 10 }), /Unsupported filter/);
});

test("API bases keep the configured server and normalize existing version prefixes", () => {
  const source = readFileSync(new URL("../src/common/config/api-url.ts", import.meta.url), "utf8");
  const { outputText } = ts.transpileModule(source, {
    compilerOptions: { module: ts.ModuleKind.CommonJS },
  });
  for (const suffix of ["", "/", "/api", "/api/v1", "/api/v2/"]) {
    const exports = {};
    runInNewContext(outputText, {
      exports,
      require: () => ({ ENV: { API_URL: `https://example.test/backend${suffix}` } }),
    });
    assert.equal(exports.API_BASE_URL, "https://example.test/backend/api/v1");
    assert.equal(exports.CARE_PLAN_API_BASE_URL, "https://example.test/backend/api/v2");
  }
});

test("content, goals, interventions and legacy services all use v2", async () => {
  const calls = [];
  const transport = Object.fromEntries(["get", "post", "patch"].map((method) => [method,
    async (url, ...args) => {
      assert.equal(args.at(-1).baseURL, "/api/v2");
      calls.push(url);
      return { data: { statusCode: 200, data: {} } };
    },
  ]));
  function load(file) {
    const source = readFileSync(new URL(`../src/services/care-plan/${file}`, import.meta.url), "utf8");
    const { outputText } = ts.transpileModule(source, {
      compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2023 },
    });
    const exports = {};
    runInNewContext(outputText, { exports, URLSearchParams,
      require: (name) => name === "@/lib/api-client" ? { apiClient: transport }
        : { CARE_PLAN_API_BASE_URL: "/api/v2" },
    });
    return exports;
  }
  const content = load("care-plan-content.ts");
  await content.getPlanContent(1);
  await content.createGoal(2, {});
  await content.createIntervention(3, {});
  const legacy = load("care-plan-services.ts");
  await legacy.getCarePlanList();
  await legacy.getCarePlanDetail(1);
  await legacy.approveCarePlan(1);
  await legacy.searchCarePlans("Ada", "Draft");
  assert.equal(calls.length, 7);
});
