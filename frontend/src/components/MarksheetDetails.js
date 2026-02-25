import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getProjectById } from "../api/projectService";
import { getMarksheets } from "../api/marksheetService";

const MarksheetDetails = () => {
  const { projectId, marksheetId } = useParams();

  const [marksheet, setMarksheet] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      try {
        const project = await getProjectById(projectId);
        const marksheets = await getMarksheets(projectId);

        const found = marksheets?.find(
          (m) => m.id === marksheetId
        );

        if (!found) {
          setError("Marksheet not found.");
        } else {
          setMarksheet(found);
        }
      } catch (err) {
        console.error(err);
        setError("Failed to load marksheet.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [projectId, marksheetId]);

  if (loading) return <p className="p-6">Loading...</p>;
  if (error) return <p className="p-6 text-red-600">{error}</p>;
  if (!marksheet) return null;

  const fileName = marksheet.url?.split("/").pop();

  return (
    <div className="p-6 bg-gray-100 min-h-screen">

      <div className="bg-white p-6 rounded shadow">

        <h2 className="text-2xl font-bold mb-4">
          Marksheet Details
        </h2>

        {/* File Preview */}
        {fileName && (
          <div className="mb-6">
            <img
              src={`${process.env.REACT_APP_SERVER_URL}/files/${fileName}`}
              alt="marksheet"
              className="max-w-md border rounded shadow"
            />

            <button
              onClick={() =>
                window.open(
                  `${process.env.REACT_APP_SERVER_URL}/files/${fileName}`,
                  "_blank"
                )
              }
              className="mt-3 px-4 py-2 bg-blue-600 text-white rounded"
            >
              Open File
            </button>
          </div>
        )}

        {/* Basic Info */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-base">

          <p><b>Student:</b> {marksheet.studentName}</p>
          <p><b>Seat No:</b> {marksheet.seatNo}</p>
          <p><b>Father:</b> {marksheet.fatherName}</p>
          <p><b>Mother:</b> {marksheet.motherName}</p>
          <p><b>Board:</b> {marksheet.board?.shortName}</p>
          <p><b>Year:</b> {marksheet.year}</p>
          <p><b>Result:</b> {marksheet.resultStatus}</p>
          <p><b>Grade:</b> {marksheet.obtainedGrade}</p>
          <p><b>Percentage:</b> {marksheet.obtainedPercentage}</p>
          <p><b>Percentile:</b> {marksheet.obtainedPercentile}</p>
          <p>
            <b>Total Marks:</b>{" "}
            {marksheet.totalObtainedMarks}/{marksheet.totalOutOfMarks}
          </p>
          <p><b>Processing Status:</b> {marksheet.processingStatus}</p>
          <p><b>Verification Status:</b> {marksheet.verificationStatus}</p>
          <p><b>Uploaded At:</b> {marksheet.createdAt}</p>

        </div>

        {/* Subjects */}
        {marksheet.markResponseList?.length > 0 && (
          <div className="mt-8">
            <h3 className="text-xl font-semibold mb-3">
              Subjects
            </h3>

            <table className="w-full border">
              <thead className="bg-gray-200">
                <tr>
                  <th className="border p-2">Subject</th>
                  <th className="border p-2">Marks</th>
                  <th className="border p-2">Out Of</th>
                  <th className="border p-2">Grade</th>
                </tr>
              </thead>

              <tbody>
                {marksheet.markResponseList.map((sub) => (
                  <tr key={sub.id}>
                    <td className="border p-2">
                      {sub.subjectName} ({sub.subjectCode})
                    </td>
                    <td className="border p-2 text-center">
                      {sub.obtained ?? "ABSENT"}
                    </td>
                    <td className="border p-2 text-center">
                      {sub.subjectOutOfMarks}
                    </td>
                    <td className="border p-2 text-center font-bold">
                      {sub.subjectGrade}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

      </div>
    </div>
  );
};

export default MarksheetDetails;