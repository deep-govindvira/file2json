import { Link } from 'react-router-dom'; // Make sure you have react-router-dom installed

function Home() {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <h1 className="text-4xl font-bold mb-8">Welcome to Home Page</h1>
            <div className="flex space-x-4">
                <Link
                    to="/login"
                    className="px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
                >
                    Login
                </Link>
                <Link
                    to="/register"
                    className="px-6 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition"
                >
                    Register
                </Link>
            </div>
        </div>
    );
}

export default Home;