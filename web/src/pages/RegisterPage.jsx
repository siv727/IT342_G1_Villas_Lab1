import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthForm from "@/components/AuthForm";
import { registerUser } from "@/api/authApi";

export default function RegisterPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleRegister = async (formData) => {
    setLoading(true);
    try {
      await registerUser({
        firstname: formData.firstname,
        lastname: formData.lastname,
        email: formData.email,
        password: formData.password,
      });

      // After successful registration, redirect to login
      navigate("/login");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-orange-50 px-4">
      <AuthForm mode="register" onSubmit={handleRegister} loading={loading} />
    </div>
  );
}
