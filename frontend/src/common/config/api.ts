import axios from 'axios';
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { API_BASE_URL } from './api-url';
import { API_CONSTANTS } from './constants';

const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_CONSTANTS.TIMEOUT,
  headers: API_CONSTANTS.DEFAULT_HEADERS,
  withCredentials: true,
});

// Request interceptor: Attach JWT token if it exists in local storage
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem(API_CONSTANTS.TOKEN_KEY);
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor: Handle global API errors/responses
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error) => {
    if (error.response) {
      const { status } = error.response;
      if (status === 401) {
        // Clear token on Unauthorized response
        localStorage.removeItem(API_CONSTANTS.TOKEN_KEY);
        // Optional: Trigger logout redirect or state change
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
