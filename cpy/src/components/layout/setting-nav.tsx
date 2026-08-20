import { Settings} from 'lucide-react';

import { Button } from '@/components/ui/button';
import { DropdownMenu, DropdownMenuContent, DropdownMenuGroup, DropdownMenuItem,DropdownMenuTrigger } from '@/components/ui/dropdown-menu';
import { useNavigate} from 'react-router-dom';
import {settingsHref} from "@/pages/app/setting-general/setting-general-page.tsx";

export function SettingNav() {

    const navigate = useNavigate();

    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button
                    size="icon"
                    variant="ghost"
                    className="relative shadow-none p-1 rounded-full border-none hover:border-none"
                    aria-label="Open notifications menu"
                >
                    <Settings className="h-5 w-5" />
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-56" align={"center"} forceMount>
                <DropdownMenuGroup>
                    {
                        settingsHref.map((item)=> (
                            <DropdownMenuItem onClick={() => navigate(item.to)}>
                                <item.icon size={24}/>
                                <span>  {item.title}</span>
                            </DropdownMenuItem>
                        ))
                    }
                </DropdownMenuGroup>
            </DropdownMenuContent>
        </DropdownMenu>
    );
}