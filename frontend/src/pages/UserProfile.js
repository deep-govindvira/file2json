import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { updateProfile, getUserInfo } from "../api/authService";

const UserProfile = () => {
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const data = await getUserInfo();
        setName(data.name);
      } catch (error) {
        toast.error("Failed to load user info");
      }
    };

    fetchUser();
  }, []);

  const handleUpdate = async (e) => {
    e.preventDefault();

    // Validate only if password entered
    if (password) {
      if (password !== confirmPassword) {
        toast.error("Passwords do not match");
        return;
      }
    }

    try {
      await updateProfile({
        name,
        password: password || undefined, // send only if exists
      });

      toast.success("Profile updated successfully");
      setPassword("");
      setConfirmPassword("");
    } catch (error) {
      toast.error("Failed to update profile");
    }
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100">
      <div className="bg-white shadow-lg rounded-lg w-full max-w-md p-6">

        <h2 className="text-2xl font-semibold mb-6 text-center">
          Update Profile
        </h2>

        <form onSubmit={handleUpdate} className="space-y-4">

          {/* Name */}
          <div>
            <label className="block text-sm mb-1">Name</label>
            <input
              type="text"
              className="w-full border rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Enter new name"
            />
          </div>

          {/* Password */}
          <div>
            <label className="block text-sm mb-1">New Password</label>
            <input
              type="password"
              className="w-full border rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Leave blank to keep current password"
            />
            <p className="text-xs text-gray-500 mt-1">
              Leave blank if you don't want to change your password.
            </p>
          </div>

          {/* Confirm Password */}
          <div>
            <label className="block text-sm mb-1">Confirm Password</label>
            <input
              type="password"
              className="w-full border rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirm new password"
            />
          </div>

          <button
            type="submit"
            className="w-full bg-blue-500 text-white py-2 rounded-md hover:bg-blue-600 transition"
          >
            Update Profile
          </button>

        </form>
      </div>
    </div>
  );
};

export default UserProfile;