import React from 'react';
import { LucideIcon } from 'lucide-react';

interface StatCardProps {
    icon: LucideIcon;
    title: string;
    value: string | number;
    bgColor?: string;
    textColor?: string;
}

const StatCard: React.FC<StatCardProps> = ({
                                               icon: IconComponent,
                                               title,
                                               value,
                                               bgColor = "bg-primary/10",
                                               textColor = "text-primary"
                                           }) => {
    return (
        <div className="bg-card p-1 rounded-lg border flex items-center gap-4 px-2">
            <div className={`p-3 rounded-full ${bgColor} ${textColor}`}>
                <IconComponent className="h-4 w-4" />
            </div>
            <div>
                <p className="text-xs font-bold text-black">{title}</p>
                <h3 className="text-sm text-left font-bold">{value}</h3>
            </div>
        </div>
    );
};

export default StatCard;