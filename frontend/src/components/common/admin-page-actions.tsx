import type { ReactNode } from "react";

interface AdminPageActionsProps {
  onCancel?: () => void;
  onSave?: () => void;
  cancelText?: string;
  saveText?: string;
  saveIcon?: ReactNode;
  cancelClassName?: string;
  saveClassName?: string;
}

export const AdminPageActions = ({
  onCancel,
  onSave,
  cancelText = "Cancel",
  saveText = "Save Changes",
  saveIcon,
  cancelClassName = "w-full sm:w-auto px-6 py-3 rounded-[14px] border border-outline text-on-surface hover:bg-surface-container transition-all font-body-sm font-bold",
  saveClassName = "w-full sm:w-auto px-7 py-3 rounded-[14px] bg-[#0B2CA1] text-white hover:bg-[#091F85] shadow-[0_20px_40px_-20px_rgba(11,44,161,0.35)] transition-all font-body-sm font-bold flex items-center justify-center gap-2",
}: AdminPageActionsProps) => {
  return (
    <div className="max-w-container-max mx-auto flex flex-col sm:flex-row items-center justify-end gap-3">
      <button type="button" className={cancelClassName} onClick={onCancel}>
        {cancelText}
      </button>
      <button type="button" className={saveClassName} onClick={onSave}>
        {saveIcon}
        {saveText}
      </button>
    </div>
  );
};
