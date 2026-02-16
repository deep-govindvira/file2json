// src/components/PublicRoute.jsx
import { Navigate } from "react-router-dom";

const PublicRoute = ({ isAuthenticated, children }) => {
  if (isAuthenticated) {
    // If user already logged in, redirect away from login/signup pages
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

export default PublicRoute;