import axiosClient from "./axiosClient";

// GET api/user/{id}
export const getUserProfile = async (id) => {
  const response = await axiosClient.get(`/api/user/${id}`);
  return response.data;
};

// PUT api/user/{id}
export const updateUserProfile = async (id, userData) => {
  const response = await axiosClient.put(`/api/user/${id}`, userData);
  return response.data;
};
