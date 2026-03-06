import React, { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getMarksheetById, updateMarksheet } from "../api/marksheetService";
import CenteredFullPageSpinner from "../components/CenteredFullPageSpinner";
import NotFoundCard from "../components/NotFoundCard";
import { toast } from "react-toastify";
import BlueButton from "../components/BlueButton";
import RedButton from "../components/RedButton";

/* ================= VERIFIED FIELD ================= */
const VerifiedField = ({
    label,
    type,
    field,
    data,
    onChange,
    onToggle,
    inputRef,
    onEnter,
}) => {
    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            onEnter(field);
        }
    };

    const isVerified = data[field]?.verified || false;

    return (
        <div className="border p-2 rounded bg-gray-50 mr-10">
            <div className="flex items-center justify-between mb-1">
                <label className="text-sm font-semibold">{label}</label>

                <div className="flex items-center gap-2">
                    <span
                        className={`text-sm font-bold ${isVerified ? "text-green-700" : "text-red-600"
                            }`}
                    >
                        {isVerified ? "Verified" : "Pending"}
                    </span>

                    <input
                        type="checkbox"
                        checked={isVerified}
                        onChange={() => onToggle(field)}
                        className="w-4 h-4 accent-green-600"
                    />
                </div>
            </div>

            {isVerified ? (
                <p className="text-blue-800 text-lg font-semibold">
                    {data[field]?.value}
                </p>
            ) : field === "board_shortName" ? (
                <select
                    ref={inputRef}
                    value={data[field]?.value || "GSEB"}
                    onChange={(e) => onChange(field, e.target.value)}
                    onKeyDown={handleKeyDown}
                    className="w-full border border-red-300 focus:border-red-500 focus:ring-1 focus:ring-red-400 px-2 py-1 rounded text-lg"
                >
                    <option value="">Select Board</option>
                    <option value="GSEB">GSEB</option>
                    <option value="ICSE">ICSE</option>
                    <option value="CBSE">CBSE</option>
                </select>
            ) : (
                <input
                    ref={inputRef}
                    type={type || "text"}
                    value={data[field]?.value || ""}
                    onChange={(e) => onChange(field, e.target.value)}
                    onKeyDown={handleKeyDown}
                    className="w-full border border-red-300 focus:border-red-500 focus:ring-1 focus:ring-red-400 px-2 py-1 rounded text-lg"
                />
            )}
        </div>
    );
};

/* ================= MAIN ================= */

const EditMarksheet = () => {
    const { projectId, marksheetId } = useParams();
    const navigate = useNavigate();

    const [marksheet, setMarksheet] = useState(null);
    const [loading, setLoading] = useState(false);
    const [isLoading, setIsloading] = useState(true);

    const subjectRefs = useRef([]);
    const subjectColumns = [
        "subjectName",
        "subjectCode",
        "obtained",
        "obtainedInWords",
        "subjectOutOfMarks",
        "subjectGrade",
    ];

    const toggleSubjectField = (rowIndex, column) => {
        setMarksheet(prev => {
            const updated = [...prev.markResponseList];
            updated[rowIndex][column].verified =
                !updated[rowIndex][column].verified;
            return { ...prev, markResponseList: updated };
        });
    };
    const handleSubjectKeyDown = (e, rowIndex, colIndex) => {
        if (e.key === "Enter") {
            e.preventDefault();

            setMarksheet(prev => {
                const updated = [...prev.markResponseList];

                // ✅ 1️⃣ Verify current cell
                const column = subjectColumns[colIndex];
                updated[rowIndex][column].verified = true;

                return { ...prev, markResponseList: updated };
            });

            // ✅ 2️⃣ Move focus to next cell
            const isLastColumn = colIndex === subjectColumns.length - 1;
            const isLastRow =
                rowIndex === marksheet.markResponseList.length - 1;

            let nextRow = rowIndex;
            let nextCol = colIndex + 1;

            if (isLastColumn) {
                nextCol = 0;
                nextRow = rowIndex + 1;
            }

            if (!isLastRow || !isLastColumn) {
                setTimeout(() => {
                    subjectRefs.current[nextRow]?.[nextCol]?.focus();
                }, 0);
            }
        }
    };
    /* ORDER OF FIELDS */
    const fieldsOrder = [
        /* ===== STUDENT DETAILS ===== */
        "studentName",
        "motherName",
        "fatherName",
        "seatNo",
        "yearOfPassing",

        /* ===== SCHOOL DETAILS ===== */
        "schoolCentreNo",
        "schoolIndexNo",
        "group",

        "board_shortName",

        /* ===== RESULT DETAILS ===== */
        "obtainedGrade",
        "totalObtainedMarks",
        "totalOutOfMarks",
        "obtainedPercentage",
        "obtainedPercentile",
        "resultStatus",

        "year",

        /* ===== SYSTEM ===== */
        // "verificationStatus",
    ];
    const inputRefs = useRef({});

    /* ================= FETCH ================= */

    useEffect(() => {
        const fetchData = async () => {
            try {
                const data = await getMarksheetById(projectId, marksheetId);

                const converted = {
                    ...data,

                    /* ================= STUDENT DETAILS ================= */
                    studentName: { value: data.studentName || "", verified: data.verificationStatus === "VERIFIED", label: "Student Name" },
                    fatherName: { value: data.fatherName || "", verified: data.verificationStatus === "VERIFIED", label: "Father Name" },
                    motherName: { value: data.motherName || "", verified: data.verificationStatus === "VERIFIED", label: "Mother Name" },
                    seatNo: { value: data.seatNo || "", verified: data.verificationStatus === "VERIFIED", label: "Seat No" },
                    yearOfPassing: { value: data.yearOfPassing || "", verified: data.verificationStatus === "VERIFIED", label: "Year Of Passing", type: "number" },
                    year: { value: data.year || "", verified: data.verificationStatus === "VERIFIED", label: "Year", type: "number" },

                    /* ================= RESULT DETAILS ================= */
                    resultStatus: { value: data.resultStatus || "", verified: data.verificationStatus === "VERIFIED", label: "Result Status" },
                    obtainedGrade: { value: data.obtainedGrade || "", verified: data.verificationStatus === "VERIFIED", label: "Obtained Grade" },
                    obtainedPercentage: { value: data.obtainedPercentage || "", verified: data.verificationStatus === "VERIFIED", label: "Percentage" },
                    obtainedPercentile: { value: data.obtainedPercentile || "", verified: data.verificationStatus === "VERIFIED", label: "Percentile" },

                    totalObtainedMarks: {
                        value: data.totalObtainedMarks || "",
                        verified: data.verificationStatus === "VERIFIED",
                        type: "number",
                        label: "Total Obtained Marks"
                    },
                    totalOutOfMarks: {
                        value: data.totalOutOfMarks || "",
                        verified: data.verificationStatus === "VERIFIED",
                        type: "number",
                        label: "Total Out Of Marks"
                    },

                    group: {
                        value: data.group || "",
                        verified: data.verificationStatus === "VERIFIED",
                        label: "Group"
                    },

                    /* ================= SCHOOL DETAILS ================= */
                    schoolCentreNo: { value: data.schoolCentreNo || "", verified: data.verificationStatus === "VERIFIED", label: "Centre No" },
                    schoolIndexNo: { value: data.schoolIndexNo || "", verified: data.verificationStatus === "VERIFIED", label: "School Index No" },

                    /* ================= BOARD DETAILS ================= */
                    board_fullName: {
                        value: data.board?.fullName || "",
                        verified: data.verificationStatus === "VERIFIED",
                        label: "Board Full Name"
                    },
                    board_shortName: {
                        value: data.board?.shortName || "GSEB",
                        verified: data.verificationStatus === "VERIFIED",
                        label: "Board"
                    },
                    board_state: {
                        value: data.board?.state || "",
                        verified: data.verificationStatus === "VERIFIED",
                        label: "Board State"
                    },
                    board_city: {
                        value: data.board?.city || "",
                        verified: data.verificationStatus === "VERIFIED",
                        label: "Board City"
                    },

                    /* ================= SYSTEM DETAILS ================= */
                    processingStatus: {
                        value: data.processingStatus || "",
                        verified: data.verificationStatus === "VERIFIED",
                        label: "Processing Status"
                    },
                    verificationStatus: {
                        value: data.verificationStatus || "",
                        verified: data.verificationStatus === "VERIFIED",
                        label: "Verification Status"
                    },

                    /* ================= SUBJECTS ================= */
                    markResponseList: data.markResponseList?.map((subject) => ({
                        id: subject.id,

                        subjectName: {
                            value: subject.subjectName || "",
                            verified: data.verificationStatus === "VERIFIED",
                            label: "Subject Name"
                        },

                        subjectCode: {
                            value: subject.subjectCode || "",
                            verified: data.verificationStatus === "VERIFIED",
                            label: "Subject Code"
                        },

                        obtained: {
                            value: subject.obtained || "",
                            verified: data.verificationStatus === "VERIFIED",
                            type: "number",
                            label: "Obtained Marks"
                        },

                        obtainedInWords: {
                            value: subject.obtainedInWords || "",
                            verified: data.verificationStatus === "VERIFIED",
                            label: "Obtained In Words"
                        },

                        subjectOutOfMarks: {
                            value: subject.subjectOutOfMarks || "",
                            verified: data.verificationStatus === "VERIFIED",
                            type: "number",
                            label: "Out Of Marks"
                        },

                        subjectGrade: {
                            value: subject.subjectGrade || "",
                            verified: data.verificationStatus === "VERIFIED",
                            label: "Subject Grade"
                        },
                    })),

                };

                setMarksheet(converted);

                // Auto focus first field
                setTimeout(() => {
                    inputRefs.current["studentName"]?.focus();
                }, 100);
            } catch (error) {
                console.error("Error fetching marksheet:", error);
                toast.error("Failed to load marksheet")
                toast.info("Please go to the Project page");
            } finally {
                setIsloading(false);
            }
        };

        fetchData();
    }, [projectId, marksheetId]);

    if (isLoading) return <CenteredFullPageSpinner message="Loading marksheet, please wait..." />;

    if (!marksheet) {
        return (
            <NotFoundCard
                title="Marksheet Not Found"
                message="This marksheet may have been deleted or never existed."
                buttonText="Back to Project"
                backTo={`/project/${projectId}/view`}
            />
        );
    }

    /* ================= CHANGE ================= */

    const handleChange = (field, value) => {
        setMarksheet((prev) => ({
            ...prev,
            [field]: {
                ...prev[field],
                value,
            },
        }));
    };

    const toggleVerified = (field) => {
        setMarksheet((prev) => ({
            ...prev,
            [field]: {
                ...prev[field],
                verified: !prev[field]?.verified,
            },
        }));
    };

    /* ================= ENTER LOGIC ================= */

    const handleEnter = (field) => {
        setMarksheet((prev) => {
            const updated = {
                ...prev,
                [field]: {
                    ...prev[field],
                    verified: true,
                },
            };

            // find next unverified field
            const currentIndex = fieldsOrder.indexOf(field);

            for (let i = currentIndex + 1; i < fieldsOrder.length; i++) {
                const nextField = fieldsOrder[i];

                if (!updated[nextField]?.verified) {
                    setTimeout(() => {
                        inputRefs.current[nextField]?.focus();
                    }, 0);
                    break;
                }
            }

            return updated;
        });
    };
    /* ================= SAVE ================= */

    const handleSave = async () => {
        try {
            setLoading(true);
            const payload = {
                id: marksheetId,

                /* ===== STUDENT ===== */
                studentName: marksheet.studentName.value,
                fatherName: marksheet.fatherName.value,
                motherName: marksheet.motherName.value,
                seatNo: marksheet.seatNo.value,
                yearOfPassing: Number(marksheet.yearOfPassing.value),
                year: Number(marksheet.year.value),

                /* ===== SCHOOL ===== */
                schoolCentreNo: marksheet.schoolCentreNo.value,
                schoolIndexNo: marksheet.schoolIndexNo.value,
                group: marksheet.group.value,

                /* ===== RESULT ===== */
                resultStatus: marksheet.resultStatus.value,
                obtainedGrade: marksheet.obtainedGrade.value,
                obtainedPercentage: Number(marksheet.obtainedPercentage.value),
                obtainedPercentile: Number(marksheet.obtainedPercentile.value),
                totalObtainedMarks: Number(marksheet.totalObtainedMarks.value),
                totalOutOfMarks: Number(marksheet.totalOutOfMarks.value),

                /* ===== SYSTEM ===== */
                processingStatus: marksheet.processingStatus.value,
                verificationStatus: allVerified ? "VERIFIED" : "PENDING",

                /* ===== SUBJECTS ===== */
                markResponseList: marksheet.markResponseList.map((subject) => ({
                    id: subject.id,
                    subjectName: subject.subjectName.value,
                    subjectCode: subject.subjectCode.value,
                    subjectGrade: subject.subjectGrade.value,
                    subjectOutOfMarks: Number(subject.subjectOutOfMarks.value),
                    obtained: Number(subject.obtained.value),
                    obtainedInWords: subject.obtainedInWords.value,
                    corrected: subject.corrected // if you need it
                })),

                board: marksheet.board_shortName.value
            };


            await updateMarksheet(projectId, marksheetId, payload);
            toast.success("Updated Successfully");
            navigate(-1);
        } catch (err) {
            toast.error("Failed to update");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const allVerified =
        fieldsOrder.every(
            (field) => marksheet?.[field]?.verified === true
        )
        &&
        marksheet?.markResponseList?.every((subject) =>
            subjectColumns.every(
                (column) => subject?.[column]?.verified === true
            )
        );

    const addRow = () => {
        const emptyRow = {
            // id: crypto.randomUUID(),
            subjectName: { value: "", verified: false },
            subjectCode: { value: "", verified: false },
            obtained: { value: "", verified: false },
            obtainedInWords: { value: "", verified: false },
            subjectOutOfMarks: { value: "", verified: false },
            subjectGrade: { value: "", verified: false },
        };

        setMarksheet(prev => ({
            ...prev,
            markResponseList: [...prev.markResponseList, emptyRow]
        }));
    };

    const deleteRow = (rowIndex) => {
        setMarksheet(prev => {
            const updated = [...prev.markResponseList];
            updated.splice(rowIndex, 1);

            return {
                ...prev,
                markResponseList: updated
            };
        });
    };

    /* ================= UI ================= */

    return (
        <div className="p-8 bg-gray-100 min-h-screen">
            <div className="mb-6 flex items-center justify-between">
                <BlueButton
                    label="← Back To Project"
                    onClick={() => navigate(`/project/${projectId}/view`)}
                />

                <BlueButton
                    label="View"
                    onClick={() => navigate(`/project/${projectId}/marksheet/${marksheetId}/view`)}
                />
            </div>

            <div className="bg-white rounded-xl shadow-xl p-8 flex gap-10">

                {/* LEFT FILE VIEW */}
                {/* <div className="w-1/2">
                    <iframe
                        src={`${process.env.REACT_APP_SERVER_URL}/files/${marksheet.url
                            ?.split("/")
                            .pop()}`}
                        className="w-full h-full border rounded"
                        title="file"
                    />
                </div> */}

                <div className="w-1/2">
                    {`${process.env.REACT_APP_SERVER_URL}/files/${marksheet.url?.split("/").pop()}`.endsWith(".pdf") ? (
                        <iframe
                            src={`${process.env.REACT_APP_SERVER_URL}/files/${marksheet.url?.split("/").pop()}`}
                            className="w-full h-full border rounded-lg shadow-lg"
                            title="pdf"
                        />
                    ) : (
                        <img
                            src={`${process.env.REACT_APP_SERVER_URL}/files/${marksheet.url?.split("/").pop()}`}
                            alt="file"
                            className="w-full border rounded-lg shadow-lg"
                        />
                    )}
                </div>

                {/* RIGHT FORM */}
                <div className="w-1/2 space-y-1 text-base h-screen overflow-y-auto">

                    {/* ================= STUDENT DETAILS ================= */}
                    {fieldsOrder.map((field) => (
                        <VerifiedField
                            type={marksheet[field].type}
                            key={field}
                            label={marksheet[field].label}
                            field={field}
                            data={marksheet}
                            onChange={handleChange}
                            onToggle={toggleVerified}
                            onEnter={handleEnter}
                            inputRef={(el) => (inputRefs.current[field] = el)}
                        />
                    ))}

                    {/* ================= SUBJECT MARKS TABLE ================= */}

                    <div className="mt-6">
                        <h2 className="text-lg font-bold mb-3">Subject Marks</h2>

                        <div className="w-full overflow-x-auto">
                            <table className="min-w-[1000px] border border-gray-300 text-base table-fixed mr-10">
                                <thead className="bg-gray-200">
                                    <tr>
                                        <th className="border p-2 w-[25%]">Subject</th>
                                        <th className="border p-2 w-[10%] text-center">Code</th>
                                        <th className="border p-2 w-[10%] text-center">Obtained</th>
                                        <th className="border p-2 w-[25%]">In Words</th>
                                        <th className="border p-2 w-[10%] text-center">Out Of</th>
                                        <th className="border p-2 w-[10%] text-center">Grade</th>
                                        <th className="border p-2 w-[10%] text-center">Delete</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {marksheet.markResponseList?.map((subject, rowIndex) => {

                                        if (!subjectRefs.current[rowIndex]) {
                                            subjectRefs.current[rowIndex] = [];
                                        }

                                        return (
                                            <tr key={subject.id} className="hover:bg-gray-50">

                                                {subjectColumns.map((column, colIndex) => {
                                                    const field = subject[column];
                                                    const isVerified = field?.verified;

                                                    return (
                                                        <td key={column} className="border p-2">

                                                            <div className="flex items-center gap-2">

                                                                {/* INPUT */}
                                                                <input
                                                                    readOnly={field?.verified}
                                                                    ref={(el) => {
                                                                        subjectRefs.current[rowIndex][colIndex] = el;
                                                                    }}
                                                                    type={
                                                                        column === "obtained" ||
                                                                            column === "subjectOutOfMarks"
                                                                            ? "number"
                                                                            : "text"
                                                                    }
                                                                    value={field?.value || ""}
                                                                    onChange={(e) => {
                                                                        const value = e.target.value;
                                                                        setMarksheet(prev => {
                                                                            const updated = [...prev.markResponseList];
                                                                            updated[rowIndex][column].value = value;
                                                                            return {
                                                                                ...prev,
                                                                                markResponseList: updated,
                                                                            };
                                                                        });
                                                                    }}
                                                                    onKeyDown={(e) =>
                                                                        handleSubjectKeyDown(e, rowIndex, colIndex)
                                                                    }
                                                                    className={`w-full px-2 py-1 rounded border ${isVerified
                                                                        ? "border-green-400 bg-green-50"
                                                                        : "border-red-300"
                                                                        }`}
                                                                />

                                                                {/* CHECKBOX */}
                                                                <input
                                                                    type="checkbox"
                                                                    checked={isVerified || false}
                                                                    onChange={() =>
                                                                        toggleSubjectField(rowIndex, column)
                                                                    }
                                                                    className="w-4 h-4 accent-green-600"
                                                                />
                                                            </div>

                                                            {/* STATUS TEXT */}
                                                            <div
                                                                className={`text-xs font-semibold mt-1 ${isVerified
                                                                    ? "text-green-600"
                                                                    : "text-red-600"
                                                                    }`}
                                                            >
                                                                {isVerified ? "Verified" : "Pending"}
                                                            </div>



                                                        </td>
                                                    );
                                                })}

                                                <td className="border p-2">
                                                    <RedButton extraClass="w-full" label="✕" onClick={() => deleteRow(rowIndex)} />
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                            <div className="mt-5 min-w-[1000px] text-base mr-10 mb-6">
                                <BlueButton
                                    label="Add Subject"
                                    onClick={addRow}
                                    extraClass="w-full"
                                // extraClass="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                                />
                            </div>
                        </div>
                    </div>

                    <button
                        onClick={handleSave}
                        disabled={loading || !allVerified}
                        className={`px-4 py-2 rounded text-sm text-white transition ${loading || !allVerified
                            ? "bg-gray-400 cursor-not-allowed"
                            : "bg-green-600 hover:bg-green-700"
                            }`}
                    >
                        {loading ? "Saving..." : "Save Changes"}
                    </button>

                </div>
            </div>
        </div>
    );
};

export default EditMarksheet;