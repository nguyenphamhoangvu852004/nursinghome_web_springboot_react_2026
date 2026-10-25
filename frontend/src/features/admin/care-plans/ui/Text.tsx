import { cn } from "@/lib/utils";
import type { ReactNode } from "react";

type TextProps = {
  className?: string;
  children: ReactNode;
};

export default function Text({ className, children }: TextProps) {
  return (
    <div className={cn(`text-[16px] text-gray-600`, className)}>{children}</div>
  );
}
