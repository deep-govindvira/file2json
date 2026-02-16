import './App.css';

import { BrowserRouter, Routes, Route } from "react-router-dom"

import Home from "./pages/Home";
import Dashboard from "./pages/Dashboard";
import RegisterForm from './pages/RegisterForm';
import PublicRoute from './routes/PublicRoute';
import ProtectedRoute from './routes/ProtectedRoute';
import LoginForm from './pages/LoginForm';
import { useSelector } from 'react-redux';
import CreateProjectForm from './pages/CreateProjectForm';

function App() {
    const userId = useSelector((state) => state.userId.value);
      const isAuthenticated = !!userId;

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />

        <Route
          path="/register"
          element={
            <PublicRoute isAuthenticated={isAuthenticated}>
              <RegisterForm />
            </PublicRoute>
          }
        />

         <Route
          path="/login"
          element={
            <PublicRoute isAuthenticated={isAuthenticated}>
              <LoginForm />
            </PublicRoute>
          }
        />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute isAuthenticated={isAuthenticated}>
              <Dashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/create-project"
          element={
            <ProtectedRoute isAuthenticated={isAuthenticated}>
              <CreateProjectForm />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
