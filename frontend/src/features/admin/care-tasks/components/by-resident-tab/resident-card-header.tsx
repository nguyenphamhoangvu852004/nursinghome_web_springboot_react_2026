import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";

interface ResidentCardHeaderProps {
  resident: {
    id: string;
    name: string;
    age: number;
    room: string;
    imageUrl: string;
    careLevel: string;
    statusDot: "active" | "fall-risk" | "alert";
    totalTasks: number;
    completedTasks: number;
    missedTasks?: number;
  };
}

export const ResidentCardHeader = ({ resident }: ResidentCardHeaderProps) => {
  // Dot colours
  const dotColor =
    resident.statusDot === "alert"
      ? "bg-red-500"
      : resident.statusDot === "fall-risk"
      ? "bg-yellow-400"
      : "bg-emerald-500";

  return (
    <div className="flex items-center justify-between px-5 py-3.5 bg-muted/30 border-b border-border">
      {/* Left — Avatar + details */}
      <div className="flex items-center gap-4">
        <div className="relative">
          <Avatar size="lg" className="ring-2 ring-background shadow-sm flex-shrink-0 size-12">
            <AvatarImage src={resident.imageUrl} alt={resident.name} />
            <AvatarFallback className="bg-primary/10 text-primary font-semibold">
              {resident.name
                .split(" ")
                .map((n: string) => n[0])
                .join("")
                .slice(0, 2)}
            </AvatarFallback>
          </Avatar>
          {/* Status Dot */}
          <div
            className={`absolute bottom-0 right-0 size-3.5 rounded-full ring-2 ring-background ${dotColor}`}
            title={resident.careLevel}
          />
        </div>

        <div className="flex flex-col gap-1">
          <div className="flex items-center gap-2">
            <h2 className="text-[15px] font-semibold text-foreground leading-none">
              {resident.name}
            </h2>
            <Badge variant="secondary" className="px-1.5 py-0 h-[18px] text-[10px] font-medium bg-muted text-muted-foreground border-transparent">
              {resident.age} yrs
            </Badge>
          </div>

          <div className="flex items-center text-[11px] text-muted-foreground gap-2 font-medium">
            <span className="flex items-center gap-1">
              Room {resident.room}
            </span>
            <span className="size-1 rounded-full bg-border" />
            <span>{resident.careLevel}</span>
            <span className="size-1 rounded-full bg-border" />
            <span>Care Plan #{Math.floor(Math.random() * 50) + 10}</span>
          </div>
        </div>
      </div>
    </div>
  );
};
