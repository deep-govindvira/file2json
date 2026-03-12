import { useEffect, useState } from "react";
import { getAdmins, registerSuperAdmin } from "../api/authService";
import { getAllDepartments } from "../api/departmentService";
import { toast } from "react-toastify";

function SuperAdminDashboard() {

  const [admins, setAdmins] = useState([]);
  const [departments, setDepartments] = useState([]);

  const [form, setForm] = useState({
    email: "",
    department: ""
  });

  const fetchAdmins = async () => {
    try {
      const data = await getAdmins();
      setAdmins(data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchDepartments = async () => {
    try {
      const data = await getAllDepartments();
      setDepartments(data);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    fetchAdmins();
    fetchDepartments();
  }, []);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await registerSuperAdmin(form);

      setForm({
        email: "",
        department: ""
      });

      fetchAdmins();
      toast.success("Admin added successfully.");
    } catch (error) {
      toast.error("Failed to add.");
      console.error(error);
    }
  };

  return (
    <div className="p-10 max-w-6xl mx-auto space-y-10">

      <h1 className="text-3xl font-bold text-gray-800">
        Super Admin Dashboard
      </h1>

      <div className="bg-white shadow-md rounded-xl p-6 max-w-md">

        <h2 className="text-xl font-semibold mb-4">
          Register Department Admin
        </h2>

        <form onSubmit={handleSubmit} className="space-y-4">

          <input
            type="email"
            name="email"
            placeholder="Department Admin Email"
            value={form.email}
            onChange={handleChange}
            autoComplete="off"
            className="w-full border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
          />

          <select
            name="department"
            value={form.department}
            onChange={handleChange}
            className="w-full border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
          >
            <option value="">Select Department</option>

            {departments.map((dept) => (
              <option key={dept.id} value={dept.id}>
                {dept.name}
              </option>
            ))}

          </select>

          <button
            type="submit"
            className="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 transition"
          >
            Register Admin
          </button>

        </form>

      </div>

      <div className="bg-white shadow-md rounded-xl p-6">

        <h2 className="text-xl font-semibold mb-4">
          Admin List
        </h2>

        <div className="overflow-x-auto">

          <table className="w-full border border-gray-200">

            <thead className="bg-gray-100">
              <tr>
                <th className="p-3 border text-left">Name</th>
                <th className="p-3 border text-left">Email</th>
                <th className="p-3 border text-left">Department</th>
              </tr>
            </thead>

            <tbody>

              {admins.map((admin) => (
                <tr key={admin.userId} className="hover:bg-gray-50">
                  <td className="p-3 border">{admin.name}</td>
                  <td className="p-3 border">{admin.email}</td>
                  <td className="p-3 border">{admin.department}</td>
                </tr>
              ))}

            </tbody>

          </table>

        </div>

      </div>

    </div>
  );
}

export default SuperAdminDashboard;