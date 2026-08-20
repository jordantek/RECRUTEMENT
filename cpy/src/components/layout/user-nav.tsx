import { LogOut, Settings, User } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { DropdownMenu, DropdownMenuContent, DropdownMenuGroup, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from '@/components/ui/dropdown-menu';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { useAuth } from '@/lib/auth';
import { useNavigate } from 'react-router-dom';
import {UserHelpers} from "@/helpers/UserHelpers.ts";

export function UserNav({onLogout}: {onLogout: () => void}) {
  const { user } = useAuth();
  const navigate = useNavigate();
  
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="relative h-8 w-8 rounded-full border-none">
          <Avatar className="h-8 w-8 border-none">
            <AvatarImage src={user?.avatar} alt={user?.fullname} />
            <AvatarFallback>{UserHelpers.getInitialUser(user?.user.fullName??"").initials}</AvatarFallback>
          </Avatar>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-56" align="end" forceMount>
        <DropdownMenuLabel className="font-normal">
          <div className="flex flex-col space-y-1">
            <p className="text-sm font-medium leading-none">{user?.user.fullName}</p>
            <p className="text-xs leading-none text-muted-foreground">
              {user?.user.email}
            </p>
          </div>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        <DropdownMenuGroup>
          <DropdownMenuItem onClick={() =>  navigate("#")}>
            <Settings className="mr-2 h-4 w-4" />
            <span>Paramètres du système</span>
          </DropdownMenuItem>
          <DropdownMenuItem onClick={() => navigate("#")}>
            <User className="mr-2 h-4 w-4" />
            <span>Profile</span>
          </DropdownMenuItem>
        </DropdownMenuGroup>
        <DropdownMenuSeparator />
        <DropdownMenuItem className='text-red-500' onClick={() => onLogout()}>
          <LogOut className="mr-2 h-4 w-4" />
          <span>Déconnexion</span>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}