import { createBrowserRouter, Navigate } from "react-router";
import { adminRoutes } from "./admin-routes";
import { LoginPage } from "@/features/auth/pages/login-page";
import { ResetPasswordPage } from "@/features/auth/pages/reset-password-page";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/login" replace />,
  },
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/forgot-password",
    element: <ResetPasswordPage />,
  },
  adminRoutes,
]);
