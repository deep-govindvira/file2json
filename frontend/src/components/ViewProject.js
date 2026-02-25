import React, { useEffect, useState, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getProjectById } from "../api/projectService";
import {
  getMarksheets,
  uploadMarksheets,
  process,
  processById
} from "../api/marksheetService";

const ViewProject = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [processingAll, setProcessingAll] = useState(false);
  const [processingId, setProcessingId] = useState(null);
  const [message, setMessage] = useState("");

  // Grid status map
  const [statusMap, setStatusMap] = useState({});

  // Highlighted marksheet
  const [highlightedId, setHighlightedId] = useState(null);

  // ================= FETCH =================
  const fetchAllData = useCallback(async () => {
    try {
      const projectResponse = await getProjectById(id);
      const marksheetsResponse = await getMarksheets(id);

      const marksheets = marksheetsResponse || [];

      setProject({
        ...projectResponse,
        marksheets
      });

      const initialStatus = {};
      marksheets.forEach((m) => {
        initialStatus[m.id] = m.processingStatus;
      });
      setStatusMap(initialStatus);

    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchAllData();
  }, [fetchAllData]);

  // ================= SSE REAL-TIME =================
  useEffect(() => {
    const eventSource = new EventSource(
      `${process.env.REACT_APP_SERVER_URL}/api/stream`
    );

    eventSource.addEventListener("marksheet-update", (event) => {
      const data = JSON.parse(event.data);

      setStatusMap((prev) => ({
        ...prev,
        [data.marksheetId]: data.processingStatus
      }));
    });

    eventSource.onerror = (error) => {
      console.error("SSE error:", error);
    };

    return () => {
      eventSource.close();
    };
  }, []);

  // ================= AUTO REMOVE HIGHLIGHT =================
  useEffect(() => {
    if (!highlightedId) return;

    const timer = setTimeout(() => {
      setHighlightedId(null);
    }, 3000);

    return () => clearTimeout(timer);
  }, [highlightedId]);

  // ================= HELPERS =================
  const getCellColor = (status) => {
    switch (status) {
      case "PROCESSING":
        return "bg-yellow-400";
      case "COMPLETED":
        return "bg-green-500";
      case "FAILED":
        return "bg-red-500";
      case "UNPROCESSED":
      default:
        return "bg-gray-400";
    }
  };

  // ================= ACTIONS =================
  const handleUpload = async () => {
    if (!files.length) return;

    setUploading(true);
    try {
      await uploadMarksheets(id, files);
      setMessage("Uploaded successfully.");
      setFiles([]);
      await fetchAllData();
    } catch (error) {
      const backendMessage =
        error?.response?.data?.message ||
        error?.response?.data ||
        error?.message ||
        "Upload failed.";
      setMessage(backendMessage);
    } finally {
      setUploading(false);
    }
  };

  const handleProcessAll = async () => {
    setProcessingAll(true);
    try {
      await process(id);
      setMessage("Processing started.");
    } catch {
      setMessage("Processing failed.");
    } finally {
      setProcessingAll(false);
    }
  };

  const handleProcessSingle = async (marksheetId) => {
    setProcessingId(marksheetId);
    try {
      await processById(id, marksheetId);
      setMessage("Processing started.");
    } catch {
      setMessage("Processing failed.");
    } finally {
      setProcessingId(null);
    }
  };

  if (loading) return <p className="p-4">Loading...</p>;
  if (!project) return <p className="p-4">Project not found.</p>;

  return (
    <div className="p-6 bg-gray-100 min-h-screen">

      {/* ================= PROJECT INFO ================= */}
      <div className="bg-white p-6 rounded shadow mb-6">
        <h2 className="text-2xl font-bold">{project.projectName}</h2>
        <p>{project.projectDescription}</p>

        <div className="mt-4 grid grid-cols-2 md:grid-cols-3 gap-4 text-base">
          <p><b>Project ID:</b> {project.projectId}</p>
          <p><b>Year:</b> {project.projectYear}</p>
          <p><b>Status:</b> {project.projectStatus}</p>
          <p><b>Total Marksheets:</b> {project.totalMarksheets}</p>
          <p className="text-green-600 font-semibold">
            <b>Processed:</b> {project.processedMarksheets}
          </p>
          <p className="text-red-600 font-semibold">
            <b>Failed:</b> {project.processingFailedMarksheets}
          </p>
          <p>
            <b>Processing Duration:</b>{" "}
            {project.projectProcessingDuration ?? 0} seconds
          </p>
        </div>

        <div className="mt-5">
          <input
            type="file"
            multiple
            onChange={(e) => setFiles(Array.from(e.target.files))}
          />

          <button
            onClick={handleUpload}
            disabled={uploading}
            className="ml-2 px-4 py-2 bg-blue-600 text-white rounded"
          >
            {uploading ? "Uploading..." : "Upload"}
          </button>

          <button
            onClick={handleProcessAll}
            disabled={processingAll}
            className="ml-4 px-4 py-2 bg-purple-600 text-white rounded"
          >
            {processingAll ? "Processing..." : "Process All"}
          </button>
        </div>

        {message && (
          <p className="mt-3 text-blue-600 font-medium">{message}</p>
        )}
      </div>

      {/* ================= STATUS GRID ================= */}
      <div className="bg-white p-6 rounded shadow mb-6">
        <h3 className="text-xl font-semibold mb-4">
          Processing Status Grid
        </h3>

        <div
          className="grid gap-1"
          style={{ gridTemplateColumns: "repeat(50, 1fr)" }}
        >
          {project.marksheets.map((m, index) => (
            <div
              key={m.id}
              title={`#${index + 1} - ${statusMap[m.id]}`}
              onClick={() => {
                const element = document.getElementById(`marksheet-${m.id}`);
                if (element) {
                  element.scrollIntoView({
                    behavior: "smooth",
                    block: "center"
                  });
                  setHighlightedId(m.id);
                }
              }}
              className={`h-6 rounded-sm cursor-pointer transition-colors duration-300 ${getCellColor(
                statusMap[m.id]
              )}`}
            />
          ))}
        </div>
      </div>

      {/* ================= MARKSHEETS GRID ================= */}
      <div className="bg-white p-6 rounded shadow">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-xl font-semibold text-gray-800">
            Marksheets Details
          </h3>
          <span className="text-sm text-gray-500">
            Total: {project.marksheets.length}
          </span>
        </div>

        {/* Responsive Grid Container */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {project.marksheets.map((m, index) => (
            <div
              id={`marksheet-${m.id}`}
              key={m.id}
              className={`relative border rounded-xl p-5 transition-all duration-300 transform hover:shadow-md ${highlightedId === m.id
                  ? "ring-4 ring-blue-500 bg-blue-50 scale-[1.02] z-10"
                  : "bg-white border-gray-200"
                }`}
            >
              {/* Badge for Index */}
              <div className="absolute top-3 right-3 bg-gray-100 text-gray-600 text-xs font-bold px-2 py-1 rounded-full">
                #{index + 1}
              </div>

              <div className="mb-4">
                <h4 className="font-bold text-gray-900 truncate pr-8" title={m.studentName}>
                  {m.studentName || "Unknown Student"}
                </h4>
                <p className="text-sm text-gray-500">Seat No: <span className="font-mono">{m.seatNo || "N/A"}</span></p>
              </div>

              <div className="flex items-center mb-4">
                <span className={`w-3 h-3 rounded-full mr-2 ${getCellColor(statusMap[m.id])}`}></span>
                <span className="text-sm font-medium uppercase tracking-wider text-gray-600">
                  {statusMap[m.id]}
                </span>
              </div>

              <div className="flex gap-2 mt-auto">
                <button
                  onClick={() => handleProcessSingle(m.id)}
                  disabled={
                    statusMap[m.id] === "PROCESSING" ||
                    statusMap[m.id] === "COMPLETED" ||
                    processingId === m.id
                  }
                  className="flex-1 text-sm py-2 bg-green-600 hover:bg-green-700 text-white font-medium rounded-lg transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
                >
                  {statusMap[m.id] === "COMPLETED"
                    ? "Done"
                    : processingId === m.id
                      ? "..."
                      : "Process"}
                </button>

                <button
                  onClick={() => navigate(`/project/${id}/marksheet/${m.id}/view`)}
                  className="flex-1 text-sm py-2 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 font-medium rounded-lg transition-colors border border-indigo-200"
                >
                  View
                </button>
              </div>
            </div>
          ))}
        </div>

        {project.marksheets.length === 0 && (
          <div className="text-center py-10 text-gray-400">
            No marksheets uploaded yet.
          </div>
        )}
      </div>

    </div>
  );
};

export default ViewProject;