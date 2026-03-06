function BlueButton(props) {
    const {
        type = "submit",
        label = "No Label",
        onClick = () => { },
        invisible = false,
        disabled = false,
        loading = false,
        loadingText = "Wait",
        extraClass = ""
    } = props;

    return (
        <button
            type={type}
            onClick={onClick}
            disabled={disabled}
            className={`
        px-4 py-2 rounded-md border
        bg-white text-blue-500 border-blue-500
        transition-colors duration-300
        ${!disabled && "hover:bg-blue-500 hover:text-white"}
        ${invisible ? "invisible" : ""}
        ${disabled ? "cursor-not-allowed opacity-50" : ""}
        ${extraClass}
    `}
        >
            {loading ? loadingText : label}
        </button>
    );
}

export default BlueButton;