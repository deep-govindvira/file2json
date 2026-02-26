import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getProjects } from "../api/projectService";

const ViewProjectList = () => {
    const [projects, setProjects] = useState([]);
    const navigate = useNavigate();

    // ================= FETCH PROJECTS =================
    useEffect(() => {
        const fetchProjects = async () => {
            try {
                const response = await getProjects();
                setProjects(response);
            } catch (error) {
                console.error("Error fetching projects:", error);
            }
        };

        fetchProjects();
    }, []);

    // ================= SSE PROJECT LISTENER =================
    useEffect(() => {

        const eventSource = new EventSource(
            `${process.env.REACT_APP_SERVER_URL}/api/stream`
        );

        eventSource.addEventListener("project-status-update", (event) => {
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
        });

        eventSource.onerror = (error) => {
            console.error("SSE error:", error);
        };

        return () => {
            eventSource.close();
        };

    }, []);

    return (
        <div className="grid gap-4 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
            {projects.map((project) => (
                <div
                    key={project.projectId}
                    onClick={() => navigate(`/project/${project.projectId}/view`)}
                    className="bg-white rounded-lg shadow p-5 hover:shadow-lg transition duration-300 cursor-pointer border"
                >
                    <h3 className="text-xl font-bold text-gray-900 mb-3">
                        {project.projectName}
                    </h3>

                    <div className="space-y-1 text-sm text-gray-700">
                        <p><strong>Project ID:</strong> {project.projectId}</p>
                        <p><strong>Description:</strong> {project.projectDescription}</p>
                        <p><strong>Year:</strong> {project.projectYear}</p>

                        <p>
                            <strong>Status:</strong>{" "}
                            <span
                                className={`font-semibold ${
                                    project.projectStatus === "UNPROCESSED"
                                        ? "text-red-500"
                                        : project.projectStatus === "COMPLETED"
                                        ? "text-green-600"
                                        : "text-yellow-600"
                                }`}
                            >
                                {project.projectStatus}
                            </span>
                        </p>

                        <p>
                            <strong>Total Marksheets:</strong> {project.totalMarksheets}
                        </p>

                        <p>
                            <strong>Processed Marksheets:</strong> {project.processedMarksheets}
                        </p>

                        <p>
                            <strong>Failed Marksheets:</strong> {project.processingFailedMarksheets}
                        </p>

                        <p>
                            <strong>Processing Duration:</strong>{" "}
                            {project.projectProcessingDuration ?? "Not processed yet"}
                        </p>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default ViewProjectList;