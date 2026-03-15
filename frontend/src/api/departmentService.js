import axiosInstance from "./axiosInstance";

export const getAllDepartments = async (projectId) => {
    const response = await axiosInstance.get(`/departments`);
    return response.data;
};

export const createDepartment = async (name) => {
  const res = await axiosInstance.post('/departments', { name });
  return res.data;
};

export const deleteDepartment = async (id) => {
  await axiosInstance.delete(`/departments/${id}`);
};