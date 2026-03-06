import { useNavigate, NavLink } from "react-router-dom";
import { logout } from "../api/authService";
import { toast } from "react-toastify";

function Navbar() {
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            await logout();
        } catch (error) {
            toast.error("Logout failed");
            console.error("Logout failed:", error);
        }
        navigate("/login");
    };

    return (
        <nav className="flex justify-between items-center px-8 py-4 bg-slate-800 text-white shadow-md">
            <h2 className="text-xl tracking-wide">
                Marksheet Management System
            </h2>

            <div className="flex items-center gap-4">
                <NavLink
                    to="/dashboard"
                    className={({ isActive }) =>
                        isActive
                            ? "px-3 py-2 bg-slate-700 rounded-md text-white transition"
                            : "px-3 py-2 text-slate-300 hover:text-white hover:bg-slate-700 rounded-md transition"
                    }
                >
                    Projects
                </NavLink>

                <button
                    onClick={handleLogout}
                    className="px-4 py-2 bg-red-500 hover:bg-red-600 rounded-md transition duration-200"
                >
                    Logout
                </button>
            </div>
        </nav>
    );
}

export default Navbar;