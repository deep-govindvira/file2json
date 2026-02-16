import React, { useState } from "react";
import axios from "axios";
import { useSelector } from "react-redux";

const SERVER_URL = process.env.SERVER_URL || "http://localhost:8080";


const CreateProjectForm = () => {
    const userId = useSelector((state) => state.userId.value);


  const [formData, setFormData] = useState({
    projectName: "",
    projectDescription: "",
    projectYear: "",
  });
  const [errors, setErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState("");

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "" });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Basic frontend validation
    if (!formData.projectName) {
      setErrors({ projectName: "Project name is required" });
      return;
    }

    try {
      const response = await axios.post(
        `${SERVER_URL}/users/${userId}/projects`,
        {
          projectName: formData.projectName,
          projectDescription: formData.projectDescription,
          projectYear: formData.projectYear ? parseInt(formData.projectYear) : null,
        }
      );
      setSuccessMessage("Project created successfully!");
      setFormData({ projectName: "", projectDescription: "", projectYear: "" });
    } catch (err) {
      console.error(err);
      setErrors({ api: "Failed to create project. Please try again." });
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow-md">
      <h2 className="text-2xl font-semibold mb-4">Create Project</h2>
      {errors.api && <p className="text-red-500 mb-2">{errors.api}</p>}
      {successMessage && <p className="text-green-500 mb-2">{successMessage}</p>}
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block font-medium mb-1">Project Name *</label>
          <input
            type="text"
            name="projectName"
            value={formData.projectName}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          {errors.projectName && <p className="text-red-500 text-sm">{errors.projectName}</p>}
        </div>

        <div>
          <label className="block font-medium mb-1">Project Description</label>
          <textarea
            name="projectDescription"
            value={formData.projectDescription}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block font-medium mb-1">Project Year</label>
          <input
            type="number"
            name="projectYear"
            value={formData.projectYear}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <button
          type="submit"
          className="w-full bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 transition"
        >
          Create Project
        </button>
      </form>
    </div>
  );
};

export default CreateProjectForm;