import { ChevronLeft, ChevronRight } from "lucide-react";
import type { PaginationMetadata } from "@/types/api";

interface PaginationProps {
  metadata: PaginationMetadata | null;
  page: number;
  pageSize: number;
  onPageChange: (newPage: number) => void;
  onPageSizeChange: (newSize: number) => void;
}

export const Pagination = ({ metadata, page, pageSize, onPageChange, onPageSizeChange }: PaginationProps) => {
  if (!metadata) return null;

  return (
    <div className="px-6 py-4 border-t border-outline-variant bg-surface flex flex-col sm:flex-row items-center justify-between gap-4">
      <div className="text-body-sm text-on-surface-variant flex items-center gap-4">
        <div>
          Showing page <span className="font-medium text-on-surface">{metadata.currentPage}</span> of{" "}
          <span className="font-medium text-on-surface">{metadata.totalPage}</span>
        </div>
        <div className="hidden sm:flex items-center gap-2">
          <span>Rows per page:</span>
          <select 
            value={pageSize}
            onChange={(e) => onPageSizeChange(Number(e.target.value))}
            className="bg-surface border border-outline-variant rounded px-2 py-1 text-body-sm focus:outline-none focus:ring-1 focus:ring-primary"
          >
            <option value={10}>10</option>
            <option value={25}>25</option>
            <option value={50}>50</option>
          </select>
        </div>
      </div>
      <div className="flex items-center gap-2">
        <button
          disabled={!metadata.hasPrevious}
          onClick={() => onPageChange(page - 1)}
          className="p-1.5 rounded-lg border border-outline-variant text-on-surface-variant disabled:opacity-50 disabled:cursor-not-allowed hover:bg-surface-container-low transition-colors"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>
        <button className="w-8 h-8 rounded-lg bg-primary !text-white text-label-md font-medium flex items-center justify-center">
          {metadata.currentPage}
        </button>
        <button
          disabled={!metadata.hasNext}
          onClick={() => onPageChange(page + 1)}
          className="p-1.5 rounded-lg border border-outline-variant text-on-surface-variant disabled:opacity-50 disabled:cursor-not-allowed hover:bg-surface-container-low transition-colors"
        >
          <ChevronRight className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
};
