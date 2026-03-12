import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { addUserToProject, getProjectById, removeUserToProject } from "../api/projectService";
import { getMarksheets, process, processById, uploadMarksheets } from "../api/marksheetService";
import CenteredFullPageSpinner from "./CenteredFullPageSpinner";
import { connectSSE } from "../api/sseClient";
import StatusCard from "./StatusCard";
import NotFoundCard from "./NotFoundCard";
import { toast } from "react-toastify";
import StatusGrid from "./StatusGrid";
import BlueButton from "./BlueButton";
import RedButton from "./RedButton";
import { getUsersByProject } from "../api/userService";
import Cookies from "js-cookie";
import { getAllDepartments } from "../api/departmentService";

const statusConfig = [
  { key: "UNPROCESSED", name: "UNPROCESSED", displayName: "Unprocessed", color: "bg-gray-200", darkColor: "bg-gray-400" },
  { key: "QUEUED", name: "QUEUED", displayName: "Queued", color: "bg-blue-200", darkColor: "bg-blue-400" },
  { key: "PROCESSING", name: "PROCESSING", displayName: "Processing", color: "bg-yellow-200", darkColor: "bg-yellow-400" },
  { key: "FAILED", name: "FAILED", displayName: "Failed", color: "bg-red-200", darkColor: "bg-red-400" },
  { key: "COMPLETED", name: "COMPLETED", displayName: "Completed", color: "bg-green-200", darkColor: "bg-green-400" },
  { key: "IN_PROGRESS", name: "VERIFYING", displayName: "Verifying", color: "bg-orange-200", darkColor: "bg-orange-400" },
  { key: "VERIFIED", name: "VERIFIED", displayName: "Verified", color: "bg-cyan-200", darkColor: "bg-cyan-400" },
  { key: "TOTAL", name: "TOTAL", displayName: "Total", color: "bg-purple-200", darkColor: "bg-purple-400" },
];

const projectStatusConfig = {
  COMPLETED: { label: "COMPLETED", style: "bg-green-100 text-green-700" },
  PROCESSING: { label: "PROCESSING", style: "bg-yellow-100 text-yellow-700" },
  PARTIALLY_COMPLETED: { label: "PARTIALLY COMPLETED", style: "bg-blue-100 text-blue-700" },
  UNPROCESSED: { label: "UNPROCESSED", style: "bg-red-100 text-red-700" },
};

function formatDuration(totalSeconds = 0) {
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = Math.floor(totalSeconds % 60);

  const result = [
    hours > 0 && `${hours}h`,
    minutes > 0 && `${minutes} min`,
    seconds > 0 && `${seconds} sec`
  ]
    .filter(Boolean)
    .join(" ");

  return result || "0 sec"; // fallback when all values are 0
}

const ViewProject = () => {

  const navigate = useNavigate();

  const { id } = useParams();
  const [project, setProject] = useState(null);
  const [statusMap, setStatusMap] = useState({});
  const [isLoading, setLoading] = useState(true);

  // Drag states
  const isDragging = useRef(false);
  const dragMode = useRef("select");
  const dragVisited = useRef(new Set());
  const [selectedMarksheets, setSelectedMarksheets] = useState([]);
  const selectedRef = useRef([]);

  const [files, setFiles] = useState([]);
  const [removingIndex, setRemovingIndex] = useState(null);

  const [uploading, setUploading] = useState(false);
  const [processingAll, setProcessingAll] = useState(false);
  const [processingSelected, setProcessingSelected] = useState(false);

  const [users, setUsers] = useState([]);
  const [verifiedByMap, setVerifiedByMap] = useState({});

  const [userEmail, setUserEmail] = useState("");
  const [addingUser, setAddingUser] = useState(false);
  const [departmentId, setDepartmentId] = useState("");
  const [departments, setDepartments] = useState([]);

  const handleAddUser = async () => {
    if (!userEmail.trim()) {
      toast.error("Please enter user email");
      return;
    }

    if (!departmentId) {
      toast.error("Please select department");
      return;
    }


    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(userEmail)) {
      toast.error("Invalid formate of email address");
      return;
    }

    try {
      setAddingUser(true);
      await addUserToProject(id, {
        email: userEmail,
        departmentId: departmentId
      });
      toast.success("User added to project");
      setUserEmail("");
      setDepartmentId("");

      const usersResponse = await getUsersByProject(id);
      setUsers(usersResponse);
    } catch {
      toast.error("Failed to add user");
    } finally {
      setAddingUser(false);
    }
  };

  const handleRemoveUser = async () => {
    if (!userEmail.trim()) {
      toast.error("Please enter user email");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(userEmail)) {
      toast.error("Invalid formate of email address");
      return;
    }

    try {
      setAddingUser(true);
      await removeUserToProject(id, userEmail);
      toast.success("User removed to project");
      setUserEmail("");
      const usersResponse = await getUsersByProject(id);
      setUsers(usersResponse);
    } catch {
      toast.error("Failed to remove user");
    } finally {
      setAddingUser(false);
    }
  };

  const fetchData = async () => {
    try {
      const projectResponse = await getProjectById(id);
      const marksheetsResponse = (await getMarksheets(id)) || [];
      const usersResponse = await getUsersByProject(id);
      const departmentsResponse = await getAllDepartments();

      setProject(projectResponse);
      setUsers(usersResponse);
      setDepartments(departmentsResponse);

      const initialStatus = {};
      const verifiedMap = {};

      marksheetsResponse.forEach((m) => {
        if (m.verificationStatus !== "UNVERIFIED") {
          initialStatus[m.id] = m.verificationStatus;
          verifiedMap[m.id] = m.verifiedByUser;
        } else initialStatus[m.id] = m.processingStatus;
      });

      setStatusMap(initialStatus);
      setVerifiedByMap(verifiedMap);

    } catch (error) {
      toast.error("Failed to fetch project details");
      toast.info("Please go to the Projects page");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {

    if (!project?.projectId) return;

    const eventSource = connectSSE();

    const handleSSEProjectUpdate = (event) => {
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
    };

    const handleSSEMarksheetUpdate = (event) => {
      const data = JSON.parse(event.data);

      if (data.projectId !== project.projectId) {
        return;
      }

      setStatusMap((prev) => ({
        ...prev,
        [data.marksheetId]: data.verificationStatus !== "UNVERIFIED" ? data.verificationStatus : data.processingStatus
      }));

      // update verifiedByMap
      if (data.verificationStatus === "VERIFIED") {
        setVerifiedByMap((prev) => ({
          ...prev,
          [data.marksheetId]: data.verifiedByUser
        }));
      }


    };

    eventSource.addEventListener("project-status-update", handleSSEProjectUpdate);
    eventSource.addEventListener("marksheet-status-update", handleSSEMarksheetUpdate);

    eventSource.onerror = (error) => {
      console.error("SSE error:", error);
    };

    return () => {
      eventSource.removeEventListener("project-status-update", handleSSEProjectUpdate);
      eventSource.removeEventListener("marksheet-status-update", handleSSEMarksheetUpdate);
    };

  }, [project?.projectId]);

  const handleProcessAll = async () => {
    setProcessingAll(true);
    try {
      await process(id);
      setSelectedMarksheets([]);
      toast.success("Processing ...")
    } catch {
      toast.error("Processing not started");
    } finally {
      setProcessingAll(false);
    }
  };

  const handleProcessSelected = async () => {
    if (!selectedMarksheets.length) return;
    setProcessingSelected(true);

    try {
      for (const marksheetId of selectedMarksheets) {
        await processById(id, marksheetId);
      }
      setSelectedMarksheets([]);
      toast.success("Processing ...")
    } catch {
      toast.error("Failed to process some marksheets");
    }
    finally {
      setProcessingSelected(false);
    }
  };

  const getCountByStatus = (key) => {
    if (key === "TOTAL") {
      return Object.values(statusMap).length;
    }

    return Object.values(statusMap).filter(
      (s) => s === key
    ).length;

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

      selectedRef.current = updated;
      return updated;
    });
  };

  const handleUpload = async () => {
    if (!files.length) return;

    setUploading(true);

    try {
      await uploadMarksheets(id, files, setFiles);
      // setFiles([]);
      await fetchData();
      toast.success("Uploaded successfully")

    } catch {
      toast.error("Failed to upload");
      toast.info("Please try again");
    } finally {
      setUploading(false);
    }
  };

  const removeFile = (index) => {
    const updated = files.filter((_, i) => i !== index);
    setFiles(updated);
  };

  function handleRemove(index) {
    setRemovingIndex(index);

    setTimeout(() => {
      removeFile(index);      // your actual remove logic
      setRemovingIndex(null);
    }, 300); // match animation duration
  }

  if (isLoading) return <CenteredFullPageSpinner message="Loading project details, please wait..." />;

  if (!project) {
    return (
      <NotFoundCard
        title="Project Not Found"
        message="This project may have been deleted or never existed."
        buttonText="Back to Projects"
        backTo="/projects"
      />
    );
  }

  const buttons = [
    {
      label: "Upload",
      show: files.length > 0,
      onClick: handleUpload,
      disabled: files.length === 0 || uploading,
      loadingText: "Uploading...",
      loading: uploading,
      // extraClass: "bg-blue-600 hover:bg-blue-700"
    },
    {
      label: "Deselect All",
      show: selectedMarksheets.length > 0,
      onClick: () => {
        setSelectedMarksheets([]);
        selectedRef.current = [];
      },
      // extraClass: "bg-gray-500 hover:bg-gray-600"
    },
    {
      label: `Process Selected (${selectedMarksheets.length})`,
      show: selectedMarksheets.length > 0,
      onClick: handleProcessSelected,
      disabled: selectedMarksheets.length === 0 || processingSelected,
      loading: processingSelected,
      loadingText: `Processing Selected (${selectedMarksheets.length}) ...`,
      // extraClass: "bg-green-600 hover:bg-green-700"
    },
    {
      label: "Process All",
      loadingText: "Processing...",
      show: project.projectStatus !== "PROCESSING",
      onClick: handleProcessAll,
      disabled: project.projectStatus === "PROCESSING" || processingAll,
      loading: processingAll,
      // extraClass:
      //   project.projectStatus === "PROCESSING"
      //     ? "bg-gray-400"
      //     : "bg-purple-600 hover:bg-purple-700",
      // margin: "ml-20"
    },

    {
      label: "Edit Project",
      show: project.projectCreator === Cookies.get("email"),
      onClick: () => {
        navigate(`/project/${id}/edit`);
      }
    },
    {
      label: "Verification Assignment",
      show: true,
      onClick: () => {
        navigate(`/project/${id}/assignMarksheets`);
      }
    }
  ];

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="mb-6 flex items-center justify-between">
        <BlueButton
          label="← Back To Projects"
          onClick={() => navigate(`/dashboard/`)}
        />
      </div>

      {/* <div className="bg-white p-6 rounded-2xl shadow mb-6"> */}
      <div className="bg-white mb-6 rounded-2xl border border-gray-200 p-6">

        {/* ================= TOP ROW ================= */}
        <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-4">

          {/* Title */}
          <div>
            <h1 className="text-3xl font-bold text-gray-900 tracking-tight">
              {project.projectName}
            </h1>
            <p className="mt-2 text-base text-gray-600">
              {project.projectDescription}
            </p>
          </div>

          {/* Status */}
          <span
            className={`px-4 py-1.5 text-sm font-semibold rounded-md w-fit ${projectStatusConfig[project.projectStatus]?.style || "bg-gray-100 text-gray-700"}`}
          >
            {projectStatusConfig[project.projectStatus]?.label}
          </span>

        </div>

        {/* ================= BUTTON ROW ================= */}
        <div className="mt-6 flex flex-col sm:flex-row sm:flex-wrap gap-3 items-start sm:items-center">
          <input
            type="file"
            onChange={(e) => setFiles(Array.from(e.target.files))}
            multiple
          />

          {buttons.map((btn, index) => (

            <BlueButton
              key={btn.label}
              label={btn.label}
              onClick={btn.onClick}
              invisible={btn.show === false}
              disabled={btn.disabled}
              loadingText={btn.loadingText}
              extraClass={btn.extraClass}
              loading={btn.loading}
            />

            // <ProjectActionButton
            //   key={index}
            //   label={btn.label}
            //   onClick={btn.onClick}
            //   show={btn.show}
            //   disabled={btn.disabled}
            //   loadingText={btn.loadingText}
            //   extraClass={btn.extraClass}
            // />
          ))}

        </div>

        {/* Divider */}
        <div className="my-6 border-t border-gray-200" />

        {/* ================= INFO SECTION ================= */}
        <div className="flex flex-wrap gap-12">

          {project.projectYear &&
            <div>
              <p className="text-sm text-gray-500 font-medium">Year</p>
              <p className="text-lg font-semibold text-gray-800">
                {project.projectYear}
              </p>
            </div>
          }

          {((getCountByStatus("QUEUED") + getCountByStatus("PROCESSING")) / 16.0) > 0 && (
            <div>
              <p className="text-sm text-gray-500 font-medium">Expected Time</p>
              <p className="text-lg font-semibold text-gray-800">
                {formatDuration((getCountByStatus("QUEUED") + getCountByStatus("PROCESSING")) * 100 / 16.0)}
              </p>
            </div>
          )}

          <div>
            <p className="text-sm text-gray-500 font-medium">Duration</p>
            <p className="text-lg font-semibold text-gray-800">
              {formatDuration(project.projectProcessingDuration)}
            </p>
          </div>

        </div>

      </div>

      {files.length > 0 && (
        <div className="mb-6">
          {/* Title */}
          <h3 className="text-lg font-semibold text-gray-800 mb-3">
            {files.length} files ready to upload
          </h3>

          {/* Horizontal List */}
          <div className="flex flex-wrap gap-3">
            {files.map((file, index) => (
              <div
                key={index}
                className={`flex items-center gap-2
                  bg-white border
                  px-3 py-2 rounded-md
                  transition-all duration-200
                  ${removingIndex === index
                    ? "opacity-0 scale-95"
                    : "opacity-100 scale-100"
                  }`}
              >
                {/* Index */}
                {/* <span className="text-sm font-semibold text-gray-600">
            {index + 1}
          </span> */}

                {/* Remove Button */}
                <button
                  onClick={() => handleRemove(index)}
                  className="text-red-500 hover:text-red-700 text-sm font-bold"
                >
                  ✕
                </button>

                {/* File Name */}
                <span className="text-sm text-gray-800">
                  {file.name}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className={`grid grid-cols-1 md:grid-cols-4 lg:grid-cols-${statusConfig.length} gap-4 mb-8`}>
        {statusConfig.map((status) => (
          <StatusCard
            key={status.key}
            title={status.name}
            count={getCountByStatus(status.key)}
            bgColor={status.color}
          />
        ))}
      </div>

      {statusConfig.map((config) => {
        if (config.key === "TOTAL") return null;

        const items = Object.keys(statusMap)
          .filter((id) => statusMap[id] === config.key)
          .map((id) => ({ id }));

        const isSelectable =
          config.key === "UNPROCESSED" || config.key === "FAILED";


        const verifiedGroups = {};

        Object.entries(statusMap).forEach(([id, status]) => {

          if (status === "VERIFIED") {

            const user = verifiedByMap[id] || "Unknown";

            if (!verifiedGroups[user]) {
              verifiedGroups[user] = [];
            }

            verifiedGroups[user].push({ id });

          }
        });

        if (config.key === "VERIFIED") {

          const getUserName = (userEmail) => {
            const user = users.find((u) => u.email === userEmail);
            return user ? user.name : userEmail;
          };

          return (
            <div key="verified-users">
              {/* <h2 className="text-lg font-semibold mb-3">Verified</h2> */}

              {Object.entries(verifiedGroups).map(([userEmail, items]) => (
                <div key={userEmail} className="mb-6">

                  <StatusGrid
                    title={config.key + " by " + getUserName(userEmail) + " " + (project.projectCreator === userEmail ? "(Project Creator)" : "") + " " +
                      (userEmail === Cookies.get("email") ? " (You)" : "")
                    }
                    items={items}
                    statusMap={statusMap}
                    selectedMarksheets={selectedMarksheets}
                    handleMouseDown={handleMouseDown}
                    handleMouseEnter={handleMouseEnter}
                    gridColumns={50}
                    colorClass={config.darkColor}
                    projectId={id}
                    isSelectable={false}
                  />

                </div>
              ))}

            </div>
          );
        }


        return (
          <div key={config.key} onMouseUp={handleMouseUp} >
            <StatusGrid
              key={config.key}
              title={config.displayName}
              items={items}
              statusMap={statusMap}
              selectedMarksheets={selectedMarksheets}
              handleMouseDown={handleMouseDown}
              handleMouseEnter={handleMouseEnter}
              gridColumns={50}
              colorClass={config.darkColor}
              projectId={id}
              isSelectable={isSelectable}
            />
          </div>

        );
      })}

      <p className="ml-1 text-sm text-gray-600">
        Drag to select/deselect. Double click to open marksheet.
      </p>

      {
        (project.projectCreator === Cookies.get("email")) &&
        (<div className="mt-4 flex gap-2 items-center">

          <input
            type="email"
            placeholder="Enter user email"
            value={userEmail}
            autoComplete="off"
            onChange={(e) => setUserEmail(e.target.value)}
            className="border rounded-md px-3 py-2 w-72 text-sm"
          />

          <select
            value={departmentId}
            onChange={(e) => setDepartmentId(e.target.value)}
            className="border rounded-md px-3 py-2 text-sm"
          >
            <option value="">Select Department</option>

            {departments.map((dept) => (
              <option key={dept.id} value={dept.id}>
                {dept.name}
              </option>
            ))}

          </select>

          <BlueButton
            label="Add User"
            onClick={handleAddUser}
            disabled={addingUser}
            loading={addingUser}
            loadingText="Adding..."
          />

          <RedButton
            label="Remove User"
            onClick={handleRemoveUser}
            disabled={addingUser}
            loading={addingUser}
            loadingText="Removing..."
          />

        </div>)
      }

      <div className="mt-5 bg-white mb-6 rounded-2xl border border-gray-200 p-6">
        <h2 className="text-xl font-semibold mb-4 text-gray-800">
          Project Users
        </h2>

        {users.length === 0 ? (
          <p className="text-gray-500">No users assigned</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {users.map((user) => (
              <div
                key={user.userId}
                className="border rounded-lg p-4 bg-gray-50 hover:-translate-y-1 transition-all duration-300 hover:shadow-xl"
              >
                <p className="font-semibold text-gray-900">
                  {user.name} {project.projectCreator === user.email ? "(Project Creator)" : ""}
                  {user.email === Cookies.get("email") ? " (You)" : ""}
                </p>

                <p className="text-sm text-gray-600">
                  {user.email}
                </p>

                <p className="text-xs text-gray-500 mt-1">
                  {user.department}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>

    </div>
  );
};

export default ViewProject;