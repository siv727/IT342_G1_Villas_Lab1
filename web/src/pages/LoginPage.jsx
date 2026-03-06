import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthForm from "@/components/AuthForm";
import { loginUser } from "@/api/authApi";

/**
 * LoginPage
 *
 * Props
 * -----
 * @param {(userId: string) => void} onLogin
 */
export default function LoginPage({ onLogin }) {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (formData) => {
    setLoading(true);
    try {
      const data = await loginUser({
        email: formData.email,
        password: formData.password,
      });

      // Tokens are stored as HttpOnly cookies by the server
      // Only userId is returned in the response body
      const userId = data.userId;

      localStorage.setItem("userId", userId);

      if (onLogin) onLogin(userId);

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
