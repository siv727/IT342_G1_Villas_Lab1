import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthForm from "@/components/AuthForm";
import { loginUser } from "@/api/authApi";
import { getUserIdFromToken } from "@/lib/auth";

/**
 * LoginPage
 *
 * Props
 * -----
 * @param {(token: string, userId: string) => void} onLogin
 */
export default function LoginPage({ onLogin }) {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (formData) => {
    setLoading(true);
    try {
      const token = await loginUser({
        email: formData.email,
        password: formData.password,
      });

      // Decode userId from the JWT token
      const userId = getUserIdFromToken(token);

      // Persist token & userId
      localStorage.setItem("token", token);
      localStorage.setItem("userId", userId);

      if (onLogin) onLogin(token, userId);

      navigate("/profile");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-orange-50 px-4">
      <AuthForm mode="login" onSubmit={handleLogin} loading={loading} />
    </div>
  );
}
