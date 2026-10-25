import { API_BASE_URL } from "@/common/config/api-url";
import axios from "axios";

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Optionally, you can add a response interceptor here later 
// to globally handle 401s or other standard API errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Extract the backend's error message so it's easier to catch
    if (error.response && error.response.data && error.response.data.message) {
      error.message = error.response.data.message;
    }
    return Promise.reject(error);
  }
);
