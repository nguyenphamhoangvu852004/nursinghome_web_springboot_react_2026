import { ChevronRight } from "lucide-react";
import type { ReactNode } from "react";

export const badgeClassOptions = [
  "bg-[rgb(219,234,254)] text-[rgb(37,99,235)] border-[rgb(191,219,254)]",
  "bg-[rgb(254,243,199)] text-[rgb(217,119,6)] border-[rgb(252,216,133)]",
  "bg-[rgb(255,237,213)] text-[rgb(194,65,12)] border-[rgb(255,210,156)]",
  "bg-[rgb(254,226,226)] text-[rgb(220,38,38)] border-[rgb(254,202,202)]",
];

type BreadcrumbItem = {
  label: string;
  active?: boolean;
};

type InfoBanner = {
  icon?: ReactNode;
  text: string;
  className?: string;
};

type AdminPageShellNoteProps = {
  children: ReactNode;
  className?: string;
};

interface AdminPageShellProps {
  breadcrumbs: BreadcrumbItem[];
  title: string;
  subtitle: string;
  infoBanner?: InfoBanner;
  children: ReactNode;
  footer?: ReactNode;
}

export const AdminPageShellNote = ({ children, className }: AdminPageShellNoteProps) => (
  <div className={`px-6 py-4 italic border-t border-outline-variant/20 text-on-surface-variant text-[11px] bg-[rgb(249,249,250)] ${className ?? ""}`}>
    {children}
  </div>
);

export const AdminPageShell = ({
  breadcrumbs,
  title,
  subtitle,
  infoBanner,
  children,
  footer,
}: AdminPageShellProps) => {
  return (
    <div className="min-h-[calc(100vh-4rem)] bg-surface text-on-surface">
      <main className="flex-1 p-6 lg:p-2 pb-44 px-4">
        <div className="max-w-container-max mx-auto">
          <nav className="flex items-center gap-2 text-sm text-on-surface-variant mb-4">
            {breadcrumbs.map((item, index) => (
              <span key={`${item.label}-${index}`} className={item.active ? "text-primary font-semibold" : "text-on-surface-variant"}>
                {item.label}
                {index < breadcrumbs.length - 1 && (
                  <ChevronRight className="w-4 h-4 text-on-surface-variant inline-block align-text-bottom" />
                )}
              </span>
            ))}
          </nav>

          <div className="mb-8">
            <h1 className="font-display-lg text-display-lg text-on-surface leading-tight">
              {title}
            </h1>
            <p className="text-body-base font-body-base text-on-surface-variant mt-2">
              {subtitle}
            </p>
          </div>

          {infoBanner ? (
            <div className={`mb-8 p-4 rounded-xl flex gap-4 border border-primary/10 shadow-sm text-[#2563EB] bg-sky-100 items-center ${infoBanner.className ?? ""}`}>
              {infoBanner.icon}
              <p className="font-body-sm text-body-sm font-medium">{infoBanner.text}</p>
            </div>
          ) : null}

          {children}
        </div>
      </main>

      {footer ? <div className="h-[2rem] md:h-[7rem]" aria-hidden="true" /> : null}

      {footer ? (
        <div className="fixed bottom-0 left-0 right-0 backdrop-blur-xl border-t border-outline-variant/30 py-5 px-8 z-40 bg-white md:ml-64">
          {footer}
        </div>
      ) : null}
    </div>
  );
};
