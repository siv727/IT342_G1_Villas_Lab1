import axiosClient from "./axiosClient";

// POST api/auth/login – tokens are set as HttpOnly cookies by the server
// Returns { userId, message }
export const loginUser = async (credentials) => {
  const response = await axiosClient.post("/api/auth/login", credentials);
  return response.data;
};

// POST api/auth/register – tokens are set as HttpOnly cookies by the server
// Returns { userId, message }
export const registerUser = async (userData) => {
  const response = await axiosClient.post("/api/auth/register", userData);
  return response.data;
};

// POST api/auth/refresh – refresh token is sent automatically via cookie
export const refreshAccessToken = async () => {
  const response = await axiosClient.post("/api/auth/refresh");
  return response.data;
};

// POST api/auth/logout – blacklists token server-side and clears cookies
export const logoutUser = async () => {
  try {
    await axiosClient.post("/api/auth/logout");
  } catch {
    // Even if the server call fails, clear local storage
  } finally {
    localStorage.removeItem("userId");
  }
};
