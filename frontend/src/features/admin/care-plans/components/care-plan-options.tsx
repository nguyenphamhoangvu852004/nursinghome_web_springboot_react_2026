import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { ChevronDown, Search } from "lucide-react";
import { useEffect, useState } from "react";
import Text from "../ui/Text";
const statusOptions = [
  "All",
  "Draft",
  "Pending review",
  "Active",
  "Review due",
  "Needs update",
  "Archived",
];

const reviewOptions = ["All", "Overdue", "Within 7 days", "Within 30 days"];

type CarePlanOptionsProps = {
  onSearch: (residentName?: string, status?: string) => void;
};
export default function CarePlanOptions(props: CarePlanOptionsProps) {
  const [status, setStatus] = useState("All");
  const [review, setReview] = useState("All");
  const [residentName, setResidentName] = useState("");

  return (
    <div className="flex flex-row items-center flex-wrap gap-3 mt-3">
      <div className="relative flex-1 min-w-[300px] ">
        <Search className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400" />

        <Input
          className="border-gray-400 bg-[#fafcfe] w-full pl-10 text-lg h-15"
          placeholder="Input Resident name,..."
          value={residentName}
          onChange={(e) => setResidentName(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              props.onSearch(
                residentName,
                status === "All"
                  ? undefined
                  : status.toUpperCase().replace(/\s+/g, "_"),
              );
            }
          }}
        />
      </div>

      <DropdownMenu>
        <DropdownMenuTrigger>
          <button className="border-2 border-gray-400 bg-[#fafcfe] rounded-lg flex items-center justify-between px-4 h-15 min-w-[180px]">
            <Text>Status: {status}</Text>
            <ChevronDown className="h-4 w-4" />
          </button>
        </DropdownMenuTrigger>

        <DropdownMenuContent className="w-[180px]">
          {statusOptions.map((option) => (
            <DropdownMenuItem
              key={option}
              onClick={() => {
                setStatus(option);

                props.onSearch(
                  residentName,
                  option === "All"
                    ? undefined
                    : option.toUpperCase().replace(/\s+/g, "_"),
                );
              }}
            >
              {option}
            </DropdownMenuItem>
          ))}
        </DropdownMenuContent>
      </DropdownMenu>

      <DropdownMenu>
        <DropdownMenuTrigger>
          <button className="border-2 border-gray-400 bg-[#fafcfe] rounded-lg flex items-center justify-between px-4 h-15 min-w-[180px]">
            <Text>Review: {review}</Text>
            <ChevronDown className="h-4 w-4" />
          </button>
        </DropdownMenuTrigger>

        <DropdownMenuContent className="w-[180px]">
          {reviewOptions.map((option) => (
            <DropdownMenuItem key={option} onClick={() => setReview(option)}>
              {option}
            </DropdownMenuItem>
          ))}
        </DropdownMenuContent>
      </DropdownMenu>
      <Button
        className={`border-2 border-solid border-gray-400  bg-[#fafcfe]  flex items-center justify-center rounded-[8px] text-black h-15 `}
      >
        Board
      </Button>

      <Button className={`md:h-[60px] md:w-[200px] `}>+ New Care Plan</Button>
    </div>
  );
}
