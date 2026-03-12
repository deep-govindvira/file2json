import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { setupInterceptors } from "./api/authInterceptors";

import Login from "./pages/Login";
import Register from "./pages/Register";
import PublicRoute from "./routes/PublicRoute";
import { useEffect, useState } from "react";
import CreateProject from "./pages/CreateProject";
import ProtectedLayout from "./components/ProtectedLayout";
import ViewProject from "./components/ViewProject";
import ViewMarksheet from "./pages/ViewMarksheet";
import EditProject from "./pages/EditProject";
import { ToastContainer } from "react-toastify";
import ViewProjectList from "./pages/ViewProjectList";
import EditMarksheet from "./pages/EditMarksheet";
import SuperAdminDashboard from "./pages/SuperAdminDashboard";
import ProtectedRoute from "./routes/ProtectedRoute";
import Cookies from "js-cookie";
import SuperAdminRoute from "./routes/SuperAdminRoute";
import DashboardRouter from "./routes/DashboardRouter";
import UserProfile from "./pages/UserProfile";
import AssignMarksheet from "./pages/AssignMarksheet";
import ProjectRouter from "./routes/ProjectRouter";

function App() {

  const [role, setRole] = useState(null);

  useEffect(() => {
    setupInterceptors();
    setRole(Cookies.get("role"));
  }, []);


  return (
    <Router>
      <Routes>

        {/* Public Routes */}
        <Route
          path="/login"
          element={
            <PublicRoute>
              <Login />
            </PublicRoute>
          }
        />
        {/* <Route
          path="/register"
          element={
            <PublicRoute>
              <Register />
            </PublicRoute>
          }
        /> */}

        {/* Protected Layout Wrapper */}
        <Route element={<ProtectedLayout />}>
          <Route path="/profile" element={<UserProfile />} />
          <Route path="/dashboard" element={<DashboardRouter />} />
          <Route path="/project/:id/assignMarksheets" element={<AssignMarksheet />} />
          <Route path="/projects" element={<DashboardRouter />} />
          <Route path="/create-project" element={<CreateProject />} />
          <Route path="/project/:id/view" element={<ProjectRouter />} />
          {/* <Route path="/project/:id/view" element={<ViewProject />} /> */}
          <Route path="/project/:id/edit" element={<EditProject />} />
          <Route
            path="/project/:projectId/marksheet/:marksheetId/view"
            element={<ViewMarksheet />}

          />
          <Route
            path="/project/:projectId/marksheet/:marksheetId/edit"
            element={<EditMarksheet />}
          />
        </Route>

        {/* Default Redirect */}
        <Route path="*" element={<Navigate to="/login" />} />

      </Routes>

      <ToastContainer position="bottom-right" autoClose={3000} />
    </Router>
  );
}

export default App;
