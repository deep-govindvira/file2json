import { Navigate } from "react-router-dom";
import Cookies from "js-cookie";
import { useState, useEffect } from "react";

const SuperAdminRoute = ({ children }) => {
    const role = !!Cookies.get("role");

    if (!role) {
        Cookies.remove("accessToken");
        Cookies.remove("refreshToken");
        Cookies.remove("role");
        Cookies.remove("email");
        return <Navigate to="/login" replace />;

    }
    
    const isSuperAdmin = role === "SUPER_ADMIN";

    if (!isSuperAdmin) {
        Cookies.remove("accessToken");
        Cookies.remove("refreshToken");
        Cookies.remove("role");
        Cookies.remove("email");
        return <Navigate to="/login" replace />;
    }

    return children;
};

export default SuperAdminRoute;