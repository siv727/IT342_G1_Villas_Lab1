import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Link } from "react-router-dom";

/**
 * AuthForm – reusable for Login & Register.
 *
 * Props
 * -----
 * @param {"login"|"register"} mode
 * @param {(formData: object) => Promise<void>} onSubmit
 * @param {boolean} loading
 */
export default function AuthForm({ mode, onSubmit, loading }) {
  const isLogin = mode === "login";

  const [formData, setFormData] = useState(
    isLogin
      ? { email: "", password: "" }
      : {
          firstname: "",
          lastname: "",
          email: "",
          password: "",
          confirmPassword: "",
        }
  );

  const [error, setError] = useState("");

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError("");
  };

  const validate = () => {
    // Check empty fields
    for (const [key, value] of Object.entries(formData)) {
      if (!value.trim()) {
        const label = key === "confirmPassword" ? "Confirm Password" : key.charAt(0).toUpperCase() + key.slice(1);
        return `${label} is required.`;
      }
    }

    // Registration-specific: passwords must match
    if (!isLogin && formData.password !== formData.confirmPassword) {
      return "Password and Confirm Password do not match.";
    }

    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }
    try {
      await onSubmit(formData);
    } catch (err) {
      setError(
        err?.response?.data?.message ||
          err?.message ||
          "Something went wrong. Please try again."
      );
    }
  };

  return (
    <Card className="w-full max-w-md mx-auto">
      <CardHeader className="text-center">
        <CardTitle className="text-orange-700">
          {isLogin ? "Login" : "Register"}
        </CardTitle>
        <CardDescription>
          {isLogin
            ? "Enter your credentials to sign in"
            : "Create a new account"}
        </CardDescription>

        {/* Error message below title */}
        {error && (
          <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2 mt-2">
            {error}
          </p>
        )}
      </CardHeader>

      <form onSubmit={handleSubmit}>
        <CardContent className="space-y-4">
          {/* Registration-only fields */}
          {!isLogin && (
            <>
              <div className="space-y-2">
                <Label htmlFor="firstname">First Name</Label>
                <Input
                  id="firstname"
                  name="firstname"
                  placeholder="John"
                  value={formData.firstname}
                  onChange={handleChange}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="lastname">Last Name</Label>
                <Input
                  id="lastname"
                  name="lastname"
                  placeholder="Doe"
                  value={formData.lastname}
                  onChange={handleChange}
                />
              </div>
            </>
          )}

          {/* Shared fields */}
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              name="email"
              type="email"
              placeholder="you@example.com"
              value={formData.email}
              onChange={handleChange}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              name="password"
              type="password"
              placeholder="••••••••"
              value={formData.password}
              onChange={handleChange}
            />
          </div>

          {!isLogin && (
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirm Password</Label>
              <Input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={handleChange}
              />
            </div>
          )}
        </CardContent>

        <CardFooter className="flex flex-col gap-3">
          <Button type="submit" className="w-full" disabled={loading}>
            {loading
              ? "Please wait…"
              : isLogin
              ? "Sign In"
              : "Create Account"}
          </Button>

          <p className="text-sm text-orange-700">
            {isLogin ? (
              <>
                Don&apos;t have an account?{" "}
                <Link to="/register" className="font-semibold underline">
                  Register
                </Link>
              </>
            ) : (
              <>
                Already have an account?{" "}
                <Link to="/login" className="font-semibold underline">
                  Login
                </Link>
              </>
            )}
          </p>
        </CardFooter>
      </form>
    </Card>
  );
}
