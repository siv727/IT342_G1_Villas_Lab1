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

  // Password rules for registration
  const passwordRules = !isLogin
    ? [
        { label: "At least 8 characters", test: (pw) => pw.length >= 8 },
        { label: "One uppercase letter", test: (pw) => /[A-Z]/.test(pw) },
        { label: "One lowercase letter", test: (pw) => /[a-z]/.test(pw) },
        { label: "One digit", test: (pw) => /[0-9]/.test(pw) },
        { label: "One special character", test: (pw) => /[^a-zA-Z0-9]/.test(pw) },
      ]
    : [];

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

    // Registration-specific: password rules
    if (!isLogin) {
      const failedRules = passwordRules.filter((rule) => !rule.test(formData.password));
      if (failedRules.length > 0) {
        const labels = failedRules.map((r) => r.label.toLowerCase());
        const list =
          labels.length === 1
            ? labels[0]
            : labels.slice(0, -1).join(", ") + ", and " + labels[labels.length - 1];
        return `Password does not meet the required criteria. It must have ${list}.`;
      }

      if (formData.password !== formData.confirmPassword) {
        return "Password and Confirm Password do not match.";
      }
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
            {/* Password rules checklist (register only, shown once user starts typing) */}
            {!isLogin && formData.password.length > 0 && (
              <ul className="mt-1 space-y-0.5 text-xs">
                {passwordRules.map((rule) => {
                  const passed = rule.test(formData.password);
                  return (
                    <li
                      key={rule.label}
                      className={passed ? "text-green-600" : "text-orange-500"}
                    >
                      {passed ? "✓" : "○"} {rule.label}
                    </li>
                  );
                })}
              </ul>
            )}
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
