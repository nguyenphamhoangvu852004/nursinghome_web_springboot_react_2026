import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useDialogStore } from "@/store/use-dialog-store";

export const DialogModal = () => {
  const { isOpen, title, description, content, close } = useDialogStore();
  return (
    <Dialog open={isOpen} onOpenChange={(v) => !v && close()}>
      <DialogContent>
        {(title || description) && (
          <DialogHeader>
            {title && <DialogTitle>{title}</DialogTitle>}
            {description && (
              <DialogDescription>{description}</DialogDescription>
            )}
          </DialogHeader>
        )}
        <ScrollArea className={"max-h-[80vh]"}>
          {content}
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
};
