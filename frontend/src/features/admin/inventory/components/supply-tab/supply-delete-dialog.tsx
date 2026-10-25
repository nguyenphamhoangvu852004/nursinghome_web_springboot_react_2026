import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

interface SupplyDeleteDialogProps {
  supplyName: string;
  isDeleting: boolean;
  open: boolean;
  onConfirm: () => void;
  onOpenChange: (open: boolean) => void;
}

export function SupplyDeleteDialog({
  supplyName,
  isDeleting,
  open,
  onConfirm,
  onOpenChange,
}: SupplyDeleteDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Delete supply</DialogTitle>
          <DialogDescription>
            Delete {supplyName} from the inventory register. This action can only succeed when the supply is retired.
          </DialogDescription>
        </DialogHeader>

        <DialogFooter>
          <Button disabled={isDeleting} onClick={() => onOpenChange(false)} variant="outline">
            Cancel
          </Button>
          <Button className="bg-red-600 text-white hover:bg-red-700" disabled={isDeleting} onClick={onConfirm}>
            {isDeleting ? "Deleting..." : "Delete"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}