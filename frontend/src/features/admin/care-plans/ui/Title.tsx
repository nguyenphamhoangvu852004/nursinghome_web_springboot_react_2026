import { cn } from "@/lib/utils";
import type { ReactNode } from "react";

type TitleProps = {
  className?: string;
  children: ReactNode;
};

export default function Title({ className, children }: TitleProps) {
  return (
    <div className={cn(`text-[32px] font-bold`, className)}>{children}</div>
  );
}
