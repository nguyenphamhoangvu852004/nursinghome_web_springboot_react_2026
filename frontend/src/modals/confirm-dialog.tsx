import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { useConfirmStore } from "@/store/use-confirm-store";

export const ConfirmDialog = () => {
  const {
    isOpen,
    title,
    description,
    confirmText,
    cancelText,
    onConfirm,
    close,
  } = useConfirmStore();

  return (
    <AlertDialog open={isOpen} onOpenChange={(v) => !v && close()}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{title ?? "Are you sure?"}</AlertDialogTitle>

          {description && (
            <AlertDialogDescription>{description}</AlertDialogDescription>
          )}
        </AlertDialogHeader>

        <AlertDialogFooter>
          <AlertDialogCancel onClick={close}>
            {cancelText ?? "Cancel"}
          </AlertDialogCancel>

          <AlertDialogAction
            onClick={() => {
              onConfirm?.();
              close();
            }}
          >
            {confirmText ?? "Confirm"}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
