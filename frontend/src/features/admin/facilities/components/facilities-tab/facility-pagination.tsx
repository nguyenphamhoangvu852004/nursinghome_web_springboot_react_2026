import { ChevronLeft, ChevronRight } from "lucide-react";
import type { PaginationMetadata } from "@/types/api";

interface FacilityPaginationProps {
  metadata: PaginationMetadata | null;
  page: number;
  onPageChange: (newPage: number) => void;
  totalElements?: number; // optionally pass this if you track total elements across the whole set
}

export const FacilityPagination = ({ metadata, page, onPageChange }: FacilityPaginationProps) => {
  if (!metadata) return null;

  return (
    <div className="px-6 py-4 border-t border-outline-variant bg-surface flex items-center justify-between">
      <div className="text-body-sm text-on-surface-variant">
        Showing page <span className="font-medium text-on-surface">{metadata.currentPage}</span> of{" "}
        <span className="font-medium text-on-surface">{metadata.totalPage}</span>
      </div>
      <div className="flex items-center gap-2">
        <button
          disabled={!metadata.hasPrevious}
          onClick={() => onPageChange(page - 1)}
          className="p-1.5 rounded-lg border border-outline-variant text-on-surface-variant disabled:opacity-50 disabled:cursor-not-allowed hover:bg-surface-container-low transition-colors"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>
        <button className="w-8 h-8 rounded-lg bg-primary text-on-primary text-label-md font-medium flex items-center justify-center">
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
