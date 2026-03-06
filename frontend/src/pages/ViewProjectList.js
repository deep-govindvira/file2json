import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getProjects } from "../api/projectService";
import { connectSSE } from "../api/sseClient";
import { toast } from "react-toastify";
import CenteredFullPageSpinner from "../components/CenteredFullPageSpinner";
import NotFoundCard from "../components/NotFoundCard";
import BlueButton from "../components/BlueButton";

const projectStatusConfig = {
    COMPLETED: {
        label: "COMPLETED",
        style: "bg-green-100 text-green-600",
    },
    PROCESSING: {
        label: "PROCESSING",
        style: "bg-yellow-100 text-yellow-700",
    },
    PARTIALLY_COMPLETED: {
        label: "PARTIALLY COMPLETED",
        style: "bg-blue-100 text-blue-700",
    },
    UNPROCESSED: {
        label: "UNPROCESSED",
        style: "bg-red-100 text-red-600",
    },
};

const ViewProjectList = () => {
    const [projects, setProjects] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();

    // ================= FETCH PROJECTS =================
    useEffect(() => {
        const fetchProjects = async () => {
            try {
                const response = await getProjects();
                setProjects(response);
            } catch (error) {
                toast.error("Failed to load projects");
                toast.info("Please log in again");
                console.error("Error fetching projects:", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchProjects();
    }, []);

    // ================= SSE PROJECT LISTENER =================
    useEffect(() => {
        const eventSource = connectSSE();

        const handleSSEProjectUpdate = (event) => {
            const data = JSON.parse(event.data);

            setProjects((prevProjects) =>
                prevProjects.map((project) =>
                    project.projectId === data.projectId
                        ? {
                            ...project,
                            projectStatus: data.projectStatus,
                            projectProcessingDuration:
                                data.projectProcessingDuration,
                            processedMarksheets: data.processedMarksheets,
                            processingFailedMarksheets:
                                data.processingFailedMarksheets,
                            totalMarksheets: data.totalMarksheets
                        }
                        : project
                )
            );
        };

        eventSource.addEventListener("project-status-update", handleSSEProjectUpdate);

        eventSource.onerror = (error) => {
            console.error("SSE error:", error);
        };

        return () => {
            eventSource.removeEventListener("project-status-update", handleSSEProjectUpdate);
        };

    }, []);

    if (isLoading) return <CenteredFullPageSpinner message="Loading projects, please wait..." />;

    if (!projects) {
        return (
            <NotFoundCard
                title="Projects Not Found"
                message="Please login again to continue."
                showButton={false}
            />
        );
    }

    return (
        <div className="px-8 py-10 bg-gray-50 min-h-screen">
            {/* ================= HEADER SECTION ================= */}
            <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-10">

                <div>
                    <h1 className="text-2xl tracking-wider text-gray-900">
                        Projects
                    </h1>
                    {/* <h1 className="text-4xl text-gray-900 tracking-tight">
                        Projects
                    </h1> */}
                    {/* <p className="text-gray-500 mt-1 text-sm">
                        Manage and monitor all your processing projects
                    </p> */}
                </div>

                <BlueButton label="New Project" onClick={() => navigate("/create-project")} />

                {/* <button
                    onClick={() => navigate("/create-project")}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-xl font-medium shadow-md hover:shadow-lg transition-all duration-300"
                >
                    Create Project
                </button> */}
            </div>



            <div className="grid gap-6 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
                {projects.map((project) => {
                    const getStatusStyle = () => {
                        return projectStatusConfig[project.projectStatus].style
                    };

                    const unprocessed =
                        (project.totalMarksheets || 0) -
                        (project.processingFailedMarksheets || 0) -
                        (project.processedMarksheets || 0);


                    return (
                        <div
                            key={project.projectId}
                            onClick={() =>
                                navigate(`/project/${project.projectId}/view`)
                            }
                            className="bg-white rounded-2xl border border-gray-200 p-6 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 cursor-pointer"
                        >
                            {/* Header */}
                            <div className="flex justify-between items-start mb-4">
                                <h3 className="text-lg font-semibold text-gray-900">
                                    {project.projectName}
                                </h3>

                                <span
                                    className={`px-3 py-1 text-xs font-semibold rounded-full ${getStatusStyle()}`}
                                >
                                    {projectStatusConfig[project.projectStatus].label}
                                </span>
                            </div>

                            {/* Description */}
                            <p className="text-sm text-gray-500 mb-4 line-clamp-2">
                                {project.projectDescription}
                            </p>

                            {/* Info */}
                            <div className="text-sm text-gray-600 mb-4 space-y-1">
                                {/* <p><span className="font-medium">Project ID:</span> {project.projectId}</p> */}
                                <p><span className="font-medium">Year:</span> {project.projectYear}</p>
                                <p>
                                    <span className="font-medium">Processing Duration:</span>{" "}
                                    {project.projectProcessingDuration ?? "Not processed yet"}
                                </p>
                            </div>

                            {/* Stats Section */}
                            <div className="grid grid-cols-4 gap-3 mt-4">
                                <div className="bg-gray-50 rounded-lg p-3 text-center">
                                    <p className="text-xs text-gray-500">Total</p>
                                    <p className="text-lg font-bold text-gray-800">
                                        {project.totalMarksheets}
                                    </p>
                                </div>

                                <div className="bg-green-50 rounded-lg p-3 text-center">
                                    <p className="text-xs text-green-600">Processed</p>
                                    <p className="text-lg font-bold text-green-700">
                                        {project.processedMarksheets}
                                    </p>
                                </div>

                                <div className="bg-red-50 rounded-lg p-3 text-center">
                                    <p className="text-xs text-red-600">Failed</p>
                                    <p className="text-lg font-bold text-red-700">
                                        {project.processingFailedMarksheets}
                                    </p>
                                </div>

                                <div className="bg-yellow-50 rounded-lg p-3 text-center">
                                    <p className="text-xs text-yellow-700">Unprocessed</p>
                                    <p className="text-lg font-bold text-yellow-800">
                                        {unprocessed >= 0 ? unprocessed : 0}
                                    </p>
                                </div>

                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default ViewProjectList;