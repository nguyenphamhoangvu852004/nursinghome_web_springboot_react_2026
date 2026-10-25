import { AlertCircle, Loader2, ShieldCheck } from "lucide-react";
import { useRoleList } from "../hooks/use-role-list";

const RolePage = () => {
    const { data: roles, isLoading, isError } = useRoleList();

    if (isLoading) {
        return (
            <div className="flex items-center justify-center gap-2 p-10 text-sm text-gray-500">
                <Loader2 className="h-4 w-4 animate-spin" />
                Loading roles...
            </div>
        );
    }

    if (isError) {
        return (
            <div className="mx-auto mt-6 flex max-w-md items-start gap-3 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-800">
                <AlertCircle className="mt-0.5 h-5 w-5 shrink-0 text-red-600" />
                Failed to load roles. Please refresh the page or try again later.
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-6xl space-y-6 p-4 sm:p-6">
            <div className="space-y-1">
                <p className="text-sm text-gray-500">Admin &gt; Roles</p>

                <h1 className="text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl">
                    Role &amp; Permission Matrix
                </h1>

                <p className="max-w-2xl text-sm text-gray-500">
                    Read-only reference — defines what each role can see and do across
                    NHMS.
                </p>
            </div>

            <div>
                <div className="mb-4 flex items-center justify-between">
                    <h2 className="text-lg font-bold text-gray-900 sm:text-xl">
                        Role Directory
                    </h2>
                    <span className="text-sm text-gray-400">
                        {roles?.length ?? 0} role{roles?.length === 1 ? "" : "s"}
                    </span>
                </div>

                {roles && roles.length === 0 ? (
                    <div className="rounded-lg border border-dashed border-gray-300 bg-gray-50 p-8 text-center text-sm text-gray-500">
                        No roles have been configured yet.
                    </div>
                ) : (
                    <div className="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-3">
                        {roles?.map((role) => (
                            <div
                                key={role.id}
                                className="group rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition-shadow hover:shadow-md"
                            >
                                <div className="mb-3 flex items-start justify-between gap-2">
                                    <h3 className="text-lg font-bold text-gray-900">
                                        {role.roleName}
                                    </h3>
                                    <ShieldCheck className="h-5 w-5 shrink-0 text-gray-300 transition-colors group-hover:text-blue-400" />
                                </div>

                                <span className="inline-flex items-center rounded-full border border-blue-200 bg-blue-50 px-3 py-1 text-xs font-semibold text-blue-700">
                                    Internal
                                </span>

                                <p className="mt-4 text-sm leading-6 text-gray-500">
                                    {role.description}
                                </p>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default RolePage;
