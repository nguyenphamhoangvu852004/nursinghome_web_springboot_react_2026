# ElderCare Frontend

Phần Frontend của dự án **ElderCare** — hệ thống chăm sóc sức khỏe người cao tuổi. Tài liệu này giải thích chi tiết cấu trúc thư mục, công nghệ sử dụng, và các quy tắc phát triển.

---

## 1. Công nghệ Sử dụng (Technology Stack)

| Danh mục | Công nghệ | Mô tả |
|---|---|---|
| **Core** | [React 19](https://react.dev/) & [TypeScript](https://www.typescriptlang.org/) | Thư viện UI khai báo kết hợp kiểu dữ liệu tĩnh, giúp phát hiện lỗi sớm ngay từ lúc viết code |
| **Build Tool** | [Vite](https://vite.dev/) | Công cụ build hiện đại, khởi động dev server gần như tức thì nhờ ESModules, hỗ trợ Hot Module Replacement (HMR) |
| **Styling** | [Tailwind CSS v4](https://tailwindcss.com/) | Framework CSS theo hướng utility-first, viết style trực tiếp trong `className` mà không cần tạo file CSS riêng |
| **UI Components** | [shadcn/ui](https://ui.shadcn.com/) | Bộ component UI chất lượng cao, dựa trên `@base-ui/react` và icon từ `lucide-react` |
| **Server State** | [TanStack React Query v5](https://tanstack.com/query/latest) | Quản lý dữ liệu từ API: tự động cache, re-fetch khi mất/có lại mạng, xử lý loading/error |
| **Client State** | [Zustand](https://zustand-demo.pmnd.rs/) | Quản lý state toàn cục phía client, nhẹ và đơn giản, không cần Provider/Context phức tạp |
| **Form & Validation** | [React Hook Form](https://react-hook-form.com/) & [Zod](https://zod.dev/) | Quản lý form tối ưu hiệu năng (giảm re-render) kết hợp schema validation chặt chẽ |
| **HTTP Client** | [Axios](https://axios-http.com/) | Thư viện gọi HTTP, hỗ trợ interceptor để tự động đính kèm token và xử lý refresh token |

---

## 2. Cấu trúc Thư mục (Directory Structure)

```text
frontend/
│
├── public/                     # Tài nguyên tĩnh công khai
│   ├── favicon.svg             #   Icon hiển thị trên tab trình duyệt
│   └── icons.svg               #   Bộ icon SVG sprite dùng chung
│
├── src/                        # ★ THƯ MỤC MÃ NGUỒN CHÍNH ★
│   │
│   ├── assets/                 # Hình ảnh & icon tĩnh được import trong code
│   │   ├── hero.png            #   Hình ảnh minh họa trang chính
│   │   ├── react.svg           #   Logo React
│   │   └── vite.svg            #   Logo Vite
│   │
│   ├── common/                 # Tài nguyên, hằng số, và thành phần dùng chung toàn cục
│   │
│   ├── components/             # UI Components tái sử dụng được ở mọi nơi trong ứng dụng
│   │   └── ui/                 #   Component nguyên tử từ shadcn/ui (Button, Input, Dialog...)
│   │       └── button.tsx      #     Component Button với nhiều variant và size
│   │
│   ├── config/                 # Cấu hình dự án (API endpoints, environment variables, app settings)
│   │
│   ├── features/               # ★ CÁC TÍNH NĂNG CHÍNH — Kiến trúc Feature-Based ★
│   │   ├── auth/               #   Xác thực: Đăng nhập, Đăng ký, Quên mật khẩu
│   │   ├── dashboard/          #   Trang tổng quan: Thống kê, biểu đồ, thông báo
│   │   └── user/               #   Quản lý người dùng: Hồ sơ, cài đặt tài khoản
│   │
│   ├── hooks/                  # Custom Hooks dùng chung toàn ứng dụng
│   │
│   ├── layouts/                # Khung giao diện bọc ngoài các trang (Layout Wrappers)
│   │
│   ├── lib/                    # Cấu hình & tích hợp thư viện bên thứ 3
│   │   └── utils.ts            #   Hàm cn() để ghép class Tailwind thông minh (clsx + tailwind-merge)
│   │
│   ├── routes/                 # Cấu hình định tuyến (Public Routes & Protected Routes)
│   │
│   ├── services/               # Các hàm gọi API chung không thuộc riêng tính năng nào
│   │
│   ├── store/                  # Quản lý state toàn cục bằng Zustand
│   │
│   ├── types/                  # Định nghĩa TypeScript Interfaces & Types toàn cục
│   │
│   ├── utils/                  # Hàm tiện ích dùng chung (format ngày, tiền tệ, regex...)
│   │
│   ├── App.css                 # CSS riêng cho App component
│   ├── App.tsx                 # Component gốc: chứa routing và các Provider chính
│   ├── index.css               # CSS toàn cục, tích hợp Tailwind CSS
│   └── main.tsx                # Điểm khởi đầu (Entrypoint): render React app vào DOM
│
├── .gitignore                  # Danh sách file/thư mục bỏ qua khi commit Git
├── .oxlintrc.json              # Cấu hình Oxlint — công cụ lint code cực nhanh
├── components.json             # Cấu hình khởi tạo shadcn/ui
├── index.html                  # Trang HTML gốc chứa <div id="root">
├── package.json                # Quản lý dependencies và scripts của dự án
├── tsconfig.json               # Cấu hình TypeScript chính (kế thừa từ tsconfig.app.json & tsconfig.node.json)
├── tsconfig.app.json           # Cấu hình TypeScript cho mã nguồn ứng dụng (src/)
├── tsconfig.node.json          # Cấu hình TypeScript cho mã nguồn cấu hình (vite.config.ts)
└── vite.config.ts              # Cấu hình Vite: plugins, alias, proxy, build options
```

---

## 3. Giải thích Chi tiết Các Thư mục

### 📂 `public/` — Tài nguyên tĩnh công khai

Chứa các file tĩnh được phục vụ trực tiếp bởi web server **mà không qua quá trình build** của Vite. Trình duyệt có thể truy cập trực tiếp theo đường dẫn `/favicon.svg`, `/icons.svg`.

- **Khi nào đặt file ở đây:** Favicon, `robots.txt`, ảnh Open Graph, hoặc file cần giữ nguyên tên khi deploy.
- **Khác với `src/assets/`:** File trong `public/` không được Vite tối ưu hóa (hash tên, nén). File trong `assets/` sẽ được Vite xử lý và tối ưu khi build.

---

### 📂 `src/assets/` — Hình ảnh & tài nguyên import trong code

Chứa hình ảnh, icon SVG, font, và các tài nguyên tĩnh được **import trực tiếp trong mã nguồn** thông qua câu lệnh `import`.

```tsx
import heroImage from '@/assets/hero.png';
// → Vite sẽ tự thêm hash vào tên file khi build (hero-abc123.png)
//   để tối ưu cache trên trình duyệt.
```

---

### 📂 `src/common/` — Tài nguyên dùng chung toàn cục

Chứa các hằng số (`constants`), enum, và tài nguyên chung không thuộc danh mục cụ thể nào khác. Ví dụ:
- Hằng số hệ thống: `ROLES`, `STATUS_CODES`, `PAGINATION_DEFAULTS`
- Enum dùng chung: `UserRole`, `AppointmentStatus`

---

### 📂 `src/components/` — UI Components tái sử dụng

Chứa các component giao diện **dùng chung** cho toàn bộ ứng dụng, không thuộc riêng bất kỳ tính năng nào.

#### 📁 `components/ui/` — Component nguyên tử (Atomic Components)

Thư mục này chứa các component UI cơ bản nhất được cài đặt thông qua **shadcn/ui**:

| Component | Chức năng |
|---|---|
| `button.tsx` | Nút bấm với nhiều biến thể (variant): `default`, `destructive`, `outline`, `secondary`, `ghost`, `link` và kích thước (size): `default`, `sm`, `lg`, `icon` |
| `input.tsx` | (Sẽ thêm) Ô nhập liệu văn bản |
| `dialog.tsx` | (Sẽ thêm) Hộp thoại modal |
| `card.tsx` | (Sẽ thêm) Thẻ hiển thị nội dung |

> **Cách thêm component mới từ shadcn/ui:**
> ```bash
> npx shadcn@latest add <tên-component>
> # Ví dụ: npx shadcn@latest add input dialog card
> ```

---

### 📂 `src/config/` — Cấu hình dự án

Tập trung mọi cấu hình của ứng dụng vào một nơi duy nhất. Ví dụ:
- `api.ts` — Base URL của API, danh sách endpoints
- `env.ts` — Biến môi trường (`VITE_API_URL`, `VITE_APP_NAME`)
- `app.ts` — Cấu hình chung (phân trang mặc định, timeout, ...)

---

### 📂 `src/features/` — ★ Các Tính năng Chính (Feature-Based Architecture) ★

Đây là thư mục **quan trọng nhất** của dự án. Thay vì gom toàn bộ component vào một chỗ, services vào một chỗ (kiến trúc truyền thống), dự án áp dụng **Kiến trúc Feature-Based** — mỗi tính năng là một module tự bao đóng (self-contained).

#### Cấu trúc bên trong mỗi feature:

```text
features/
└── auth/                       # Ví dụ: tính năng Xác thực
    ├── components/             #   Component riêng cho tính năng này
    │   ├── LoginForm.tsx       #     Form đăng nhập
    │   └── RegisterForm.tsx    #     Form đăng ký
    ├── hooks/                  #   Custom hooks riêng
    │   └── useLogin.ts         #     Hook xử lý logic đăng nhập
    ├── services/               #   API calls riêng
    │   └── authService.ts      #     Gọi API login, register, refresh token
    ├── store/                  #   State cục bộ cho tính năng (nếu cần)
    ├── types/                  #   Type definitions riêng
    │   └── auth.types.ts       #     LoginRequest, LoginResponse, User
    └── utils/                  #   Helper functions riêng
        └── tokenUtils.ts       #     Lưu/đọc/xóa token từ localStorage
```

#### Các features hiện có:

| Feature | Mô tả | Chức năng chính |
|---|---|---|
| `auth/` | Xác thực người dùng | Đăng nhập, đăng ký tài khoản, quên mật khẩu, xác thực email, refresh token |
| `dashboard/` | Trang tổng quan | Hiển thị thống kê, biểu đồ sức khỏe, thông báo, lịch hẹn sắp tới |
| `user/` | Quản lý người dùng | Xem/chỉnh sửa hồ sơ cá nhân, thay đổi mật khẩu, cài đặt tài khoản |

> **Nguyên tắc:** Nếu một component hoặc hook được **nhiều feature** cùng sử dụng → đưa lên `src/components/` hoặc `src/hooks/` thay vì giữ trong feature.

---

### 📂 `src/hooks/` — Custom Hooks dùng chung

Chứa các React custom hooks được **tái sử dụng** ở nhiều nơi trong ứng dụng. Ví dụ:

| Hook | Chức năng |
|---|---|
| `useDebounce.ts` | Trì hoãn cập nhật giá trị (dùng cho ô tìm kiếm, tránh gọi API liên tục) |
| `useLocalStorage.ts` | Đọc/ghi dữ liệu vào localStorage với hỗ trợ TypeScript |
| `useMediaQuery.ts` | Kiểm tra kích thước màn hình (responsive) |
| `useClickOutside.ts` | Phát hiện click bên ngoài một element (đóng dropdown/modal) |

---

### 📂 `src/layouts/` — Khung giao diện (Layout Wrappers)

Chứa các component layout bọc ngoài các trang, định nghĩa bố cục chung (header, sidebar, footer). Sử dụng `<Outlet />` của React Router để render nội dung trang con.

| Layout | Mô tả | Ví dụ sử dụng |
|---|---|---|
| `AuthLayout.tsx` | Layout cho trang xác thực: background gradient, form căn giữa | Trang đăng nhập, đăng ký |
| `DashboardLayout.tsx` | Layout có Header + Sidebar + vùng nội dung chính | Trang quản lý sau đăng nhập |
| `MainLayout.tsx` | Layout cho trang công khai: Navbar + Footer | Landing page, Giới thiệu |

---

### 📂 `src/lib/` — Cấu hình thư viện bên thứ 3

Chứa mã khởi tạo, cấu hình, hoặc wrapper cho các thư viện bên ngoài:

| File | Chức năng |
|---|---|
| `utils.ts` | Hàm `cn()` — ghép class Tailwind CSS thông minh bằng `clsx` + `tailwind-merge`, tự động loại bỏ class xung đột |
| `axios.ts` | (Sẽ thêm) Cấu hình Axios instance: `baseURL`, `headers`, `interceptors` để tự động đính kèm JWT token |
| `queryClient.ts` | (Sẽ thêm) Cấu hình TanStack React Query client: `defaultOptions`, `retry`, `staleTime` |

**Ví dụ sử dụng `cn()`:**
```tsx
import { cn } from '@/lib/utils';

<button className={cn(
  "px-4 py-2 rounded-lg",           // Luôn có
  variant === "primary" && "bg-blue-600 text-white",  // Có điều kiện
  className                           // Cho phép override từ bên ngoài
)} />
```

---

### 📂 `src/routes/` — Cấu hình định tuyến (Routing)

Quản lý sơ đồ định tuyến (URL mapping) của toàn bộ ứng dụng. Phân loại route thành:

- **Public Routes** — Trang truy cập tự do: Landing page, Đăng nhập, Đăng ký
- **Protected Routes** — Trang yêu cầu đăng nhập: Dashboard, Quản lý hồ sơ
- **Role-Based Routes** — Trang chỉ dành cho vai trò cụ thể: Admin panel

---

### 📂 `src/services/` — Hàm gọi API chung

Chứa các hàm gọi API **không thuộc riêng** một feature nào, hoặc các thiết lập API chung dùng với React Query. Ví dụ:
- `uploadService.ts` — Upload file lên server
- `notificationService.ts` — API thông báo dùng ở nhiều feature

---

### 📂 `src/store/` — Quản lý State toàn cục (Zustand)

Chứa các Zustand store để quản lý trạng thái toàn cục phía client — dữ liệu cần chia sẻ giữa nhiều component mà không phụ thuộc vào vòng đời component.

| Store | Chức năng |
|---|---|
| `useAuthStore.ts` | Lưu thông tin user đã đăng nhập, token, hàm `login()`, `logout()` |
| `useThemeStore.ts` | Lưu trạng thái giao diện Dark/Light mode |
| `useSidebarStore.ts` | Lưu trạng thái mở/đóng sidebar |

---

### 📂 `src/types/` — Định nghĩa TypeScript Types & Interfaces

Chứa các kiểu dữ liệu TypeScript **dùng chung** trên toàn hệ thống:

```typescript
// types/api.types.ts
interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

// types/user.types.ts
interface User {
  id: string;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'CAREGIVER' | 'FAMILY';
}
```

---

### 📂 `src/utils/` — Hàm tiện ích dùng chung (Utility Functions)

Chứa các hàm xử lý logic thuần túy (pure functions) — nhận đầu vào, trả kết quả, không phụ thuộc React:

| File | Chức năng |
|---|---|
| `formatDate.ts` | Định dạng ngày tháng: `2024-01-15` → `15/01/2024` |
| `formatCurrency.ts` | Định dạng tiền VND: `1500000` → `1.500.000 ₫` |
| `validation.ts` | Regex kiểm tra email, số điện thoại, CMND/CCCD |
| `storage.ts` | Wrapper cho `localStorage` với xử lý lỗi JSON parse |

---

## 4. Các File Gốc (Root Files)

| File | Chức năng |
|---|---|
| `index.html` | Trang HTML duy nhất chứa `<div id="root">` — React sẽ render toàn bộ ứng dụng vào đây (Single Page Application) |
| `package.json` | Khai báo dependencies, devDependencies, và scripts: `dev`, `build`, `lint`, `preview` |
| `vite.config.ts` | Cấu hình Vite: plugin React, alias đường dẫn (`@/` → `src/`), proxy API cho dev, tùy chọn build |
| `tsconfig.json` | File TypeScript gốc, kế thừa cấu hình từ `tsconfig.app.json` và `tsconfig.node.json` |
| `tsconfig.app.json` | Cấu hình TS cho mã nguồn ứng dụng (`src/`): strict mode, path aliases, JSX transform |
| `tsconfig.node.json` | Cấu hình TS cho file cấu hình Node.js (`vite.config.ts`): module system, target |
| `.oxlintrc.json` | Cấu hình Oxlint — linter cực nhanh viết bằng Rust, kiểm tra lỗi code và React hooks |
| `components.json` | Cấu hình shadcn/ui: thư mục đích, alias import, style framework |
| `.gitignore` | Danh sách file/thư mục không commit lên Git: `node_modules/`, `dist/`, `.env` |

---

## 5. Các File chính trong `src/`

| File | Chức năng |
|---|---|
| `main.tsx` | **Entrypoint** — Điểm bắt đầu của ứng dụng. Import `App.tsx`, wrap trong `StrictMode`, và render vào `#root` |
| `App.tsx` | **Component gốc** — Thiết lập các Provider (QueryClientProvider, BrowserRouter), cấu hình routing, và theme |
| `index.css` | **CSS toàn cục** — Import Tailwind CSS directives (`@import "tailwindcss"`), định nghĩa CSS variables cho theme, custom utilities |
| `App.css` | **CSS riêng** cho App component — Animation, transition, và style đặc thù của layout gốc |

---

## 6. Quy tắc Viết Code (Coding Guidelines)

### 📝 Quy tắc Đặt tên

| Loại | Quy tắc | Ví dụ |
|---|---|---|
| Thư mục | `kebab-case` | `elderly-management/`, `health-check/` |
| Component file | `PascalCase` | `UserCard.tsx`, `LoginForm.tsx` |
| Hook file | `camelCase` bắt đầu bằng `use` | `useAuth.ts`, `useDebounce.ts` |
| Hàm & biến | `camelCase` | `getUserInfo`, `isLoading`, `handleSubmit` |
| Type & Interface | `PascalCase` | `User`, `ApiResponse`, `LoginRequest` |
| Hằng số | `UPPER_SNAKE_CASE` | `MAX_RETRY`, `API_BASE_URL` |

### 🔄 Quản lý State

| Loại State | Công cụ | Khi nào dùng |
|---|---|---|
| State cục bộ component | `useState` / `useReducer` | Trạng thái mở/đóng modal, giá trị input tạm |
| Dữ liệu từ API (Server State) | **React Query** | Mọi dữ liệu fetch từ backend — tự động cache & re-fetch |
| State toàn cục client | **Zustand** | Thông tin user đăng nhập, theme, ngôn ngữ |
| Dữ liệu form | **React Hook Form** | Mọi form nhập liệu phức tạp |

### ✅ Validation

- Sử dụng **Zod** để định nghĩa schema validation cho form
- Schema Zod có thể dùng để sinh TypeScript type tự động (`z.infer<typeof schema>`)

### 🎨 Styling

- Viết Tailwind class trực tiếp trong `className`
- Dùng `cn()` từ `src/lib/utils.ts` khi cần ghép class có điều kiện
- **Không dùng** inline style (`style={{}}`) trừ trường hợp giá trị động (ví dụ: `width` tính từ JavaScript)

### 🚫 Lưu ý quan trọng

- **Không** sử dụng kiểu `any` trong TypeScript — luôn định nghĩa type rõ ràng
- **Không** gọi API trực tiếp trong component — đặt trong `services/` hoặc `features/*/services/`
- **Không** đặt component dùng chung vào `features/` — đưa lên `src/components/`

---

## 7. Scripts

```bash
# Khởi động dev server (http://localhost:5173)
npm run dev

# Build production (kiểm tra TypeScript + tạo bundle tối ưu)
npm run build

# Kiểm tra lỗi code bằng Oxlint
npm run lint

# Xem trước bản build production
npm run preview
```
