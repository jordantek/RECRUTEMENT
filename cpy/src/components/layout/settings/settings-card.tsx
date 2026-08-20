
import { forwardRef } from "react"; // ✅ Ajouté ici
import { LucideIcon } from "lucide-react";


type SettingsCardProps = {
    title: string;
    description: string;
    icon: LucideIcon;
    to: string;
};

const SettingsCard = forwardRef<HTMLAnchorElement, SettingsCardProps>(
    ({ title, icon: Icon }) => {
        return (
           /* <Link
                to={to}
                ref={ref} // Le ref est transmis ici
                className="group rounded-xl border border-neutral-200 bg-white hover:shadow-lg transition-all duration-300 p-6 flex flex-col items-center text-center gap-4 cursor-pointer"
            >*/
<button>
                <div className="p-3 rounded-lg bg-blue-100 group-hover:bg-blue-200 text-blue-600 transition-colors">
                    <Icon size={24} />
                </div>

                {/* Titre */}
                <h3 className="text-md font-semibold text-gray-900 group-hover:text-blue-700 transition-colors">
                    {title}
                </h3>

                {/* Description optionnelle */}{/*
                {description && (
                    <p className="text-sm text-muted-foreground mt-1">{description}</p>
                )}*/}
        </button>
           /* </Link>*/
        );
    }
);

SettingsCard.displayName = "SettingsCard";

export { SettingsCard };