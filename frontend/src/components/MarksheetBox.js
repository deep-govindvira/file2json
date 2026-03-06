function MarksheetBox({
    id,
    index,
    status,
    isSelected,
    colorClass,
    handleMouseDown,
    handleMouseEnter,
    navigate,
    projectId,
}) {
    return (
        <div
            title={`#${index + 1} - ${status}`}
            onMouseDown={() => handleMouseDown(id)}
            onMouseEnter={() => handleMouseEnter(id)}
            onDoubleClick={() =>
                navigate(`/project/${projectId}/marksheet/${id}/view`)
            }
            className={`
        h-6 rounded-sm cursor-pointer transition-all duration-200
        ${colorClass}
        ${isSelected ? "ring-2 ring-black scale-110" : ""}
      `}
        />
    );
}

export default MarksheetBox;