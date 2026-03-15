import { useEffect, useState } from "react";
import {
  getAdmins,
  registerSuperAdmin,
} from "../api/authService";

import {
  getAllDepartments,
  createDepartment,
  deleteDepartment,
} from "../api/departmentService";

import { toast } from "react-toastify";

function SuperAdminDashboard() {
  const [admins, setAdmins] = useState([]);
  const [departments, setDepartments] = useState([]);

  // ⚠️ KEEP ORIGINAL API FORMAT
  const [form, setForm] = useState({
    email: "",
    department: "",
  });

  const [newDept, setNewDept] = useState("");
  const [deleteId, setDeleteId] = useState(null);

  // ================= FETCH =================

  const fetchAdmins = async () => {
    try {
      const data = await getAdmins();
      setAdmins(data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchDepartments = async () => {
    try {
      const data = await getAllDepartments();
      setDepartments(data);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchAdmins();
    fetchDepartments();
  }, []);

  // ================= FORM =================

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleRegisterAdmin = async (e) => {
    e.preventDefault();

    if (!form.email) return toast.error("Enter email");
    if (!form.department)
      return toast.error("Select department");

    try {
      await registerSuperAdmin(form);

      setForm({ email: "", department: "" });
      fetchAdmins();

      toast.success("Admin registered");
    } catch (err) {
      toast.error("Registration failed");
      console.error(err);
    }
  };

  // ================= DEPARTMENT =================

  const handleAddDepartment = async () => {
    if (!newDept)
      return toast.error("Enter department name");

    try {
      await createDepartment(newDept);
      setNewDept("");
      fetchDepartments();

      toast.success("Department added");
    } catch {
      toast.error("Add failed");
    }
  };

  const confirmDeleteDepartment = (id) => {
    setDeleteId(id);
  };

  const handleDeleteDepartment = async () => {
    try {
      await deleteDepartment(deleteId);
      setDeleteId(null);
      fetchDepartments();

      toast.success("Department removed");
    } catch {
      toast.error("Delete failed");
    }
  };

  // ================= UI =================

  return (
    <div className="min-h-screen bg-gray-100 p-8">

      <div className="max-w-7xl mx-auto space-y-8">

        {/* ===== TITLE ===== */}
        <h1 className="text-3xl font-bold text-gray-800">
          Super Admin Dashboard
        </h1>

        {/* ===== TOP GRID ===== */}
        <div className="grid md:grid-cols-2 gap-8">

          {/* ===== REGISTER ADMIN ===== */}
          <div className="bg-white rounded-2xl shadow p-6">

            <h2 className="text-xl font-semibold mb-6">
              Register Department Admin
            </h2>

            <form
              onSubmit={handleRegisterAdmin}
              className="space-y-4"
            >
              <input
                type="email"
                name="email"
                placeholder="Admin email"
                value={form.email}
                onChange={handleChange}
                className="w-full border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-400 outline-none"
              />

              <select
                name="department"
                value={form.department}
                onChange={handleChange}
                className="w-full border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-400 outline-none"
              >
                <option value="">
                  Select Department
                </option>

                {departments.map((dept) => (
                  <option key={dept.id} value={dept.id}>
                    {dept.name}
                  </option>
                ))}
              </select>

              <button
                type="submit"
                className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition"
              >
                Register Admin
              </button>
            </form>
          </div>

          {/* ===== MANAGE DEPARTMENTS ===== */}
          <div className="bg-white rounded-2xl shadow p-6">

            <h2 className="text-xl font-semibold mb-6">
              Manage Departments
            </h2>

            {/* Add Department */}
            <div className="flex gap-3 mb-6">
              <input
                type="text"
                placeholder="Department name"
                value={newDept}
                onChange={(e) =>
                  setNewDept(e.target.value)
                }
                className="flex-1 border rounded-lg px-4 py-2 focus:ring-2 focus:ring-green-400 outline-none"
              />

              <button
                onClick={handleAddDepartment}
                className="bg-green-500 text-white px-5 rounded-lg hover:bg-green-600 transition"
              >
                Add
              </button>
            </div>

            {/* Department List */}
            <div className="max-h-72 overflow-y-auto space-y-2">

              {departments.map((dept) => (
                <div
                  key={dept.id}
                  className="flex justify-between items-center border rounded-lg px-4 py-2"
                >
                  <span className="font-medium">
                    {dept.name}
                  </span>

                  <button
                    onClick={() =>
                      confirmDeleteDepartment(dept.id)
                    }
                    className="text-red-500 hover:text-red-700 font-semibold"
                  >
                    Remove
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* ===== ADMIN LIST ===== */}
        <div className="bg-white rounded-2xl shadow p-6">

          <h2 className="text-xl font-semibold mb-6">
            Admin List
          </h2>

          <div className="overflow-x-auto">

            <table className="w-full border border-gray-200">

              <thead className="bg-gray-50">
                <tr>
                  <th className="p-3 border text-left">
                    Name
                  </th>
                  <th className="p-3 border text-left">
                    Email
                  </th>
                  <th className="p-3 border text-left">
                    Department
                  </th>
                </tr>
              </thead>

              <tbody>

                {admins.map((admin) => (
                  <tr
                    key={admin.userId}
                    className="hover:bg-gray-50"
                  >
                    <td className="p-3 border">
                      {admin.name}
                    </td>

                    <td className="p-3 border">
                      {admin.email}
                    </td>

                    <td className="p-3 border">
                      {admin.department}
                    </td>
                  </tr>
                ))}

              </tbody>
            </table>

          </div>
        </div>

      </div>

      {/* ===== DELETE CONFIRM MODAL ===== */}
      {deleteId && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

          <div className="bg-white rounded-xl shadow-lg p-6 w-96">

            <h3 className="text-lg font-semibold mb-2">
              Delete Department
            </h3>

            <p className="text-gray-600 mb-6">
              Are you sure you want to delete this
              department? This action cannot be undone.
            </p>

            <div className="flex justify-end gap-3">

              <button
                onClick={() => setDeleteId(null)}
                className="px-4 py-2 rounded-lg border hover:bg-gray-100"
              >
                Cancel
              </button>

              <button
                onClick={handleDeleteDepartment}
                className="px-4 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700"
              >
                Delete
              </button>

            </div>
          </div>
        </div>
      )}

    </div>
  );
}

export default SuperAdminDashboard;