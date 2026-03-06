import { useEffect, useState } from "react";

function CenteredFullPageSpinner({ message = "Loading, please wait..." }) {
    const [show, setShow] = useState(false);

    useEffect(() => {
        const timer = setTimeout(() => {
            setShow(true);
        }, 1000); // 1 second delay

        return () => clearTimeout(timer); // cleanup
    }, []);

    if (!show) return null; // don't render before 1s

    return (
        <div className="flex flex-col items-center justify-center h-screen">
            <div className="h-12 w-12 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
            <p className="mt-4 text-gray-600 text-sm">
                {message}
            </p>
        </div>
    );
}

export default CenteredFullPageSpinner;