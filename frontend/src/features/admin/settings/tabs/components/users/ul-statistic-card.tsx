import type { ReactNode } from "react";

interface StatisticCardProps {
  label: string;
  value: number | string;
  icon: ReactNode;
  iconBgClassName?: string;
}

export function StatisticCard({
  label,
  value,
  icon,
  iconBgClassName,
}: StatisticCardProps) {
  return (
    <div className="rounded-xl border bg-white p-5">
      <div className="flex items-center gap-3">

        <div
          className={`flex h-12 w-12 items-center justify-center rounded-full ${iconBgClassName}`}
        >
          {icon}
        </div>

        <div>
          <p className="text-sm text-gray-500">
            {label}
          </p>

          <p className="text-3xl font-bold">
            {value}
          </p>
        </div>

      </div>
    </div>
  );
}