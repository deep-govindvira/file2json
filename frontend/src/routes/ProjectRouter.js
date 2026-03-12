import Cookies from "js-cookie";
import SuperAdminDashboard from "../pages/SuperAdminDashboard";
import ViewProjectList from "../pages/ViewProjectList";
import { Navigate } from "react-router-dom";
import VerifierDashboard from "../pages/VerifierDashboard";
import ViewProject from "../components/ViewProject";
import VerifierViewProject from "../components/VerifierViewProject";

const ProjectRouter = () => {
  const role = Cookies.get("role");

  if (!role) {
    return <Navigate to="/login" />;
  }

  if (role === "SUPER_ADMIN") {
    return <SuperAdminDashboard />;
  }

  if (role == "ADMIN") {
    return <ViewProject />;
  }

  return <VerifierViewProject />;

};

export default ProjectRouter;