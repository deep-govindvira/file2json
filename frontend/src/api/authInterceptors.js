import { refreshToken } from "./authService";
import axiosInstance from "./axiosInstance";
import Cookies from "js-cookie";

export const setupInterceptors = () => {
  // Request interceptor
  axiosInstance.interceptors.request.use(
    (config) => {
      const accessToken = Cookies.get("accessToken");
      if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  // Response interceptor
  axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;

      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          // Call refreshToken() which should read refresh token internally
          await refreshToken();
          originalRequest.headers.Authorization = `Bearer ${Cookies.get("accessToken")}`;
          return axiosInstance(originalRequest);
        } catch (err) {
          console.error("Token refresh failed:", err);
        }
      }

      return Promise.reject(error);
    }
  );
};
