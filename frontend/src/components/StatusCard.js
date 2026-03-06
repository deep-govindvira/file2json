const StatusCard = ({ title, count, bgColor = "bg-gray-200", }) => {
    if (count === 0) return null

    return (
        <div className={`${bgColor} text-center rounded-2xl border border-gray-200 p-6 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300`}>
            {/* <div className={`${bgColor} p-4 rounded shadow text-center`}> */}
            {/* <h4 className="font-semibold">{title}</h4> */}
            {/* <p className="text-2xl">{count}</p> */}
            {/* </div> */}
            <h4 className="text-sm text-gray-500">{title}</h4>
            <p className="text-3xl font-bold text-gray-800 mt-2">{count}</p>
        </div>

    );
};

export default StatusCard;