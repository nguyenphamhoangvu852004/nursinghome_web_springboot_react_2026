import React from "react";

type Card = {
  label: string;
  value: number;
  icon: React.ReactNode;
  iconBg: string;
};

export function CardSummary({ cards }: { cards: Card[] }) {
  return (
    <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      {cards.map((card) => (
        <div key={card.label} className="rounded-[20px] border border-gray-200 bg-white p-5 shadow-sm">
          <div className="flex items-center gap-3">
            <div className={"flex h-10 w-z10 shrink-0 items-center justify-center rounded-full " + card.iconBg}>
              {card.icon}
            </div>
            <div>
              <p className="text-sm text-slate-500">{card.label}</p>
              <p className="mt-1 text-2xl font-bold text-slate-900">{card.value}</p>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
