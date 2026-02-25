import { useNavigate } from "react-router-dom";
import ProjectList from "../components/ProjectList";

function Dashboard() {
  const navigate = useNavigate();

  return (
    <div className="p-6 bg-gray-100 min-h-screen">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Projects</h1>
        <button
          onClick={() => navigate("/create-project")}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg shadow hover:bg-blue-700 transition-colors duration-300"
        >
          Create Project
        </button>
      </div>

      <ProjectList />
    </div>
  );
}

export default Dashboard;
