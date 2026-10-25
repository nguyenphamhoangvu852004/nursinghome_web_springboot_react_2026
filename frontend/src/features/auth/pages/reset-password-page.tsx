import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useNavigate } from "react-router";
import { authService, type ResetPasswordRequest } from "../services/auth-service";

const resetSchema = z.object({
  email: z.string().email("Invalid email address").min(1, "Email is required"),
});

export const ResetPasswordPage = () => {
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetPasswordRequest>({
    resolver: zodResolver(resetSchema),
  });

  const onSubmit = async (data: ResetPasswordRequest) => {
    setLoading(true);
    setError(null);
    try {
      await authService.ResetPassword(data);
      setSuccess(true);
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to reset password.");
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
              Reset Password
            </h2>
            <p className="text-sm text-gray-500 mt-1">
              Enter your email to receive a reset link
            </p>
          </div>

          {!success ? (
            <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
              {error && (
                <div className="text-red-600 text-sm text-center bg-red-50 p-2 rounded">
                  {error}
                </div>
              )}
              
              <div>
                <label
                  htmlFor="email"
                  className="block text-sm font-semibold text-gray-700"
                >
                  Email address
                </label>
                <div className="mt-1">
                  <Input
                    id="email"
                    type="email"
                    placeholder="name@facility.org"
                    {...register("email")}
                    className={errors.email ? "border-red-500" : ""}
                  />
                  {errors.email && (
                    <p className="mt-1 text-sm text-red-600">
                      {errors.email.message}
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
                  {loading ? "Sending..." : "Send Reset Link"}
                </Button>
              </div>
            </form>
          ) : (
            <div className="text-center">
              <p className="text-sm text-gray-700 mb-6">
                If an account matches that email, we have sent a password reset link.
              </p>
              <Button
                onClick={() => navigate("/login")}
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 h-10"
              >
                Return to Sign In
              </Button>
            </div>
          )}

          <div className="mt-8 pt-6 border-t border-gray-100 flex justify-center">
            <a
              href="#"
              onClick={(e) => { e.preventDefault(); navigate("/login"); }}
              className="text-sm font-semibold text-blue-600 hover:text-blue-500"
            >
              Back to Sign In
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResetPasswordPage;
