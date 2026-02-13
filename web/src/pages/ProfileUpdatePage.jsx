import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getUserProfile, updateUserProfile } from "@/api/profileApi";
import LogoutButton from "@/components/LogoutButton";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";

/**
 * ProfileUpdatePage – allows the user to update their profile.
 *
 * Props
 * -----
 * @param {string|null} token
 * @param {string|null} userId
 * @param {() => void}  onLogout
 */
export default function ProfileUpdatePage({ token, userId, onLogout }) {
  const [formData, setFormData] = useState({
    firstname: "",
    lastname: "",
    email: "",
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    if (!token || !userId) {
      navigate("/login");
      return;
    }

    const fetchProfile = async () => {
      try {
        const data = await getUserProfile(userId);
        setFormData({
          firstname: data.firstname || "",
          lastname: data.lastname || "",
          email: data.email || "",
        });
      } catch (err) {
        setError(err?.response?.data?.message || "Failed to load profile.");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [token, userId, navigate]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError("");
    setSuccess("");
  };

  const validate = () => {
    for (const [key, value] of Object.entries(formData)) {
      if (!value.trim()) {
        const label = key.charAt(0).toUpperCase() + key.slice(1);
        return `${label} is required.`;
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

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await updateUserProfile(userId, formData);
      setSuccess("Profile updated successfully!");
    } catch (err) {
      setError(
        err?.response?.data?.message ||
          err?.message ||
          "Failed to update profile."
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-orange-50">
        <p className="text-orange-600 text-lg">Loading profile…</p>
      </div>
    );
  }

  const initials =
    (formData.firstname?.[0] || "") + (formData.lastname?.[0] || "") || "U";

  return (
    <div className="min-h-screen flex items-center justify-center bg-orange-50 px-4">
      <Card className="w-full max-w-md">
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle className="text-orange-700">Update Profile</CardTitle>
          <LogoutButton onLogout={onLogout} />
        </CardHeader>

        <form onSubmit={handleSubmit}>
          <CardContent className="space-y-4">
            {/* Avatar */}
            <div className="flex justify-center">
              <Avatar className="h-24 w-24">
                <AvatarFallback className="text-2xl">
                  {initials.toUpperCase()}
                </AvatarFallback>
              </Avatar>
            </div>

            {/* Error / Success messages */}
            {error && (
              <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">
                {error}
              </p>
            )}
            {success && (
              <p className="text-sm text-green-700 bg-green-50 border border-green-200 rounded-md px-3 py-2">
                {success}
              </p>
            )}

            <div className="space-y-2">
              <Label htmlFor="firstname">First Name</Label>
              <Input
                id="firstname"
                name="firstname"
                value={formData.firstname}
                onChange={handleChange}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="lastname">Last Name</Label>
              <Input
                id="lastname"
                name="lastname"
                value={formData.lastname}
                onChange={handleChange}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
              />
            </div>
          </CardContent>

          <CardFooter className="flex flex-col gap-3">
            <Button type="submit" className="w-full" disabled={saving}>
              {saving ? "Saving…" : "Save Changes"}
            </Button>
            <Button
              type="button"
              variant="ghost"
              className="w-full"
              onClick={() => navigate("/profile")}
            >
              Cancel
            </Button>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
}
