import Cookies from "js-cookie";
import SuperAdminDashboard from "../pages/SuperAdminDashboard";
import ViewProjectList from "../pages/ViewProjectList";
import { Navigate } from "react-router-dom";
import VerifierDashboard from "../pages/VerifierDashboard";

const DashboardRouter = () => {
  const role = Cookies.get("role");

  if (!role) {
    return <Navigate to="/login" />;
  }

  if (role === "SUPER_ADMIN") {
    return <SuperAdminDashboard />;
  }

  if (role == "ADMIN") {
    return <ViewProjectList />;
  }

  return <VerifierDashboard />;

};

export default DashboardRouter;