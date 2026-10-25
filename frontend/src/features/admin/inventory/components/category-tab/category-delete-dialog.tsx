import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

interface CategoryDeleteDialogProps {
  open: boolean;
  categoryName: string;
  isDeleting: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: () => void;
}

export function CategoryDeleteDialog({
  open,
  categoryName,
  isDeleting,
  onOpenChange,
  onConfirm,
}: CategoryDeleteDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-sm rounded-2xl border border-slate-200 bg-white p-6 shadow-xl" showCloseButton={false}>
        <DialogHeader className="space-y-2">
          <DialogTitle className="text-lg font-semibold text-slate-900">Delete category</DialogTitle>
          <DialogDescription className="text-sm text-slate-500">
            This will permanently delete <span className="font-semibold text-slate-700">{categoryName}</span>. This action cannot be undone.
          </DialogDescription>
        </DialogHeader>

        <DialogFooter className="-mx-6 -mb-6 rounded-b-2xl border-t border-slate-200 bg-slate-50 px-6 py-4">
          <Button
            className="h-10 min-w-24 rounded-md border border-slate-300 bg-white px-5 text-sm font-semibold text-slate-600 hover:bg-slate-50"
            disabled={isDeleting}
            onClick={() => onOpenChange(false)}
            type="button"
            variant="outline"
          >
            Cancel
          </Button>
          <Button
            className="h-10 min-w-24 rounded-md bg-red-600 px-5 text-sm font-semibold text-white hover:bg-red-700"
            disabled={isDeleting}
            onClick={onConfirm}
            type="button"
          >
            {isDeleting ? "Deleting..." : "Delete"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}