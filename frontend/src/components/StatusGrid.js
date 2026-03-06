import { useNavigate } from "react-router-dom";
import MarksheetBox from "./MarksheetBox";

function StatusGrid({
    title,
    items,
    statusMap,
    selectedMarksheets,
    handleMouseDown,
    handleMouseEnter,
    gridColumns = 50,
    colorClass,
    projectId,
    isSelectable, // ✅ add this
}) {
    const navigate = useNavigate();

    if (!items || items.length === 0) return null;

    return (
        <div className="focus:text-lg bg-white mb-6 rounded-2xl border border-gray-200 p-6 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300">
            <h3 className="text-xl mb-4">{title} ({items.length})</h3>

            <div
                className="grid gap-1 select-none"
                style={{ gridTemplateColumns: `repeat(${gridColumns}, 1fr)` }}
            >
                {items.map((m, index) => {

                    return (
                        <MarksheetBox
                            key={m.id}
                            id={m.id}
                            index={index}
                            status={statusMap[m.id]}
                            isSelected={selectedMarksheets.includes(m.id)}
                            colorClass={colorClass}
                            handleMouseDown={isSelectable ? handleMouseDown : () => { }}
                            handleMouseEnter={isSelectable ? handleMouseEnter : () => { }}
                            navigate={navigate}
                            projectId={projectId}
                        />

                    );
                })}
            </div>
        </div>
    );
}

export default StatusGrid;