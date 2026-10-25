# ElderCare — Nursing Home Management

Ứng dụng web quản lý viện dưỡng lão, hỗ trợ các nghiệp vụ tiếp nhận cư dân, đánh giá nhu cầu chăm sóc, lập kế hoạch chăm sóc và quản lý hoạt động của cơ sở.

**Dự án nhóm · Đang phát triển · Java / Spring Boot + React / TypeScript + SQL Server**

## Đóng góp cá nhân

**[nguyenphamhoangvu852004](https://github.com/nguyenphamhoangvu852004)** — phụ trách **Care Planning, Care Goal và Care Intervention**, đang tiếp tục nâng cấp module. Định hướng: **Backend Java hoặc Full-stack thiên về Backend**.

Phần giới thiệu kỹ thuật bên dưới tập trung vào module này. Các nghiệp vụ còn lại cung cấp bối cảnh cho sản phẩm chung của nhóm.

| Phạm vi phụ trách | Nội dung để người review kiểm tra |
| --- | --- |
| Care Planning | Kế hoạch gắn với cư dân và đợt nhập viện; phiên bản nội dung; luồng gửi duyệt, chấp thuận và từ chối |
| Care Goal | Mục tiêu gắn với phiên bản kế hoạch; thông tin nhu cầu, kết quả mong muốn, baseline, target và mức ưu tiên |
| Care Intervention | Biện pháp chăm sóc gắn với mục tiêu và phiên bản; kiểm tra quan hệ dữ liệu trước khi lưu |

## Bài toán nghiệp vụ

Một cư dân có thể có nhiều đợt lưu trú và nhu cầu chăm sóc thay đổi theo thời gian. Kế hoạch cần gắn với đúng đợt tiếp nhận, tổ chức nội dung thành mục tiêu và biện pháp thực hiện, đồng thời phân biệt nội dung đang soạn với nội dung đã có hiệu lực.

Module Care Planning v2 mô hình hóa quan hệ:

```text
Resident → Admission → Care Plan → Care Plan Version → Care Goal → Care Intervention
```

Ví dụ minh họa: một mục tiêu hỗ trợ khả năng vận động có thể ghi nhận tình trạng ban đầu, kết quả kỳ vọng, thời hạn và các biện pháp hỗ trợ tương ứng. Đây là ví dụ giải thích cấu trúc dữ liệu, không phải dữ liệu bệnh nhân thực tế.

## Luồng chức năng chính

1. Chọn cư dân và đợt tiếp nhận để tạo kế hoạch. Service kiểm tra đợt tiếp nhận đã có kế hoạch `OPEN` hay chưa.
2. Tạo kế hoạch cùng phiên bản đầu tiên ở trạng thái `DRAFT` trong một transaction.
3. Thêm mục tiêu và biện pháp chăm sóc vào bản nháp. Service kiểm tra dữ liệu bắt buộc, trạng thái phiên bản và quan hệ giữa các đối tượng.
4. Gửi phiên bản sang `PENDING_APPROVAL`; xử lý chấp thuận thành `EFFECTIVE` hoặc từ chối kèm lý do.
5. Tra cứu danh sách có tìm kiếm, lọc, sắp xếp và phân trang; xem nội dung qua giao diện quản trị.

Luồng phiên bản và phần tích hợp giao diện đang tiếp tục được hoàn thiện. Các giới hạn hiện tại được ghi ở cuối tài liệu.

## Điểm kỹ thuật đáng xem

| Vấn đề | Cách triển khai hiện tại | Mã nguồn |
| --- | --- | --- |
| Tính nhất quán khi khởi tạo | Tạo plan, phiên bản đầu tiên và liên kết phiên bản trong `@Transactional` | [CarePlanService](backend/src/main/java/com/eldercare/modules/careplan_management/careplan_design/service/v2/CarePlanService.java) |
| Quy tắc nghiệp vụ khi ghi nội dung | Kiểm tra bản nháp, trường bắt buộc và độ dài; intervention kiểm tra goal thuộc phiên bản tương ứng | [CareGoalService](backend/src/main/java/com/eldercare/modules/careplan_management/careplan_design/service/v2/CareGoalService.java), [CareInterventionService](backend/src/main/java/com/eldercare/modules/careplan_management/careplan_design/service/v2/CareInterventionService.java) |
| Mô hình kế hoạch có phiên bản | Tách `CarePlan` và `CarePlanVersion`; có trường `@Version` để hỗ trợ optimistic locking | [Entities v2](backend/src/main/java/com/eldercare/modules/careplan_management/careplan_design/entity/v2) |
| Truy vấn danh sách nhiều bảng | Read model phẳng bằng JDBC; dùng `ROW_NUMBER()` chọn phiên bản mới nhất, tham số hóa điều kiện và danh sách cột sort cho phép | [CarePlanListQueryService](backend/src/main/java/com/eldercare/modules/careplan_management/careplan_design/service/v2/CarePlanListQueryService.java) |
| Chuyển đổi API từng bước | Namespace `/api/v1` và `/api/v2`; frontend tập trung cấu hình API base URL | [API configuration](frontend/src/common/config/api-url.ts), [Controllers v2](backend/src/main/java/com/eldercare/modules/careplan_management/careplan_design/controller/v2) |
| Kiểm thử hợp đồng frontend–backend | Test service với HTTP transport giả lập; kiểm tra payload, URL, phân trang và lỗi nghiệp vụ trong HTTP 200 | [Frontend service tests](frontend/scripts/test-care-plan-v2.mjs) |

Kiểm tra một kế hoạch `OPEN` hiện nằm ở service; điều này chưa chứng minh an toàn khi hai request tạo đồng thời. Tương tự, có `@Version` trong entity chưa thay thế việc kiểm thử xung đột trên SQL Server thực tế.

## Công nghệ và cấu trúc

| Thành phần | Công nghệ khai báo trong dự án |
| --- | --- |
| Backend | Java 21, Spring Boot 4.1.0, Spring MVC, Spring Data JPA, JDBC, Bean Validation |
| Mapping | MapStruct, Lombok |
| Database | Microsoft SQL Server; H2 trong cấu hình test |
| Frontend | React 19, TypeScript 6, Vite 8, Tailwind CSS 4 |
| Data và form | TanStack Query, Axios, Zustand, React Hook Form, Zod |
| Kiểm thử | Spring Boot Test, Spring Security Test, Node.js test runner |

Backend tổ chức theo module nghiệp vụ, bên trong chia controller, service, repository, DTO và entity. Frontend nhóm màn hình và hooks theo feature, đồng thời có services và components dùng chung.

```text
backend/
  src/main/java/com/eldercare/
    modules/                 # Các module nghiệp vụ
    common/                  # DTO, constants và thành phần dùng chung
    config/                  # Cấu hình ứng dụng
    exception/               # Xử lý lỗi
  src/main/resources/db/     # SQL cho care planning v2
  src/test/                  # Controller, service, repository và entity tests
  http/                      # Request mẫu để thử API
frontend/
  src/features/admin/        # Các màn hình quản trị
  src/services/care-plan/    # Client cho API care planning
  src/common/config/         # Cấu hình API
  scripts/                   # Test service frontend
postman/                     # Tài nguyên Postman
```

Các module khác trong repository gồm tiếp nhận và hồ sơ cư dân, assessment, cơ sở/phòng/giường, kho vật tư, sự cố, SLA và staffing. Mức hoàn thiện cần được đánh giá theo từng module.

## Chạy local

### Điều kiện

- JDK 21; backend có Maven Wrapper.
- Node.js 24 và pnpm để cài frontend theo `pnpm-lock.yaml` hiện có.
- SQL Server cùng database và dữ liệu nền phù hợp với các entity.

**Database chưa có quy trình khởi tạo toàn hệ thống có thể tái lập từ repository.** Backend đặt `ddl-auto: none`, nên cần chuẩn bị schema trước khi chạy. [v2_care_planning.sql](backend/src/main/resources/db/v2_care_planning.sql) có lệnh `DROP TABLE` và chỉ bao phủ phần care planning; cần đọc và đối chiếu trước khi dùng trên database phát triển riêng. File này không phải migration đầy đủ cho toàn ứng dụng.

### Backend

Từ thư mục gốc, đặt biến môi trường theo database local của bạn:

```bash
export DB_HOST=localhost
export DB_PORT=1434
export DB_NAME=eldercare
export DB_USERNAME=sa
read -rsp 'Database password: ' DB_PASSWORD; echo
export DB_PASSWORD
export ALLOWED_ORIGINS=http://localhost:5173

cd backend
bash mvnw spring-boot:run
```

Backend dùng profile `dev` mặc định, địa chỉ API local là `http://localhost:8080`. Thay `DB_PORT` theo SQL Server của bạn. CORS hiện được cấu hình trực tiếp cho `http://localhost:5173` trong `SecurityConfig`; biến `ALLOWED_ORIGINS` chưa thay thế cấu hình đó.

### Frontend

Mở terminal khác, từ thư mục gốc:

```bash
cd frontend
pnpm install --frozen-lockfile
pnpm dev
```

Mở `http://localhost:5173`. API mặc định trỏ đến `http://localhost:8080`; có thể đổi bằng biến môi trường `VITE_API_URL` khi khởi động Vite. Đăng nhập cần user đã có trong database; repository chưa cung cấp hướng dẫn tài khoản demo có thể tái lập.

## Kiểm thử và trạng thái xác minh

```bash
# Từ backend/
bash mvnw test

# Từ frontend/
node --test scripts/test-care-plan-v2.mjs
pnpm lint
pnpm build
```

Backend có test cho controller, service, mapper và entity của care planning v2. Frontend có test cho tạo kế hoạch, lọc đợt tiếp nhận, truy vấn danh sách, API namespace và xử lý lỗi nghiệp vụ.

Trạng thái kiểm tra local ngày **25/10/2026**:

| Kiểm tra | Kết quả |
| --- | --- |
| Frontend service test | Lệnh chạy thành công |
| Frontend build | Dừng ở lỗi TypeScript `TS5101`: cấu hình `baseUrl` deprecated |
| Backend tests | Chưa chạy trong phiên kiểm tra vì môi trường thiếu Java |
| Frontend lint | Chưa chạy trong phiên kiểm tra |

## Giới hạn và hướng nâng cấp

- **Đang phát triển:** module Care Planning, Care Goal và Care Intervention tiếp tục được nâng cấp; repository còn đồng thời có code legacy và v2.
- **Xác thực và phân quyền:** login hiện tạo token giả lập và lưu session trong bộ nhớ; `SecurityConfig` đang cho phép mọi request. Route guard frontend không thay thế phân quyền backend. JWT thật và bảo vệ endpoint là hạng mục cần hoàn thiện.
- **Khả năng chạy lại:** cần bổ sung migration toàn hệ thống, dữ liệu demo và tài khoản demo có hướng dẫn rõ ràng.
- **Độ tin cậy:** cần xác minh luồng trạng thái end-to-end, ràng buộc khi tạo đồng thời và optimistic locking trên SQL Server.
- **Tự động hóa:** cần xử lý lỗi build hiện tại và thiết lập CI chạy build/test trước khi merge.

## Liên hệ

- GitHub: [nguyenphamhoangvu852004](https://github.com/nguyenphamhoangvu852004)
- Email: [nguyenphamhoangvu852004@gmail.com](mailto:nguyenphamhoangvu852004@gmail.com)
