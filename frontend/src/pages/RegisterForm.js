import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useDispatch } from "react-redux";
import { setUserId } from "../reducers/userIdSlice";

const SERVER_URL = process.env.SERVER_URL || "http://localhost:8080";

export default function RegisterForm() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    userName: "",
    email: "",
    password: "",
    department: ""
  });

   const dispatch = useDispatch();
  const [errors, setErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState("");

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    setSuccessMessage("");

    try {
      const response = await axios.post(`${SERVER_URL}/users/register`, formData);


      setSuccessMessage(`User ${response.data.userName} registered successfully!`);
      setFormData({ userName: "", email: "", password: "", department: "" });

      dispatch(setUserId(response.data.userId));
      
      navigate("/dashboard");
    } catch (err) {
      if (err.response && err.response.data) {
        setErrors(err.response.data);
      } else {
        setErrors({ general: "Server error. Please try again later." });
      }
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10 p-6 border rounded shadow-lg bg-white">
      <h2 className="text-2xl font-bold mb-4">Register User</h2>

      {errors.general && <p className="text-red-600 mb-2">{errors.general}</p>}
      {successMessage && <p className="text-green-600 mb-2">{successMessage}</p>}

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Username */}
        <div>
          <label className="block mb-1 font-semibold">Username</label>
          <input
            type="text"
            name="userName"
            value={formData.userName}
            onChange={handleChange}
            className={`w-full border px-3 py-2 rounded ${
              errors.userName ? "border-red-500" : "border-gray-300"
            }`}
          />
          {errors.userName && (
            <p className="text-red-500 text-sm mt-1">{errors.userName}</p>
          )}
        </div>

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

        {/* Department */}
        <div>
          <label className="block mb-1 font-semibold">Department</label>
          <input
            type="text"
            name="department"
            value={formData.department}
            onChange={handleChange}
            className={`w-full border px-3 py-2 rounded ${
              errors.department ? "border-red-500" : "border-gray-300"
            }`}
          />
          {errors.department && (
            <p className="text-red-500 text-sm mt-1">{errors.department}</p>
          )}
        </div>

        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
        >
          Register
        </button>
      </form>

      {/* Login Button */}
      <div className="mt-4 text-center">
        <p className="mb-2 text-gray-600">Already have an account?</p>
        <button
          onClick={() => navigate("/login")}
          className="px-6 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition"
        >
          Login
        </button>
      </div>
    </div>
  );
}