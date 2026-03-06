import { useState } from "react";
import { Routes, Route, Navigate } from "react-router-dom";

import LoginPage from "@/pages/LoginPage";
import RegisterPage from "@/pages/RegisterPage";
import ProfilePage from "@/pages/ProfilePage";
import ProfileUpdatePage from "@/pages/ProfileUpdatePage";

function App() {
  const [userId, setUserId] = useState(localStorage.getItem("userId"));

  const handleLogin = (newUserId) => {
    setUserId(newUserId);
  };

  const handleLogout = () => {
    setUserId(null);
  };

  return (
    <Routes>
      <Route
        path="/login"
        element={<LoginPage onLogin={handleLogin} />}
      />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/profile"
        element={
          <ProfilePage
            userId={userId}
            onLogout={handleLogout}
          />
        }
      />
      <Route
        path="/profile/update"
        element={
          <ProfileUpdatePage
            userId={userId}
            onLogout={handleLogout}
          />
        }
      />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;
