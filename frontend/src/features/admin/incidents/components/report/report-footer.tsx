"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";

type Props = {
  onSubmit: () => Promise<void>;
};

export function ReportFooter({ onSubmit }: Props) {
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [successOpen, setSuccessOpen] = useState(false);
  const [errorOpen, setErrorOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);

  // Bấm "Report Incident" -> chỉ mở popup xác nhận, chưa submit
  const handleOpenConfirm = () => {
    setConfirmOpen(true);
  };

  // Bấm "Save" trong popup xác nhận -> mới thực sự submit
  const handleConfirmSave = async () => {
    try {
      setLoading(true);
      await onSubmit();

      setConfirmOpen(false);
      setSuccessOpen(true);
    } catch (error: any) {
      console.error(error);
      setConfirmOpen(false);
      setErrorMessage(
        error?.message || "Đã có lỗi xảy ra, vui lòng thử lại."
      );
      setErrorOpen(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <div className="flex flex-col gap-3 rounded-[20px] border border-border bg-card p-6 shadow-sm sm:flex-row sm:items-center sm:justify-end">
        <div className="flex flex-1 items-center text-sm text-muted-foreground">
          <p>Ready to submit? Review details before reporting.</p>
        </div>

        <div className="flex gap-3">
          <Button variant="outline">Cancel</Button>

          <Button onClick={handleOpenConfirm} disabled={loading}>
            {loading ? "Reporting..." : "Report Incident"}
          </Button>
        </div>
      </div>

      {/* Popup xác nhận trước khi lưu */}
      <Dialog open={confirmOpen} onOpenChange={setConfirmOpen}>
        <DialogContent className="sm:max-w-sm">
          <DialogHeader>
            <DialogTitle className="text-center">
              Bạn có muốn báo cáo sự cố này không?
            </DialogTitle>
          </DialogHeader>

          <div className="py-2 text-center text-gray-600">
            Vui lòng kiểm tra lại thông tin trước khi lưu.
          </div>

          <DialogFooter className="sm:justify-center">
            <Button
              variant="outline"
              className="flex-1"
              onClick={() => setConfirmOpen(false)}
              disabled={loading}
            >
              Cancel
            </Button>
            <Button
              className="flex-1"
              onClick={handleConfirmSave}
              disabled={loading}
            >
              {loading ? "Saving..." : "Save"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Popup báo thành công */}
      <Dialog open={successOpen} onOpenChange={setSuccessOpen}>
        <DialogContent className="sm:max-w-sm">
          <DialogHeader>
            <DialogTitle className="text-center">
              Incident Created Successfully
            </DialogTitle>
          </DialogHeader>

          <div className="py-2 text-center text-gray-600">
            Your incident has been reported successfully.
          </div>

          <DialogFooter>
            <Button className="w-full" onClick={() => setSuccessOpen(false)}>
              OK
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Popup báo lỗi (thay cho alert) */}
      <Dialog open={errorOpen} onOpenChange={setErrorOpen}>
        <DialogContent className="sm:max-w-sm">
          <DialogHeader>
            <DialogTitle className="text-center text-destructive">
              Không thể báo cáo sự cố
            </DialogTitle>
          </DialogHeader>

          <div className="py-2 text-center text-gray-600">
            {errorMessage}
          </div>

          <DialogFooter>
            <Button className="w-full" onClick={() => setErrorOpen(false)}>
              OK
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}