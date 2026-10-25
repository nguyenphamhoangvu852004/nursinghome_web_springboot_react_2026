export interface ApiResponse<T> {
  statusCode: number;
  message: string;
  data: T;
}

export interface PaginationMetadata {
  currentPage: number;
  totalPage: number;
  currentLimit: number;
  hasNext: boolean;
  hasPrevious: boolean;
  totalElements: number;
}

export interface PagedApiResponse<T> extends ApiResponse<T> {
  metadata: PaginationMetadata;
}
