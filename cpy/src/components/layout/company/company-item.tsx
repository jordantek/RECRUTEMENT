import { cn } from "@/lib/utils";
import { CompanyType } from "@/types/company/CompanyType.ts";
import {Building2} from "lucide-react";

interface CompanyItemProps {
    company: CompanyType;
    isActive?: boolean;
    onClick?: (company: CompanyType) => void;
}

export default function CompanyItem({
                                        company,
                                        isActive = false,
                                        onClick,
                                    }: CompanyItemProps) {
    //const avatarInfo = UserHelpers.getInitialUser(company.name ?? "", 1);

    return (
        <div
            className={cn(
                "relative w-full cursor-pointer overflow-hidden rounded-sm border p-1 my-2 transition-all border-none",
                "hover:bg-primary/20 dark:bg-muted dark:hover:bg-muted/80",
                isActive && "bg-primary/5 dark:bg-primary/10"
            )}
            onClick={onClick ? () => onClick(company) : undefined}
        >
            <div className="flex items-center gap-2">
                {/* Avatar */}
               {/* <Avatar
                    className={"w-10 h-10"}
                >
                    <AvatarFallback className={cn(
                        "h-10 w-10 shrink-0 border",
                        `bg-[${avatarInfo.bgColor}] text-[${avatarInfo.textColor}]`
                    )}>
                        {avatarInfo.initials}
                    </AvatarFallback>
                </Avatar>*/}

                {/* Content */}
                <div className="flex flex-col overflow-hidden w-full space-y-0.5">
                    {/* Nom de l’entreprise */}
                    <div className="flex items-center justify-start">
            <span className=" text-sm truncate text-gray-800 dark:text-white flex items-center">
                <Building2 size={15} className={"text-muted-foreground me-1"}/>
              {company.name ?? ""}
            </span>
                    </div>

                    {/* Adresse (en italique) */}
                    <div
                        className={cn(
                            "flex items-center justify-start",
                            isActive && "text-primary"
                        )}
                    >
                        <p className="truncate text-[10px] italic text-gray-600 dark:text-white/70">
                            {company.address ?? ""}
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}
