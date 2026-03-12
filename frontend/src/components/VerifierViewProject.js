import { useEffect, useState, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getMarksheetsForVerifier } from "../api/marksheetService";
import { getProjectByIdForVerifier } from "../api/projectService";
import BlueButton from "./BlueButton";

import { CircularProgressbar, buildStyles } from "react-circular-progressbar";
import "react-circular-progressbar/dist/styles.css";

function StatCard({ title, value }) {
    return (
        <div className="border rounded-lg p-4 bg-white shadow-sm flex flex-col">
            <span className="text-xl text-gray-500">{title}</span>
            <span className="text-2xl font-semibold text-gray-800">{value}</span>
        </div>
    );
}

function CompletionCard({ percent }) {
    return (
        <div className="border rounded-lg p-4 bg-white shadow-sm flex flex-col items-center justify-center">
            <span className="text-sm text-gray-500 mb-3">Completion</span>

            <div className="w-20 h-20">
                <CircularProgressbar
                    value={percent}
                    text={`${percent.toFixed(0)}%`}
                    styles={buildStyles({
                        textSize: "28px",
                        pathColor: "#06b6d4",
                        textColor: "#374151",
                        trailColor: "#e5e7eb",
                    })}
                />
            </div>
        </div>
    );
}

function MarksheetGrid({ list, projectId, navigate }) {
    return (
        <div className="flex flex-wrap gap-1">
            {list.map((m, index) => (
                <div
                    key={m.id}
                    title={`#${index + 1} - ${m.verificationStatus}`}
                    onDoubleClick={() =>
                        navigate(`/project/${projectId}/marksheet/${m.id}/view`)
                    }
                    className={`
                        h-6 w-6 rounded-sm cursor-pointer transition-all duration-200
                        ${
                            m.verificationStatus === "VERIFIED"
                                ? "bg-cyan-400"
                                : "bg-gray-400"
                        }
                    `}
                />
            ))}
        </div>
    );
}

function VerifierViewProject() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [project, setProject] = useState(null);
    const [marksheets, setMarksheets] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchData();
    }, [id]);

    const fetchData = async () => {
        try {
            const [projectData, marksheetData] = await Promise.all([
                getProjectByIdForVerifier(id),
                getMarksheetsForVerifier(id),
            ]);

            setProject(projectData);
            setMarksheets(marksheetData);
        } catch (error) {
            console.error("Failed to load data", error);
        } finally {
            setLoading(false);
        }
    };

    const { verified, unverified } = useMemo(() => {
        const verified = [];
        const unverified = [];

        for (const m of marksheets) {
            if (m.verificationStatus === "VERIFIED") {
                verified.push(m);
            } else {
                unverified.push(m);
            }
        }

        return { verified, unverified };
    }, [marksheets]);

    const completionPercent = useMemo(() => {
        if (marksheets.length === 0) return 0;
        return (verified.length / marksheets.length) * 100;
    }, [verified, marksheets]);

    if (loading) {
        return <div className="p-5">Loading...</div>;
    }

    return (
        <div className="p-5 space-y-8">

            {/* Back Button */}
            <div className="flex justify-between">
                <BlueButton
                    label="← Back To Projects"
                    onClick={() => navigate(`/projects`)}
                />
            </div>

            {/* Project Card */}
            <div className="p-5 border rounded-lg shadow-sm bg-white">
                <div className="text-lg font-semibold text-gray-800">
                    {project?.projectName ?? "Untitled Project"}
                </div>

                {project?.projectDescription && (
                    <div className="mt-1 text-sm text-gray-600">
                        {project.projectDescription}
                    </div>
                )}
            </div>

            {/* Stats */}
            <div className="grid grid-cols-4 gap-4">
                <StatCard title="Unverified" value={unverified.length} />
                <StatCard title="Verified" value={verified.length} />
                <StatCard title="Total" value={marksheets.length} />
                <CompletionCard percent={completionPercent} />
            </div>

            {/* Unverified */}
            <div className="border rounded-lg p-5 bg-white shadow-sm">
                <h2 className="text-lg font-semibold mb-3">
                    Unverified ({unverified.length})
                </h2>

                <MarksheetGrid
                    list={unverified}
                    projectId={id}
                    navigate={navigate}
                />
            </div>

            {/* Verified */}
            <div className="border rounded-lg p-5 bg-white shadow-sm">
                <h2 className="text-lg font-semibold mb-3">
                    Verified ({verified.length})
                </h2>

                <MarksheetGrid
                    list={verified}
                    projectId={id}
                    navigate={navigate}
                />
            </div>

        </div>
    );
}

export default VerifierViewProject;