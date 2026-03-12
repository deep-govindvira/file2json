import axios from "axios";
import axiosInstance from "./axiosInstance";
import Cookies from "js-cookie";

export const register = async (data) => {
  const response = await axiosInstance.post("/api/auth/register", data);
  Cookies.set("accessToken", response.data.accessToken);
  Cookies.set("refreshToken", response.data.refreshToken);
  Cookies.set("email", data.email);
  return response.data;
};

export const login = async (data) => {
  const response = await axiosInstance.post("/api/auth/login", data);
  Cookies.set("accessToken", response.data.accessToken);
  Cookies.set("refreshToken", response.data.refreshToken);
  Cookies.set("role", response.data.role);
  Cookies.set("email", data.email);
  return response.data;
};


export const refreshToken = async () => {
  const response = await axios.post(`${process.env.REACT_APP_SERVER_URL}/api/auth/refresh`, { token: Cookies.get("refreshToken") }, { withCredentials: true });
  Cookies.set("accessToken", response.data.accessToken);
  return response.data;
};

export const logout = async () => {
  try {
    await axiosInstance.post("/api/auth/logout", { token: Cookies.get("refreshToken") });
  } catch (error) {
    console.error("Logout request failed:", error);
  } finally {
    Cookies.remove("refreshToken");
    Cookies.remove("accessToken");
    Cookies.remove("email");
    Cookies.remove("role");
  }
};


export const registerSuperAdmin = async (data) => {
  const response = await axiosInstance.post("/superadmin/registerAdmin", data);
  return response.data;
};

export const getAdmins = async () => {
  const response = await axiosInstance.get("/superadmin/admins");
  return response.data;
};


export const updateProfile = async (data) => {
  const response = await axiosInstance.put("/users", data);
  return response.data;
};


export const getUserInfo = async () => {
  const response = await axiosInstance.get("/users");
  return response.data;
}