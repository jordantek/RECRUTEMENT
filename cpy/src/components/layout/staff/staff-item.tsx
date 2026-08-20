import { cn } from "@/lib/utils";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { StaffType } from "@/types/sttaf/StaffType.ts";
import { UserHelpers } from "@/helpers/UserHelpers.ts";
import { getRoleBadgeInfo, Role } from "@/helpers/RoleHelpers.ts";

interface StaffItemProps {
  staff: StaffType;
  isActive?: boolean | null;
  onClick?: (staff: StaffType) => void;
}

export default function StaffItem({ staff, isActive = false, onClick }: StaffItemProps) {
  const avatarInfo = UserHelpers.getInitialUser(staff.fullName ?? staff.username ?? "");
  const roleInfo = getRoleBadgeInfo(staff.roles[0] as Role);

  return (
    <div
      className={cn(
        "group relative flex items-center gap-3 w-full p-3 rounded-lg cursor-pointer transition-all duration-300 ease-out",
        "hover:shadow-lg hover:shadow-primary/5 hover:-translate-y-0.5",
        "border border-transparent backdrop-blur-sm",
        "before:absolute before:inset-0 before:rounded-xl before:bg-gradient-to-r before:from-transparent before:via-primary/5 before:to-transparent before:opacity-0 before:transition-opacity before:duration-300",
        "hover:before:opacity-100",
        isActive
          ? "bg-gray-100/80 dark:bg-gray-800/80 shadow-sm border-gray-200/50 dark:border-gray-700/50"
          : "bg-white/80 dark:bg-card/50 hover:bg-white/90 dark:hover:bg-card/70",
        "mb-0",
        "overflow-hidden"
      )}
      onClick={onClick ? () => onClick(staff) : undefined}
    >
      {/* Avatar */}
      <div className="shrink-0 relative z-10">
        <div
          className={cn(
            "relative rounded-full p-1 transition-all duration-300",
            isActive
              ? "bg-gradient-to-br from-primary via-primary/80 to-primary/60"
              : "bg-gradient-to-br from-gray-200 via-gray-100 to-gray-200 dark:from-gray-700 dark:via-gray-600 dark:to-gray-700 group-hover:from-primary/30 group-hover:via-primary/20 group-hover:to-primary/30"
          )}
        >
          <Avatar className="w-10 h-10 border-2 border-white dark:border-gray-800 shadow-inner">
            <AvatarFallback
              className={cn(
                "h-full w-full text-[12px] font-bold transition-all duration-300",
                "group-hover:scale-105 group-active:scale-95",
                "shadow-inner"
              )}
              style={{
                background: `linear-gradient(135deg, ${avatarInfo.bgColor} 0%, ${avatarInfo.bgGradient} 100%)`,
                color: "#FFFFFF",
              }}
            >
              {avatarInfo.initials}
            </AvatarFallback>
          </Avatar>
        </div>
      </div>

      {/* Text content */}
      <div className="flex flex-col flex-1 min-w-0 z-10 overflow-hidden">
        {/* Full name */}
        <div className="flex items-center justify-between w-full mb-1">
          <h3
            className={cn(
              "font-semibold text-[13px] truncate tracking-tight leading-tight",
              "bg-gradient-to-r bg-clip-text transition-all duration-300",
              isActive
                ? "from-primary to-primary/80 text-transparent"
                : "from-foreground to-foreground/90 text-transparent group-hover:from-primary group-hover:to-primary/80"
            )}
            title={staff.fullName ?? staff.username ?? ""}
          >
            {staff.fullName ?? staff.username ?? ""}
          </h3>
        </div>

        {/* Role badge */}
        {roleInfo && (
          <div className="flex items-center gap-2">
            <span
              className={cn(
                "text-[9px] font-medium rounded px-1 w-fit",
                roleInfo.color
              )}
            >
              {roleInfo.role}
            </span>
          </div>
        )}
      </div>

      {/* Right indicator bar */}
      <div
        className={cn(
          "absolute right-1.5 top-1/2 -translate-y-1/2 w-0.5 h-5 rounded-full transition-all duration-300 opacity-0",
          "group-hover:opacity-100",
          isActive ? "bg-primary" : "bg-primary/50"
        )}
      />

      {/* Hover gradient effect */}
      <div className="absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/10 to-transparent group-hover:translate-x-full transition-transform duration-1000 ease-out rounded-xl" />
    </div>
);
}
