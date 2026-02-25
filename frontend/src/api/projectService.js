import axiosInstance from "./axiosInstance";

export const createProject = async (projectData) => {
  const response = await axiosInstance.post(`/projects`, projectData);
  return response.data;
};

export const getProjects = async () => {
  const response = await axiosInstance.get(`/projects`);
  return response.data;
};

export const getProjectById = async (projectId) => {
  const response = await axiosInstance.get(`/projects/${projectId}`);
  return response.data;
};

