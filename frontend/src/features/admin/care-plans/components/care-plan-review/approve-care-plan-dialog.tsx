import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Field, FieldGroup } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import Text from "../../ui/Text";
import Title from "../../ui/Title";
import { useState } from "react";

type ApproveCarePlanDialogProps = {
  actorReviewName: string;
  actorReviewRoleName: string;
  onApprove?: (password: string) => Promise<void>;
};

export default function ApproveCarePlanDialog({
  actorReviewName,
  actorReviewRoleName,
  onApprove,
}: ApproveCarePlanDialogProps) {
  const [open, setOpen] = useState(false);
  const [password, setPassword] = useState("");
  const [attested, setAttested] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!attested) return;

    try {
      setIsSubmitting(true);

      await onApprove?.(password);

      // Reset form
      setPassword("");
      setAttested(true);

      // Close dialog
      setOpen(false);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger
        render={
          <Button className="min-h-14 w-full border-2 border-red-300 text-white">
            Approve & e-Sign
          </Button>
        }
      />

      <DialogContent className="w-[420px] rounded-xl p-6">
        <form onSubmit={handleSubmit}>
          <DialogHeader className="space-y-2">
            <DialogTitle>
              <Title className="text-[24px] font-bold">
                Electronic Signature Required
              </Title>
            </DialogTitle>

            <DialogDescription>
              <Text className="text-sm text-gray-500">
                Approving activates this care plan and generates downstream
                tasks & billing.
              </Text>
            </DialogDescription>
          </DialogHeader>

          <div className="mt-5 space-y-5">
            {/* Reviewer */}
            <div className="flex items-center gap-3 rounded-lg border border-gray-200 p-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-full border text-gray-500">
                {actorReviewName.charAt(0).toUpperCase()}
              </div>

              <div>
                <Title className="text-base">
                  {actorReviewName}, {actorReviewRoleName}
                </Title>

                <Text className="text-sm text-gray-500">
                  Signing as {actorReviewRoleName}
                </Text>
              </div>
            </div>

            {/* Password */}
            <Field>
              <Label>Re-enter password to sign</Label>

              <Input
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </Field>

            {/* Checkbox */}
            <label className="flex items-start gap-2">
              <input
                type="checkbox"
                checked={attested}
                onChange={(e) => setAttested(e.target.checked)}
                className="mt-1"
              />

              <Text className="text-sm text-gray-700">
                I attest this plan is compliant and ready for activation.
              </Text>
            </label>

            {/* Timestamp */}
            <div>
              <Text className="text-xs text-gray-500">
                Signature timestamp: {new Date().toLocaleString()}
              </Text>

              <Text className="text-xs text-gray-400">
                Recorded immutably in audit log.
              </Text>
            </div>
          </div>

          <DialogFooter className="mt-6 flex gap-3">
            <DialogClose
              render={
                <Button type="button" variant="outline" className="flex-1">
                  Cancel
                </Button>
              }
            />

            <Button
              type="submit"
              className="flex-1"
              disabled={!password || !attested || isSubmitting}
            >
              {isSubmitting ? "Signing..." : "Sign & Approve"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
