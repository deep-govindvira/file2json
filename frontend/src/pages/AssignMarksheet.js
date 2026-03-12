import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { getMarksheets, assignMarksheetToUser } from "../api/marksheetService";
import { getUsersByProject } from "../api/userService";
import BlueButton from "../components/BlueButton";
import CenteredFullPageSpinner from "../components/CenteredFullPageSpinner";
import NotFoundCard from "../components/NotFoundCard";
import Cookies from "js-cookie";
import { toast } from "react-toastify";
import { buildStyles, CircularProgressbar } from "react-circular-progressbar";

const statusConfig = {
    UNPROCESSED: "bg-gray-400",
    QUEUED: "bg-blue-400",
    PROCESSING: "bg-yellow-400",
    FAILED: "bg-red-400",
    COMPLETED: "bg-green-400",
    IN_PROGRESS: "bg-orange-400",
    VERIFIED: "bg-cyan-400",
};

const AssignMarksheet = () => {

    const { id } = useParams();
    const navigate = useNavigate();

    const [isLoading, setIsLoading] = useState(true);

    const [marksheets, setMarksheets] = useState([]);
    const [users, setUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState("");
    const [sortType, setSortType] = useState("NAME_ASC");

    const [selectedMarksheets, setSelectedMarksheets] = useState([]);

    const isDragging = useRef(false);
    const dragMode = useRef("select");
    const dragVisited = useRef(new Set());

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {

        try {

            setIsLoading(true);

            const [m, u] = await Promise.all([
                getMarksheets(id),
                getUsersByProject(id)
            ]);

            setMarksheets(m || []);
            setUsers(u || []);

        } catch (error) {

            console.error("Error fetching data:", error);

        } finally {

            setIsLoading(false);

        }
    };

    // ================= FILTER ONLY COMPLETED + FAILED =================

    const filteredMarksheets = useMemo(() => {

        return marksheets.filter(
            m =>
                m.processingStatus === "COMPLETED" ||
                m.processingStatus === "FAILED"
        );

    }, [marksheets]);

    // ================= USER MAP =================

    const userMap = useMemo(() => {

        const map = {};

        users.forEach(u => {
            map[u.email] = u.name;
        });

        return map;

    }, [users]);

    const getUserName = (email) => userMap[email] || email;

    // ================= STATUS COLOR =================

    const getStatusColor = (m) => {

        if (m.verificationStatus === "VERIFIED")
            return statusConfig.VERIFIED;

        if (m.verificationStatus === "IN_PROGRESS")
            return statusConfig.IN_PROGRESS;

        return statusConfig[m.processingStatus] || "bg-gray-200";
    };

    // ================= SELECTABLE =================

    const selectableSet = useMemo(() => {

        const set = new Set();

        filteredMarksheets.forEach(m => {
            set.add(m.id);
        });

        return set;

    }, [filteredMarksheets]);

    const isSelectable = (id) => selectableSet.has(id);

    // ================= DRAG =================

    const handleMouseDown = (marksheetId) => {

        if (!isSelectable(marksheetId)) return;

        isDragging.current = true;
        dragVisited.current.clear();

        const alreadySelected = selectedMarksheets.includes(marksheetId);
        dragMode.current = alreadySelected ? "deselect" : "select";

        updateSelection(marksheetId);
    };

    const handleMouseEnter = (marksheetId) => {

        if (!isDragging.current) return;
        if (!isSelectable(marksheetId)) return;

        updateSelection(marksheetId);
    };

    const handleMouseUp = () => {

        isDragging.current = false;
        dragVisited.current.clear();

    };

    const updateSelection = (marksheetId) => {

        if (dragVisited.current.has(marksheetId)) return;
        dragVisited.current.add(marksheetId);

        setSelectedMarksheets(prev => {

            if (dragMode.current === "select") {

                if (prev.includes(marksheetId)) return prev;
                return [...prev, marksheetId];

            } else {

                return prev.filter(id => id !== marksheetId);
            }
        });
    };

    // ================= ASSIGN =================

    const handleAssign = async () => {

        if (!selectedUser || selectedMarksheets.length === 0) return;

        try {

            await Promise.all(
                selectedMarksheets.map(m =>
                    assignMarksheetToUser(id, m, selectedUser)
                )
            );

            toast.success("Marksheets assigned successfully");

            setSelectedMarksheets([]);
            setSelectedUser("");

            await fetchData();

        } catch (error) {

            toast.error("Failed to assign marksheets");

        }
    };

    // ================= GROUPING =================

    const { unassigned, assignedGroups } = useMemo(() => {

        const unassigned = [];
        const assigned = {};

        filteredMarksheets.forEach(m => {

            if (!m.assignedToUser) {
                unassigned.push(m);
                return;
            }

            if (!assigned[m.assignedToUser]) {
                assigned[m.assignedToUser] = [];
            }

            assigned[m.assignedToUser].push(m);
        });

        return { unassigned, assignedGroups: assigned };

    }, [filteredMarksheets]);

    // ================= VERIFIER STATS =================

    const verifierStats = useMemo(() => {

        const stats = Object.entries(assignedGroups).map(([user, list]) => {

            const verified = list.filter(
                m => m.verificationStatus === "VERIFIED"
            ).length;

            const total = list.length;
            const pending = total - verified;
            const progress = total ? verified / total : 0;

            return { user, list, verified, total, pending, progress };
        });

        stats.sort((a, b) => {

            switch (sortType) {

                case "PROGRESS_DESC": return b.progress - a.progress;
                case "PROGRESS_ASC": return a.progress - b.progress;

                case "ASSIGNED_DESC": return b.total - a.total;
                case "ASSIGNED_ASC": return a.total - b.total;

                case "VERIFIED_DESC": return b.verified - a.verified;
                case "VERIFIED_ASC": return a.verified - b.verified;

                case "PENDING_DESC": return b.pending - a.pending;
                case "PENDING_ASC": return a.pending - b.pending;

                case "NAME_ASC":
                    return getUserName(a.user).localeCompare(getUserName(b.user));

                case "NAME_DESC":
                    return getUserName(b.user).localeCompare(getUserName(a.user));

                default:
                    return 0;
            }
        });

        return stats;

    }, [assignedGroups, sortType, userMap]);

    const sortedUsers = useMemo(() => {

        return [...users].sort((a, b) =>
            a.name.localeCompare(b.name)
        );

    }, [users]);

    // ================= GLOBAL STATS =================

    const globalStats = useMemo(() => {

        let verified = 0;
        let unverified = 0;

        filteredMarksheets.forEach(m => {

            if (m.verificationStatus === "VERIFIED") {
                verified++;
            } else {
                unverified++;
            }

        });

        return {
            verified,
            unverified,
            total: filteredMarksheets.length
        };

    }, [filteredMarksheets]);

    // ================= GRID =================

    const renderRow = (title, list) => {

        const verifiedCount = list.filter(
            m => m.verificationStatus === "VERIFIED"
        ).length;

        const total = list.length;
        const percent = total ? Math.round((verifiedCount / total) * 100) : 0;

        return (

            <div className="border rounded-lg shadow-sm bg-white p-4 mb-6">

                <div className="flex items-center justify-between mb-3">

                    <h2 className="font-semibold text-gray-800">
                        {title}
                    </h2>

                    <div className="flex items-center gap-3 text-sm">

                        <span className="px-2 py-1 bg-cyan-100 text-cyan-800 rounded">
                            {title !== "Unassigned Marksheets" && (verifiedCount + " /")} {total}
                        </span>

                        {title !== "Unassigned Marksheets" &&
                            <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded font-semibold">
                                {percent}% Verified
                            </span>
                        }

                    </div>

                </div>

                <div
                    className="flex flex-wrap gap-1"
                    onMouseUp={handleMouseUp}
                >
                    {list.map(m => {

                        const isSelected = selectedMarksheets.includes(m.id);

                        return (
                            <div
                                key={m.id}
                                onMouseDown={() => handleMouseDown(m.id)}
                                onMouseEnter={() => handleMouseEnter(m.id)}
                                onDoubleClick={() =>
                                    navigate(`/project/${id}/marksheet/${m.id}/view`)
                                }
                                className={`
                                ${getStatusColor(m)}
                                ${isSelected ? "ring-2 ring-black scale-110" : ""}
                                w-6 h-6 rounded-sm cursor-pointer transition-all duration-200
                            `}
                            />
                        );
                    })}
                </div>

            </div>
        );
    };

    if (isLoading)
        return <CenteredFullPageSpinner message="Loading details, please wait..." />;

    if (Cookies.get("role") != "ADMIN") {

        return (
            <NotFoundCard
                title="Not Found"
                message="You can't assign marksheets."
                buttonText="Back to Projects"
                backTo="/projects"
            />
        );
    }

    return (

        <div className="p-6">

            <div className="mb-6 flex items-center justify-between">

                <BlueButton
                    label="← Back To Project"
                    onClick={() => navigate(`/project/${id}/view`)}
                />

            </div>

            <h1 className="text-2xl font-bold mb-6">
                Verification Assignment
            </h1>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-6">

                <div className="px-4 py-3 border border-gray-200 rounded shadow">
                    <div className="text-xl text-gray-600">Total Unverified</div>
                    <div className="mt-3 text-3xl font-bold">{globalStats.unverified}</div>
                </div>

                <div className="px-4 py-3 border border-gray-200 rounded shadow">
                    <div className="text-xl text-gray-600">Total Verified</div>
                    <div className="mt-3 text-3xl font-bold">{globalStats.verified}</div>
                </div>

                <div className="px-4 py-3 border border-gray-200 rounded shadow">
                    <div className="text-xl text-gray-600">Total Marksheets</div>
                    <div className="mt-3 text-3xl font-bold">{globalStats.total}</div>
                </div>

                <div className="px-4 py-3 border border-gray-200 bg-white rounded shadow flex flex-col items-center justify-center">
                    <div className="text-xl mb-2 text-gray-600">Completion</div>

                    <div className="w-16 h-16">
                        <CircularProgressbar
                            value={globalStats.unverified}
                            text={`${globalStats.unverified.toFixed(0)}%`}
                            styles={buildStyles({
                                textSize: "28px",
                                pathColor: "#06b6d4",
                                textColor: "#374151",
                                trailColor: "#e5e7eb",
                            })}
                        />
                    </div>
                </div>

            </div>

            <div className="flex gap-3 mb-6">

                <select
                    value={selectedUser}
                    onChange={(e) => setSelectedUser(e.target.value)}
                    className="border px-3 py-2 rounded"
                >
                    <option value="">Select User</option>

                    {sortedUsers.map(u => {

                        const assignedCount =
                            assignedGroups[u.name]?.length || 0;

                        return (
                            <option key={u.userId} value={u.userId}>
                                {u.name} ({assignedCount})
                            </option>
                        );
                    })}

                </select>

                <button
                    onClick={handleAssign}
                    disabled={!selectedUser || selectedMarksheets.length === 0}
                    className="px-4 py-2 bg-blue-600 text-white rounded disabled:bg-gray-400"
                >
                    Assign ({selectedMarksheets.length})
                </button>

                {selectedMarksheets.length > 0 && (

                    <button
                        onClick={() => setSelectedMarksheets([])}
                        className="px-4 py-2 bg-gray-500 text-white rounded"
                    >
                        Deselect
                    </button>

                )}

                <select
                    value={sortType}
                    onChange={(e) => setSortType(e.target.value)}
                    className="border px-3 py-2 rounded"
                >
                    <option value="PROGRESS_DESC">Progress Highest</option>
                    <option value="PROGRESS_ASC">Progress Lowest</option>
                    <option value="ASSIGNED_DESC">Most Assigned</option>
                    <option value="ASSIGNED_ASC">Least Assigned</option>
                    <option value="VERIFIED_DESC">Most Verified</option>
                    <option value="VERIFIED_ASC">Least Verified</option>
                    <option value="PENDING_DESC">Most Pending</option>
                    <option value="PENDING_ASC">Least Pending</option>
                    <option value="NAME_ASC">Name A → Z</option>
                    <option value="NAME_DESC">Name Z → A</option>
                </select>

            </div>

            <div className="flex items-center gap-6 mb-4 text-sm">

                <div className="flex items-center gap-2">
                    <div className="w-4 h-4 bg-red-400 rounded-sm"></div>
                    <span>Failed</span>
                </div>

                <div className="flex items-center gap-2">
                    <div className="w-4 h-4 bg-green-400 rounded-sm"></div>
                    <span>Completed</span>
                </div>

                <div className="flex items-center gap-2">
                    <div className="w-4 h-4 bg-cyan-400 rounded-sm"></div>
                    <span>Verified</span>
                </div>

            </div>

            {renderRow("Unassigned Marksheets", unassigned)}

            {verifierStats.map(v => (
                <div key={v.user}>
                    {renderRow(
                        `Assigned to - ${getUserName(v.user)}`,
                        v.list
                    )}
                </div>
            ))}

        </div>
    );
};

export default AssignMarksheet;