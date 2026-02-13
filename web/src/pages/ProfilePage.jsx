import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getUserProfile } from "@/api/profileApi";
import LogoutButton from "@/components/LogoutButton";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Pencil } from "lucide-react";

/**
 * ProfilePage – displays the authenticated user's profile.
 *
 * Props
 * -----
 * @param {string|null} token
 * @param {string|null} userId
 * @param {() => void}  onLogout
 */
export default function ProfilePage({ token, userId, onLogout }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    if (!token || !userId) {
      navigate("/login");
      return;
    }

    const fetchProfile = async () => {
      try {
        const data = await getUserProfile(userId);
        setUser(data);
      } catch (err) {
        setError(err?.response?.data?.message || "Failed to load profile.");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [token, userId, navigate]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-orange-50">
        <p className="text-orange-600 text-lg">Loading profile…</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-orange-50 px-4">
        <Card className="w-full max-w-md text-center">
          <CardContent className="p-6">
            <p className="text-red-600">{error}</p>
            <LogoutButton onLogout={onLogout} />
          </CardContent>
        </Card>
      </div>
    );
  }

  const initials =
    (user?.firstname?.[0] || "") + (user?.lastname?.[0] || "") || "U";

  return (
    <div className="min-h-screen flex items-center justify-center bg-orange-50 px-4">
      <Card className="w-full max-w-md">
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle className="text-orange-700">My Profile</CardTitle>
          <LogoutButton onLogout={onLogout} />
        </CardHeader>

        <CardContent className="flex flex-col items-center gap-6">
          {/* Default profile picture (avatar with initials) */}
          <Avatar className="h-28 w-28">
            <AvatarFallback className="text-3xl">
              {initials.toUpperCase()}
            </AvatarFallback>
          </Avatar>

          <div className="w-full space-y-3 text-sm">
            <div className="flex justify-between border-b border-orange-100 pb-2">
              <span className="font-medium text-orange-800">First Name</span>
              <span className="text-orange-950">{user?.firstname}</span>
            </div>
            <div className="flex justify-between border-b border-orange-100 pb-2">
              <span className="font-medium text-orange-800">Last Name</span>
              <span className="text-orange-950">{user?.lastname}</span>
            </div>
            <div className="flex justify-between border-b border-orange-100 pb-2">
              <span className="font-medium text-orange-800">Email</span>
              <span className="text-orange-950">{user?.email}</span>
            </div>
          </div>

          <Button
            variant="secondary"
            className="w-full gap-2"
            onClick={() => navigate("/profile/update")}
          >
            <Pencil className="h-4 w-4" />
            Edit Profile
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
