import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createProject } from "../api/projectService";

function CreateProject() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    projectName: "",
    projectDescription: "",
    projectYear: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.projectName.trim()) {
      return; // no alert, just stop
    }

    try {
      const payload = {
        ...formData,
        projectYear: formData.projectYear
          ? parseInt(formData.projectYear)
          : undefined,
      };

      const data = await createProject(payload);

      // Redirect to /project/{projectId}
      navigate(`/project/${data.projectId}`);

      // Optional: reset form (not really needed since redirect happens)
      setFormData({
        projectName: "",
        projectDescription: "",
        projectYear: "",
      });
    } catch (error) {
      console.error("Failed to create project", error);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
      <form
        onSubmit={handleSubmit}
        className="bg-white shadow-lg rounded-xl p-8 w-full max-w-md space-y-6"
      >
        <h2 className="text-2xl font-bold text-gray-800 text-center">
          Create Project
        </h2>

        {/* Project Name */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Project Name
          </label>
          <input
            type="text"
            name="projectName"
            value={formData.projectName}
            onChange={handleChange}
            placeholder="Enter project name"
            maxLength={100}
            required
            className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
          />
        </div>

        {/* Project Description */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Project Description
          </label>
          <textarea
            name="projectDescription"
            value={formData.projectDescription}
            onChange={handleChange}
            rows="3"
            placeholder="Enter project description"
            className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
          />
        </div>

        {/* Project Year */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Project Year
          </label>
          <input
            type="number"
            name="projectYear"
            value={formData.projectYear}
            onChange={handleChange}
            placeholder="e.g. 2025"
            className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
          />
        </div>

        {/* Submit Button */}
        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition duration-200 font-semibold"
        >
          Create Project
        </button>
      </form>
    </div>
  );
}

export default CreateProject;