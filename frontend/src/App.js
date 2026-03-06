import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { setupInterceptors } from "./api/authInterceptors";

import Login from "./pages/Login";
import Register from "./pages/Register";
import PublicRoute from "./routes/PublicRoute";
import { useEffect } from "react";
import CreateProject from "./pages/CreateProject";
import ProtectedLayout from "./components/ProtectedLayout";
import ViewProject from "./components/ViewProject";
import ViewMarksheet from "./pages/ViewMarksheet";
import EditProject from "./pages/EditProject";
import { ToastContainer } from "react-toastify";
import ViewProjectList from "./pages/ViewProjectList";
import EditMarksheet from "./pages/EditMarksheet";

function App() {

  useEffect(() => {
    setupInterceptors();
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
        <Route
          path="/register"
          element={
            <PublicRoute>
              <Register />
            </PublicRoute>
          }
        />

        {/* Protected Layout Wrapper */}
        <Route element={<ProtectedLayout />}>
          <Route path="/dashboard" element={<ViewProjectList />} />
          <Route path="/projects" element={<ViewProjectList />} />          <Route path="/create-project" element={<CreateProject />} />
          <Route path="/project/:id/view" element={<ViewProject />} />
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
        <Route path="*" element={<Navigate to="/dashboard" />} />

      </Routes>

      <ToastContainer position="bottom-right" autoClose={3000} />
    </Router>
  );
}

export default App;
