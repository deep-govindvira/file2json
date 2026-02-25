import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { setupInterceptors } from "./api/authInterceptors";

import Login from "./pages/Login";
import Register from "./pages/Register";
import PublicRoute from "./routes/PublicRoute";
import Dashboard from "./pages/Dashboard";
import { useEffect } from "react";
import CreateProject from "./pages/CreateProject";
import ProtectedLayout from "./components/ProtectedLayout";
import ViewProject from "./components/ViewProject";
import ViewMarksheet from "./pages/ViewMarksheet";
import EditProject from "./pages/EditProject";

function App() {

  useEffect(() => {
    setupInterceptors();
  });

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
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/create-project" element={<CreateProject />} />
          <Route path="/project/:id/view" element={<ViewProject />} />
          <Route path="/project/:id/edit" element={<EditProject />} />
          <Route
            path="/project/:projectId/marksheet/:marksheetId/view"
            element={<ViewMarksheet />}
          />
        </Route>

        {/* Default Redirect */}
        <Route path="*" element={<Navigate to="/dashboard" />} />

      </Routes>
    </Router>
  );
}

export default App;
