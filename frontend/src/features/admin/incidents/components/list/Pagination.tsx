import React from "react";
import { Button } from "@/components/ui/button";
import { ChevronLeft, ChevronRight } from "lucide-react";

type PaginationProps = {
  currentPage: number;
  totalItems: number;
  pageSize: number;
  onPageChange: (page: number) => void;
};
function buildPageList(currentPage: number, totalPages: number): (number | "ellipsis")[] {
  const pages: (number | "ellipsis")[] = [];

  if (totalPages <= 7) {
    for (let i = 1; i <= totalPages; i++) pages.push(i);
    return pages;
  }

  const showLeftEllipsis = currentPage > 3;
  const showRightEllipsis = currentPage < totalPages - 2;

  pages.push(1);

  if (showLeftEllipsis) {
    pages.push("ellipsis");
  }

  const start = showLeftEllipsis ? Math.max(2, currentPage - 1) : 2;
  const end = showRightEllipsis ? Math.min(totalPages - 1, currentPage + 1) : totalPages - 1;

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }

  if (showRightEllipsis) {
    pages.push("ellipsis");
  }

  pages.push(totalPages);

  return pages;
}

export function Pagination({
  currentPage,
  totalItems,
  pageSize,
  onPageChange,
}: PaginationProps) {
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));

  // Chặn currentPage lệch khỏi phạm vi hợp lệ (vd. totalItems đổi sau khi filter)
  const safePage = Math.min(Math.max(1, currentPage), totalPages);

  const from = totalItems === 0 ? 0 : (safePage - 1) * pageSize + 1;
  const to = Math.min(safePage * pageSize, totalItems);

  const pageList = buildPageList(safePage, totalPages);

  const goTo = (page: number) => {
    if (page < 1 || page > totalPages || page === safePage) return;
    onPageChange(page);
  };

  return (
    <div className="flex flex-col items-center justify-between gap-4 border-t border-gray-200 px-6 py-4 sm:flex-row">
      <p className="text-sm text-slate-500">
        Showing <span className="font-semibold">{from}</span> to{" "}
        <span className="font-semibold">{to}</span> of{" "}
        <span className="font-semibold">{totalItems}</span> results
      </p>

      <div className="flex items-center gap-2">
        <Button
          variant="outline"
          size="icon"
          className="h-9 w-9"
          disabled={safePage <= 1}
          onClick={() => goTo(safePage - 1)}
        >
          <ChevronLeft className="size-4" />
        </Button>

        {pageList.map((page, idx) =>
          page === "ellipsis" ? (
            <span key={`ellipsis-${idx}`} className="px-2 text-slate-400">
              ...
            </span>
          ) : (
            <Button
              key={page}
              variant={page === safePage ? "default" : "outline"}
              className={
                page === safePage
                  ? "h-9 w-9 rounded-lg bg-blue-600 p-0 text-white hover:bg-blue-700"
                  : "h-9 w-9 rounded-lg p-0"
              }
              onClick={() => goTo(page)}
            >
              {page}
            </Button>
          )
        )}

        <Button
          variant="outline"
          size="icon"
          className="h-9 w-9"
          disabled={safePage >= totalPages}
          onClick={() => goTo(safePage + 1)}
        >
          <ChevronRight className="size-4" />
        </Button>
      </div>
    </div>
  );
}