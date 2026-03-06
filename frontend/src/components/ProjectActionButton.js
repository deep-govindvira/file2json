function ProjectActionButton(props) {
    const {
        label,
        onClick,
        show,
        disabled,
        extraClass = "",
        loadingText
    } = props;

    return (
        <button
            onClick={onClick}
            disabled={disabled}
            className={`
                ml-20 px-4 py-2 rounded border
                bg-white text-blue-500 border-blue-500
                hover:bg-blue-500 hover:text-white
                transition-colors duration-300
                ${show ? "" : "invisible"}
                ${disabled ? "cursor-not-allowed opacity-50 hover:bg-white hover:text-blue-500" : ""}
            `}
        >
            {loadingText && disabled ? loadingText : label}
        </button>
    );
}

export default ProjectActionButton;