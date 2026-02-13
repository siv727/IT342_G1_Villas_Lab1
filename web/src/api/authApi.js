import axiosClient from "./axiosClient";

// POST api/auth/login – response is just the token string
export const loginUser = async (credentials) => {
  const response = await axiosClient.post("/api/auth/login", credentials);
  // Handle both plain string and { token } responses
  return typeof response.data === "string" ? response.data : response.data.token;
};

// POST api/auth/register – response is just the token string
export const registerUser = async (userData) => {
  const response = await axiosClient.post("/api/auth/register", userData);
  return typeof response.data === "string" ? response.data : response.data.token;
};

// POST api/auth/logout (client-side only – clears token)
export const logoutUser = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
};
