import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useDispatch } from "react-redux";
import { setUserId } from "../reducers/userIdSlice";

const SERVER_URL = process.env.SERVER_URL || "http://localhost:8080";

export default function LoginForm() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: "",
    password: ""
  });

   const dispatch = useDispatch();
  const [errors, setErrors] = useState({});
  const [generalError, setGeneralError] = useState("");

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    setGeneralError("");

    try {
      const response = await axios.post(`${SERVER_URL}/users/login`, formData);
      const data = response.data;

      // Store user info in localStorage
      dispatch(setUserId(data.userId));
      // Navigate to dashboard
      navigate("/dashboard");
    } catch (err) {
      if (err.response && err.response.data) {
        setErrors(err.response.data);
        setGeneralError(err.response.data.message || "Login failed");
      } else {
        setGeneralError("Server error. Please try again later.");
      }
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10 p-6 border rounded shadow-lg bg-white">
      <h2 className="text-2xl font-bold mb-4">Login</h2>

      {generalError && <p className="text-red-600 mb-2">{generalError}</p>}

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Email */}
        <div>
          <label className="block mb-1 font-semibold">Email</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            className={`w-full border px-3 py-2 rounded ${
              errors.email ? "border-red-500" : "border-gray-300"
            }`}
          />
          {errors.email && (
            <p className="text-red-500 text-sm mt-1">{errors.email}</p>
          )}
        </div>

        {/* Password */}
        <div>
          <label className="block mb-1 font-semibold">Password</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            className={`w-full border px-3 py-2 rounded ${
              errors.password ? "border-red-500" : "border-gray-300"
            }`}
          />
          {errors.password && (
            <p className="text-red-500 text-sm mt-1">{errors.password}</p>
          )}
        </div>

        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
        >
          Login
        </button>
      </form>

      {/* Register Button */}
      <div className="mt-4 text-center">
        <p className="text-gray-600 mb-2">Don't have an account?</p>
        <button
          onClick={() => navigate("/register")}
          className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 transition"
        >
          Register
        </button>
      </div>
    </div>
  );
}