import React from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { clearUserId } from '../reducers/userIdSlice';

function Dashboard() {
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const handleLogout = () => {
        dispatch(clearUserId());
        navigate('/login');
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-4xl font-bold mb-8">Dashboard Page</h1>
            <button
                onClick={handleLogout}
                className="px-6 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition"
            >
                Logout
            </button>

            <button
                onClick={() => navigate('/create-project')}
                className="px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
            >
                Create Project
            </button>

        </div>
    );
}

export default Dashboard;