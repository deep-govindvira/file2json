import { useState } from "react";
import { exportExcelData } from "../api/marksheetService";
import { useNavigate, useParams } from "react-router-dom";
import BlueButton from "../components/BlueButton";

export default function Export() {
    const navigate = useNavigate();
    const { projectId } = useParams();

    const subjectFields = [
        "Subject Name",
        "Subject Code",
        "Subject Obtained In Words",
        "Subject Obtained Marks",
        "Subject Out Of Marks",
        "Subject Grade",
    ];

    const allColumns = [
        "Student Name",
        "Seat No",
        "School Index No",
        "School Centre No",
        "Mother Name",
        "Father Name",
        "Group",
        "Board",
        "Verifier Name",
        "Verifier Email",
        "Year of Passing",
        "Total Obtained Marks",
        "Total Out Of Marks",
        "Total Obtained Percentage",
        "Total Obtained Percentile",
        "Overall Obtained Grade",
        "Result Status",
        ...subjectFields,
    ];

    const [selected, setSelected] = useState([]);

    const toggleColumn = (col) => {
        setSelected((prev) =>
            prev.includes(col)
                ? prev.filter((c) => c !== col)
                : [...prev, col]
        );
    };

    const moveUp = (index) => {
        if (index === 0) return;
        const newArr = [...selected];
        [newArr[index - 1], newArr[index]] =
            [newArr[index], newArr[index - 1]];
        setSelected(newArr);
    };

    const moveDown = (index) => {
        if (index === selected.length - 1) return;
        const newArr = [...selected];
        [newArr[index + 1], newArr[index]] =
            [newArr[index], newArr[index + 1]];
        setSelected(newArr);
    };

    const exportExcel = async () => {
        exportExcelData(projectId, selected);
    };

    return (
        <div className="bg-gray-100 p-6">
            <BlueButton
                label="← Back To Project"
                onClick={() => navigate(`/project/${projectId}/view`)}
            />

            <div className="min-h-screen bg-gray-100 flex items-center justify-center p-6">


                <div className="bg-white shadow-xl rounded-2xl p-8 w-full max-w-xl">

                    <h1 className="text-2xl font-bold mb-6 text-center">
                        Export Marksheets
                    </h1>

                    {/* Select Columns */}
                    <div className="mb-6">
                        <h2 className="font-semibold mb-3">
                            Select Columns
                        </h2>

                        <label className="flex items-center gap-3 mb-2">
                            <input
                                type="checkbox"
                                className="h-4 w-4"
                                checked={selected.length === allColumns.length}
                                indeterminate={selected.length > 0 && selected.length < allColumns.length ? "true" : undefined}
                                onChange={() => {
                                    if (selected.length === allColumns.length) {
                                        setSelected([]); // Deselect all
                                    } else {
                                        setSelected([...allColumns]); // Select all
                                    }
                                }}
                            />
                            <span>Select All</span>
                        </label>

                        <div className="space-y-2">
                            {allColumns.map((col) => (
                                <label
                                    key={col}
                                    className="flex items-center gap-3"
                                >
                                    <input
                                        type="checkbox"
                                        className="h-4 w-4"
                                        checked={selected.includes(col)}
                                        onChange={() => toggleColumn(col)}
                                    />
                                    <span>{col}</span>
                                </label>
                            ))}
                        </div>
                    </div>

                    {/* Column Order */}
                    <div className="mb-6">
                        <h2 className="font-semibold mb-3">
                            Column Order
                        </h2>

                        {selected.length === 0 && (
                            <p className="text-gray-500">
                                No columns selected
                            </p>
                        )}

                        <div className="space-y-2">
                            {selected.map((col, index) => (
                                <div
                                    key={col}
                                    className="flex items-center justify-between bg-gray-100 p-3 rounded-lg"
                                >
                                    <span>{col}</span>

                                    <div className="flex gap-2">
                                        <button
                                            onClick={() => moveUp(index)}
                                            className="px-2 py-1 bg-gray-300 rounded hover:bg-gray-400"
                                        >
                                            ↑
                                        </button>

                                        <button
                                            onClick={() => moveDown(index)}
                                            className="px-2 py-1 bg-gray-300 rounded hover:bg-gray-400"
                                        >
                                            ↓
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Export Button */}
                    <button
                        onClick={exportExcel}
                        className="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
                    >
                        Export to Excel
                    </button>

                </div>
            </div>

        </div>
    );
}