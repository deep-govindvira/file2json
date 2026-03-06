import axiosInstance from "./axiosInstance";

export const getUsers = async () => {
  const response = await axiosInstance.get("/users");
  return response.data;
};

export const getUsersByProject = async (projectId) => {
  const response = await axiosInstance.get(
    `${process.env.REACT_APP_SERVER_URL}/users/project/${projectId}`
  );

  return response.data;
};