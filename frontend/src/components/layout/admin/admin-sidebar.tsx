import { NavLink } from "react-router";
import { HeartPulse, Menu } from "lucide-react";
import { cn } from "@/lib/utils";
import { adminNavGroups } from "@/common/admin-nav";
import { usePermissions } from "@/features/auth/hooks/use-current-user";
import { Sheet, SheetContent } from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { useSidebarStore } from "@/store/use-sidebar-store";

interface SidebarNavProps {
  visibleGroups: typeof adminNavGroups;
  isLoading: boolean;
  onNavigate?: () => void;
}

const SidebarNav = ({
  visibleGroups,
  isLoading,
  onNavigate,
}: SidebarNavProps) => (
  <>
    <div className="h-16 flex items-center gap-2 px-5 border-b border-sidebar-border">
      <HeartPulse className="w-6 h-6 text-primary" />
      <span className="font-semibold text-sidebar-foreground">
        ElderCare Admin
      </span>
    </div>

    <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-5">
      {isLoading && (
        <p className="px-3 text-sm text-muted-foreground">Loading...</p>
      )}

      {!isLoading &&
        visibleGroups.map((group) => (
          <div key={group.title}>
            <p className="px-3 mb-1 text-xs font-medium uppercase text-muted-foreground">
              {group.title}
            </p>
            <div className="space-y-0.5">
              {group.items.map((item) => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  end={item.path === "/admin"}
                  onClick={onNavigate}
                  className={({ isActive }) =>
                    cn(
                      "flex items-center gap-3 rounded-md px-3 py-2 text-sm text-sidebar-foreground hover:bg-sidebar-accent",
                      isActive &&
                        "bg-sidebar-accent text-sidebar-accent-foreground font-medium",
                    )
                  }
                >
                  <item.icon className="w-4 h-4" />
                  {item.label}
                </NavLink>
              ))}
            </div>
          </div>
        ))}
    </nav>
  </>
);

export const AdminSidebar = () => {
  const { canAccess, isLoading } = usePermissions();
  const { isOpen, close, toggle } = useSidebarStore();

  const visibleGroups = adminNavGroups
    .map((group) => ({
      ...group,
      items: group.items.filter((item) => canAccess(item.permission)),
    }))
    .filter((group) => group.items.length > 0);

  return (
    <>
      <Button
        variant="ghost"
        size="icon"
        className="md:hidden fixed top-3 left-3 z-40"
        onClick={toggle}
      >
        <Menu className="w-5 h-5" />
      </Button>

      <aside className="hidden md:flex w-64 shrink-0 border-r border-sidebar-border bg-sidebar flex-col h-screen sticky top-0">
        <SidebarNav visibleGroups={visibleGroups} isLoading={isLoading} />
      </aside>

      <Sheet open={isOpen} onOpenChange={(v) => !v && close()}>
        <SheetContent side="left" className="w-64 p-0 flex flex-col bg-sidebar">
          <SidebarNav
            visibleGroups={visibleGroups}
            isLoading={isLoading}
            onNavigate={close}
          />
        </SheetContent>
      </Sheet>
    </>
  );
};
