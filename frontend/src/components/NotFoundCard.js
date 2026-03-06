import { useNavigate } from "react-router-dom";

const NotFoundCard = ({
  title = "Not Found",
  message = "The requested resource could not be found.",
  buttonText = "Go Back",
  backTo = -1, // default: go back in history,
  showButton = true
}) => {
  const navigate = useNavigate();

  const handleClick = () => {
    if (typeof backTo === "number") {
      navigate(backTo);
    } else {
      navigate(backTo);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-100 to-gray-200">
      <div className="bg-white p-10 rounded-2xl shadow-xl border border-gray-200 text-center max-w-md w-full">
        <h2 className="text-2xl font-bold text-gray-800 mb-3">
          {title}
        </h2>

        <p className="text-gray-500 mb-6">
          {message}
        </p>

        {showButton && <button
          onClick={handleClick}
          className="group inline-flex items-center gap-2 px-6 py-3 
                     bg-gradient-to-r from-blue-600 to-indigo-600 
                     text-white font-semibold rounded-xl 
                     shadow-md hover:shadow-lg 
                     hover:scale-105 active:scale-95 
                     transition-all duration-300 ease-in-out"
        >
          <span className="transition-transform group-hover:-translate-x-1">
            ←
          </span>
          {buttonText}
        </button>}

      </div>
    </div>
  );
};

export default NotFoundCard;