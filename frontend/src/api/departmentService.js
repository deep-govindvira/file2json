import axiosInstance from "./axiosInstance";

export const getAllDepartments = async (projectId) => {
    const response = await axiosInstance.get(`/departments`);
    return response.data;
};