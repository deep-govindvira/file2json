import React, { useEffect, useState, useRef } from "react";
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
  const [message, setMessage] = useState("");
  const [statusMap, setStatusMap] = useState({});
  const [selectedMarksheets, setSelectedMarksheets] = useState([]);
  const selectedRef = useRef([]);

  // Drag states
  const isDragging = useRef(false);
  const dragMode = useRef("select");
  const dragVisited = useRef(new Set());

  // ================= FETCH =================
  const fetchAllData = async () => {
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
  };

  useEffect(() => {
    fetchAllData();
  }, []);

  // ================= SSE =================
  useEffect(() => {
    const eventSource = new EventSource(
      `${process.env.REACT_APP_SERVER_URL}/api/stream`
    );

    eventSource.addEventListener("project-status-update", (event) => {
      const data = JSON.parse(event.data);

      setProject((prev) => {
        if (!prev || prev.projectId !== data.projectId) return prev;

        return {
          ...prev,
          projectStatus: data.projectStatus,
          projectProcessingDuration: data.projectProcessingDuration,
          processedMarksheets: data.processedMarksheets,
          processingFailedMarksheets: data.processingFailedMarksheets,
          totalMarksheets: data.totalMarksheets
        };
      });
    });

    eventSource.addEventListener("marksheet-status-update", (event) => {
      const data = JSON.parse(event.data);
      setStatusMap((prev) => ({
        ...prev,
        [data.marksheetId]: data.processingStatus
      }));
    });

    eventSource.onerror = (error) => {
      console.error("SSE error:", error);
    };

    return () => eventSource.close();
  }, []);

  // ================= HELPERS =================
  const getCellColor = (status) => {
    switch (status) {
      case "PROCESSING":
        return "bg-yellow-400";
      case "COMPLETED":
        return "bg-green-500";
      case "FAILED":
        return "bg-red-500";
      default:
        return "bg-gray-400";
    }
  };

  // ================= DRAG LOGIC =================
  const handleMouseDown = (marksheetId) => {
    isDragging.current = true;
    dragVisited.current.clear();

    const alreadySelected = selectedMarksheets.includes(marksheetId);
    dragMode.current = alreadySelected ? "deselect" : "select";

    updateSelection(marksheetId);
  };

  const handleMouseEnter = (marksheetId) => {
    if (!isDragging.current) return;
    updateSelection(marksheetId);
  };

  const handleMouseUp = () => {
    isDragging.current = false;
    dragVisited.current.clear();
  };

  const updateSelection = (marksheetId) => {
    if (dragVisited.current.has(marksheetId)) return;
    dragVisited.current.add(marksheetId);

    setSelectedMarksheets((prev) => {
      let updated;

      if (dragMode.current === "select") {
        updated = prev.includes(marksheetId)
          ? prev
          : [...prev, marksheetId];
      } else {
        updated = prev.filter((id) => id !== marksheetId);
      }

      selectedRef.current = updated; // 🔥 keep ref in sync
      return updated;
    });
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
    } catch {
      setMessage("Upload failed.");
    } finally {
      setUploading(false);
    }
  };

  const handleProcessAll = async () => {
    try {
      await process(id);
      setMessage("Processing started.");
      setSelectedMarksheets([]);
    } catch {
      setMessage("Processing failed.");
    } finally {
    }
  };

  const handleProcessSelected = async () => {
    if (!selectedMarksheets.length) return;

    try {
      setMessage("Processing selected marksheets...");
      for (const marksheetId of selectedMarksheets) {
        await processById(id, marksheetId);
      }
      setSelectedMarksheets([]);
    } catch {
      setMessage("Processing failed.");
    }
  };

  if (loading) return <p className="p-4">Loading...</p>;
  if (!project) return <p className="p-4">Project not found.</p>;

  return (
    <div
      className="p-6 bg-gray-100 min-h-screen"
      onMouseUp={handleMouseUp}
    >
      {/* ================= PROJECT INFO ================= */}
      <div className="bg-white p-6 rounded shadow mb-6">
        <h2 className="text-2xl font-bold">{project.projectName}</h2>
        <p>{project.projectDescription}</p>

        <div className="mt-4 grid grid-cols-2 md:grid-cols-3 gap-4 text-base">
          <p><b>Project ID:</b> {project.projectId}</p>
          <p><b>Project Description:</b> {project.projectDescription}</p>
          <p>
            <b>Project Duration:</b>{" "}
            {(() => {
              const totalSeconds = project.projectProcessingDuration || 0;

              const hours = Math.floor(totalSeconds / 3600);
              const minutes = Math.floor((totalSeconds % 3600) / 60);
              const seconds = totalSeconds % 60;

              return `${hours} hr ${minutes} min ${seconds} sec`;
            })()}
          </p>          <p><b>Year:</b> {project.projectYear}</p>
          <p><b>Status:</b> {project.projectStatus}</p>
          <p><b>Total Marksheets:</b> {project.totalMarksheets}</p>
          <p className="text-green-600 font-semibold">
            <b>Processed:</b> {project.processedMarksheets}
          </p>
          <p className="text-red-600 font-semibold">
            <b>Failed:</b> {project.processingFailedMarksheets}
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
            disabled={
              project.projectStatus === "PROCESSING"
            }
            className={`ml-4 px-4 py-2 text-white rounded
              ${project.projectStatus === "PROCESSING"
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-purple-600 hover:bg-purple-700"
              }`}
          >
            {project.projectStatus === "PROCESSING"
              ? "Processing..."
              : "Process All"}
          </button>

          <button
            onClick={handleProcessSelected}
            disabled={
              !selectedMarksheets.length ||
              project.projectStatus === "PROCESSING"
            }
            className={`ml-4 px-4 py-2 text-white rounded
              ${(!selectedMarksheets.length || project.projectStatus === "PROCESSING")
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-green-600 hover:bg-green-700"
              }`}
          >
            Process Selected ({selectedMarksheets.length})
          </button>
          <button
            onClick={() => {
              setSelectedMarksheets([]);
              selectedRef.current = [];
            }}
            disabled={!selectedMarksheets.length}
            className={`ml-4 px-4 py-2 text-white rounded
    ${!selectedMarksheets.length
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-red-600 hover:bg-red-700"
              }`}
          >
            Deselect All
          </button>
        </div>

        {message && (
          <p className="mt-3 text-blue-600 font-medium">{message}</p>
        )}
      </div>

      {/* ================= STATUS GRID ================= */}
      <div className="bg-white p-6 rounded shadow mb-6">
        <h3 className="text-xl font-semibold mb-4">
          Marksheets Status
        </h3>

        <div
          className="grid gap-1 select-none"
          style={{ gridTemplateColumns: "repeat(50, 1fr)" }}
        >
          {project.marksheets.map((m, index) => {
            const isSelected = selectedMarksheets.includes(m.id);

            return (
              <div
                key={m.id}
                title={`#${index + 1} - ${statusMap[m.id]}`}
                onMouseDown={() => handleMouseDown(m.id)}
                onMouseEnter={() => handleMouseEnter(m.id)}
                onDoubleClick={() =>
                  navigate(`/project/${id}/marksheet/${m.id}/view`)
                }
                className={`
                  h-6 rounded-sm cursor-pointer transition-all duration-200
                  ${getCellColor(statusMap[m.id])}
                  ${isSelected ? "ring-2 ring-black scale-110" : ""}
                `}
              />
            );
          })}
        </div>

        <p className="mt-3 text-sm text-gray-600">
          Drag to select/deselect. Double click to open marksheet.
        </p>
      </div>
    </div>
  );
};

export default ViewProject;