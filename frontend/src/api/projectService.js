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

export const addUserToProject = async (projectId, email) => {
  const response = await axiosInstance.put(`/projects/${projectId}/addUser`, { email });
  return response.data;
};

export const removeUserToProject = async (projectId, email) => {
  const response = await axiosInstance.put(`/projects/${projectId}/removeUser`, { email });
  return response.data;
};

export const updateProject = async (projectId, data) => {
  const response = await axiosInstance.put(`/projects/${projectId}`, data);
  return response.data;
};

