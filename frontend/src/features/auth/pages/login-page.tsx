import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useNavigate } from "react-router";
import { authService, type LoginRequest } from "../services/auth-service";

const loginSchema = z.object({
  username: z.string().min(1, "Email or Phone is required"),
  password: z.string().min(1, "Password is required"),
});

export const LoginPage = () => {
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginRequest>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginRequest) => {
    setLoading(true);
    setError(null);
    try {
      const response = await authService.Login(data);
      // In a real app, save token to context/Zustand and localStorage
      localStorage.setItem("token", response.token);
      localStorage.setItem("eldcare_token", response.token);
      navigate("/admin"); // Redirect to dashboard
    } catch (err: any) {
      setError(err.response?.data?.message || "Invalid credentials. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10 border border-gray-200">
          <div className="sm:mx-auto sm:w-full sm:max-w-md mb-6 text-center flex flex-col items-center">
            <div className="h-12 w-12 rounded-lg bg-blue-600 text-white flex items-center justify-center text-2xl font-bold mb-4">
              N
            </div>
            <h2 className="text-2xl font-bold text-gray-900 tracking-tight">
              Sign in to NHMS
            </h2>
            <p className="text-sm text-gray-500 mt-1">
              Nursing Home Management System
            </p>
          </div>

          <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
            {error && (
              <div className="text-red-600 text-sm text-center bg-red-50 p-2 rounded">
                {error}
              </div>
            )}
            
            <div>
              <label
                htmlFor="username"
                className="block text-sm font-semibold text-gray-700"
              >
                Email or Phone
              </label>
              <div className="mt-1">
                <Input
                  id="username"
                  type="text"
                  placeholder="name@facility.org or +1 555 000 1234"
                  {...register("username")}
                  className={errors.username ? "border-red-500" : ""}
                />
                {errors.username && (
                  <p className="mt-1 text-sm text-red-600">
                    {errors.username.message}
                  </p>
                )}
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between">
                <label
                  htmlFor="password"
                  className="block text-sm font-semibold text-gray-700"
                >
                  Password
                </label>
                <div className="text-sm">
                  <a
                    href="#"
                    onClick={(e) => { e.preventDefault(); navigate("/forgot-password"); }}
                    className="font-semibold text-blue-600 hover:text-blue-500"
                  >
                    Forgot password?
                  </a>
                </div>
              </div>
              <div className="mt-1">
                <Input
                  id="password"
                  type="password"
                  placeholder="••••••••••"
                  {...register("password")}
                  className={errors.password ? "border-red-500" : ""}
                />
                {errors.password && (
                  <p className="mt-1 text-sm text-red-600">
                    {errors.password.message}
                  </p>
                )}
              </div>
            </div>

            <div>
              <Button
                type="submit"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 h-10"
                disabled={loading}
              >
                {loading ? "Signing in..." : "Sign In"}
              </Button>
            </div>
          </form>

          <div className="mt-8 pt-6 border-t border-gray-100">
            <p className="text-xs text-center text-gray-500">
              Accounts are provisioned by your administrator.<br />
              Need access? Contact your NHMS admin.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
