import type { ReactNode } from "react";

type SeverityRowProps = {
  level: string;
  badgeClasses: string;
  viewCells: ReactNode;
  editCells: ReactNode;
  viewLevelCell?: ReactNode;
  editLevelCell?: ReactNode;
  isEditing?: boolean;
  onEdit?: () => void;
  onSave?: () => void;
  onCancel?: () => void;
};

export const SeverityRow = ({
  level,
  badgeClasses,
  viewCells,
  editCells,
  viewLevelCell,
  editLevelCell,
  isEditing = false,
  onEdit,
  onSave,
  onCancel,
}: SeverityRowProps) => {
  const handleEdit = () => onEdit?.();
  const handleCancel = () => onCancel?.();
  const handleSave = () => onSave?.();

  const levelCell = isEditing
    ? editLevelCell ?? (
        <td className="px-6 py-6 align-top">
          <span className={`px-3 py-1 font-label-bold text-sm rounded-full border ${badgeClasses}`}>
            {level}
          </span>
        </td>
      )
    : viewLevelCell ?? (
        <td className="px-6 py-6 align-top">
          <span className={`px-3 py-1 font-label-bold text-sm rounded-full border ${badgeClasses}`}>
            {level}
          </span>
        </td>
      );

  return (
    <tr className="group hover:bg-surface-container/30 transition-colors">
      {levelCell}
      {isEditing ? editCells : viewCells}
      <td className="px-6 py-6 text-right align-top">
        {isEditing ? (
          <div className="flex justify-end gap-2">
            <button
              type="button"
              className="px-3 py-1.5 rounded-[14px] border border-outline text-on-surface hover:bg-surface-container transition-all text-xs font-body-sm font-bold"
              onClick={handleCancel}
            >
              Cancel
            </button>
            <button
              type="button"
              className="px-3 py-1.5 rounded-[14px] bg-[#0B2CA1] text-white hover:bg-[#091F85] shadow-[0_16px_32px_-18px_rgba(11,44,161,0.35)] transition-all text-xs font-body-sm font-bold"
              onClick={handleSave}
            >
              Save
            </button>
          </div>
        ) : (
          <button
            type="button"
            className="px-3 py-1.5 rounded-[14px] border border-primary text-primary hover:bg-surface-container transition-all text-xs font-body-sm font-bold"
            onClick={handleEdit}
          >
            Edit
          </button>
        )}
      </td>
    </tr>
  );
};
