import { HeartIcon, type LucideIcon } from "lucide-react";

export type CardProps = {
  className?: string;
  icon?: LucideIcon;
  title?: string;
  amount?: string;
  width?: string;
  height?: string;
};

export default function Card({
  className,
  width,
  height,
  icon: Icon,
  title,
  amount,
}: CardProps) {
  return (
    <div
      className={`flex bg-gray-50 rounded-lg p-4 items-center ${width} ${height}`}
    >
      {Icon && (
        <div className={`mr-3 rounded-full h-12 w-12 p-2 ${className}`}>
          <Icon className="h-full w-full" />
        </div>
      )}

      <div className="flex flex-col">
        <div>{title}</div>
        <div className="text-[30px] font-bold">{amount}</div>
      </div>
    </div>
  );
}
