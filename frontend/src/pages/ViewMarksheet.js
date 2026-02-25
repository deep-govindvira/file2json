import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getMarksheetById } from "../api/marksheetService";

const ViewMarksheet = () => {
    const { projectId, marksheetId } = useParams();
    const [marksheet, setMarksheet] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const eventSource = new EventSource(`${process.env.REACT_APP_SERVER_URL}/api/stream`);

        eventSource.addEventListener("marksheet-update", (event) => {
            const parsedData = JSON.parse(event.data);
            console.log(event.data);
            if (parsedData.id !== parseInt(marksheetId)) return; // Only update if the incoming marksheet ID matches the current one

            setMarksheet(parsedData);
        });

        eventSource.onerror = (error) => {
            console.error("SSE Error:", error);
            eventSource.close();
        };

        return () => eventSource.close();
    }, [marksheetId]);


    useEffect(() => {
        const fetchMarksheet = async () => {
            try {
                const data = await getMarksheetById(projectId, marksheetId);
                setMarksheet(data);
            } catch (err) {
                console.error(err);
            }
        };

        fetchMarksheet();
    }, [projectId, marksheetId]);

    if (!marksheet) return <div className="p-6">Loading...</div>;

    const mainCorrected = marksheet.corrected
        ? JSON.parse(marksheet.corrected)
        : [];

    return (
        <div className="p-8 bg-gray-100 min-h-screen">
            <div className="mb-6">
                <button
                    onClick={() => navigate(`/project/${projectId}/view`)}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-lg shadow"
                >
                    ← Go To Project
                </button>
            </div>
            <div className="bg-white rounded-xl shadow-xl p-8 flex gap-10">

                {/* LEFT SIDE - IMAGE */}
                <div className="w-1/2">
                    <img
                        src={`${process.env.REACT_APP_SERVER_URL}/files/${marksheet.url?.split("/").pop()}`}
                        alt="marksheet"
                        className="w-full border rounded-lg shadow-lg"
                    />
                </div>

                {/* RIGHT SIDE - COMPLETE DETAILS */}
                <div className="w-1/2 space-y-8 text-base">

                    {/* ================= STUDENT DETAILS ================= */}
                    <div>
                        {/* <h2 className="text-2xl font-bold mb-4">Student Details</h2> */}
                        <div className="grid grid-cols-2 gap-3">
                            <p><strong>Marksheet ID:</strong> {marksheet.id}</p>
                            <p><strong>Student Name:</strong> {marksheet.studentName}</p>
                            <p><strong>Father Name:</strong> {marksheet.fatherName ?? "NULL"}</p>
                            <p><strong>Mother Name:</strong> {marksheet.motherName ?? "NULL"}</p>
                            <p><strong>Seat No:</strong> {marksheet.seatNo}</p>
                            <p><strong>School Centre No:</strong> {marksheet.schoolCentreNo}</p>
                            <p><strong>School Index No:</strong> {marksheet.schoolIndexNo}</p>
                            <p><strong>Group:</strong> {marksheet.group}</p>
                            <p><strong>Year:</strong> {marksheet.year ?? "NULL"}</p>
                            <p><strong>Year Of Passing:</strong> {marksheet.yearOfPassing}</p>
                            <p><strong>Result Status:</strong> {marksheet.resultStatus}</p>
                            <p><strong>Obtained Grade:</strong> {marksheet.obtainedGrade}</p>
                            <p><strong>Obtained Percentage:</strong> {marksheet.obtainedPercentage}</p>
                            <p><strong>Obtained Percentile:</strong> {marksheet.obtainedPercentile}</p>
                            <p><strong>Total Obtained Marks:</strong> {marksheet.totalObtainedMarks}</p>
                            <p><strong>Total Out Of Marks:</strong> {marksheet.totalOutOfMarks}</p>
                        </div>
                    </div>

                    {/* ================= SUBJECT DETAILS (TABLE FORMAT) ================= */}
                    <div>
                        {/* <h2 className="text-2xl font-bold mb-4">Marks</h2> */}

                        <div className="overflow-x-auto">
                            <table className="min-w-full border border-gray-300 text-sm">
                                <thead className="bg-gray-200">
                                    <tr>
                                        <th className="border p-2">Subject ID</th>
                                        <th className="border p-2">Code</th>
                                        <th className="border p-2">Subject Name</th>
                                        <th className="border p-2 text-center">Obtained</th>
                                        <th className="border p-2 text-center">Out Of</th>
                                        <th className="border p-2">Obtained In Words</th>
                                        <th className="border p-2 text-center">Grade</th>
                                        <th className="border p-2">Raw Corrected</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {marksheet.markResponseList.map((subject) => {
                                        const subjectCorrected = subject.corrected
                                            ? JSON.parse(subject.corrected)
                                            : [];

                                        return (
                                            <React.Fragment key={subject.id}>
                                                <tr className="bg-white hover:bg-gray-50">
                                                    <td className="border p-2">{subject.id}</td>
                                                    <td className="border p-2">{subject.subjectCode}</td>
                                                    <td className="border p-2">{subject.subjectName}</td>
                                                    <td className="border p-2 text-center">{subject.obtained}</td>
                                                    <td className="border p-2 text-center">{subject.subjectOutOfMarks}</td>
                                                    <td className="border p-2">{subject.obtainedInWords}</td>
                                                    <td className="border p-2 text-center">{subject.subjectGrade}</td>
                                                    {/* <td className="border p-2">{subject.corrected}</td> */}
                                                    <td className="border p-2">
                                                        {subjectCorrected.length > 0 && (
                                                            <div className="overflow-x-auto">
                                                                {/* <div className="bg-yellow-50 p-4 rounded border"> */}
                                                                {/* <h3 className="font-semibold mb-3">Main Corrections</h3> */}

                                                                <table className="min-w-full border border-gray-300 text-sm">
                                                                    <thead className="bg-yellow-100">
                                                                        {/* <tr>
                                                                                <th className="border px-3 py-2 text-left">Field Name</th>
                                                                                <th className="border px-3 py-2 text-left">OCR Value</th>
                                                                                <th className="border px-3 py-2 text-left">Corrected Value</th>
                                                                            </tr> */}
                                                                    </thead>
                                                                    <tbody>
                                                                        {mainCorrected.map((c, index) => (
                                                                            <tr key={index} className="bg-white">
                                                                                <td className="border px-3 py-2">{c.fieldName}</td>
                                                                                <td className="border px-3 py-2 text-red-600">
                                                                                    {c.ocrValue}
                                                                                </td>
                                                                                <td className="border px-3 py-2 text-green-600 font-medium">
                                                                                    {c.correctedValue}
                                                                                </td>
                                                                            </tr>
                                                                        ))}
                                                                    </tbody>
                                                                </table>
                                                                {/* </div> */}
                                                            </div>
                                                        )}
                                                    </td>
                                                </tr>
                                            </React.Fragment>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                    </div>

                    {/* ================= BOARD DETAILS ================= */}
                    <div>
                        <h2 className="text-2xl font-bold mb-4">Board Details</h2>
                        <div className="grid grid-cols-2 gap-3">
                            <p><strong>ID:</strong> {marksheet.board?.id ?? "NULL"}</p>
                            <p><strong>Full Name:</strong> {marksheet.board?.fullName ?? "NULL"}</p>
                            <p><strong>Short Name:</strong> {marksheet.board?.shortName ?? "NULL"}</p>
                            <p><strong>State:</strong> {marksheet.board?.state ?? "NULL"}</p>
                            <p><strong>City:</strong> {marksheet.board?.city ?? "NULL"}</p>
                            {/* <p><strong>Created At:</strong> {marksheet.board?.createdAt ?? "NULL"}</p> */}
                            {/* <p><strong>Updated At:</strong> {marksheet.board?.updatedAt ?? "NULL"}</p> */}
                        </div>
                    </div>

                    {/* ================= MAIN CORRECTED ================= */}
                    <div>
                        {/* <p><strong>Raw Corrected:</strong> {marksheet.corrected}</p> */}

                        {mainCorrected.length > 0 && (
                            <div className="mt-4 overflow-x-auto">
                                {/* <div className="p-4 rounded border"> */}
                                {/* <h3 className="font-semibold mb-3">Main Corrections</h3> */}

                                <table className="min-w-full border border-gray-300 text-sm">
                                    <thead className="bg-yellow-100">
                                        <tr>
                                            <th className="border px-3 py-2 text-left">Field Name</th>
                                            <th className="border px-3 py-2 text-left">OCR Value</th>
                                            <th className="border px-3 py-2 text-left">Corrected Value</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {mainCorrected.map((c, index) => (
                                            <tr key={index} className="bg-white">
                                                <td className="border px-3 py-2">{c.fieldName}</td>
                                                <td className="border px-3 py-2 text-red-600">
                                                    {c.ocrValue}
                                                </td>
                                                <td className="border px-3 py-2 text-green-600 font-medium">
                                                    {c.correctedValue}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                                {/* </div> */}
                            </div>
                        )}
                    </div>

                    {/* ================= PROCESSING DETAILS ================= */}
                    <div>
                        <h2 className="text-2xl font-bold mb-4">Processing Details</h2>
                        <div className="grid grid-cols-2 gap-3">
                            <p><strong>Processing Status:</strong> {marksheet.processingStatus}</p>
                            <p><strong>Processing Duration:</strong> {marksheet.processingDuration}</p>
                            <p><strong>Processing Started At:</strong> {marksheet.processingStartedAt}</p>
                            <p><strong>Created At:</strong> {marksheet.createdAt}</p>
                            <p><strong>Updated At:</strong> {marksheet.updatedAt}</p>
                            <p className="break-all">
                                <strong>URL:</strong>{" "}
                                <a
                                    href={`${process.env.REACT_APP_SERVER_URL}/files/${marksheet.url?.split("/").pop()}`}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="text-blue-600 underline"
                                >
                                    {marksheet.url}
                                </a>
                            </p>                        </div>
                    </div>

                    {/* ================= VERIFICATION DETAILS ================= */}
                    <div>
                        <h2 className="text-2xl font-bold mb-4">Verification Details</h2>
                        <div className="grid grid-cols-2 gap-3">
                            <p><strong>Verification Status:</strong> {marksheet.verificationStatus}</p>
                            <p><strong>Verified By User:</strong> {marksheet.verifiedByUser ?? "NULL"}</p>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
};

export default ViewMarksheet;