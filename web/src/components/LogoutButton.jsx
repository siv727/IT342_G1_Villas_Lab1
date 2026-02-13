import { useNavigate } from "react-router-dom";
import { logoutUser } from "@/api/authApi";
import { Button } from "@/components/ui/button";
import { LogOut } from "lucide-react";

/**
 * LogoutButton
 *
 * Props
 * -----
 * @param {() => void} onLogout – callback after logout (e.g. clear user state)
 */
export default function LogoutButton({ onLogout }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    logoutUser();
    if (onLogout) onLogout();
    navigate("/login");
  };

  return (
    <Button variant="outline" onClick={handleLogout} className="gap-2">
      <LogOut className="h-4 w-4" />
      Logout
    </Button>
  );
}
