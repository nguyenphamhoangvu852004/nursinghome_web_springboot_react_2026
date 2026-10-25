export interface BedSelectResponse {
  id: number;
  bedNumber: string;
}

export interface RoomSelectResponse {
  id: number;
  roomNumber: string;
  beds: BedSelectResponse[];
}

export interface FacilitySelectResponse {
  id: number;
  name: string;
  rooms: RoomSelectResponse[];
}
