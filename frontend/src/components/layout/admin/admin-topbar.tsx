import { NotificationMenu } from "@/components/common/notification-menu";
import { UserAvatarMenu } from "@/components/common/user-avatar-menu";

export const AdminTopbar = () => {
  return (
    <header className="h-16 border-b border-border bg-background flex items-center justify-between px-6 sticky top-0 z-10">
      <div className="relative"></div>

      <div className="flex items-center gap-4">
        <NotificationMenu viewAllPath="/admin/notifications" />
        <UserAvatarMenu
          name="Admin"
          subtitle="Administrator"
          profilePath="/admin/profile"
        />
      </div>
    </header>
  );
};
