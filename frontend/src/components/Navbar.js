import { useNavigate, Link } from "react-router-dom";
import { logout } from "../api/authService";

function Navbar() {
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            await logout();
        } catch (error) {
            console.error("Logout failed:", error);
        }
        navigate("/login");
    };

    return (
        <nav className="flex justify-between items-center px-8 py-4 bg-slate-800 text-white">
            <h2 className="text-xl font-semibold">
                Marksheet Management System
            </h2>

            <div className="flex items-center gap-6">
                <Link
                    to="/dashboard"
                    className="hover:text-slate-300 font-medium"
                >
                    Projects
                </Link>

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