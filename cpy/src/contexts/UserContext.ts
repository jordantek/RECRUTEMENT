
import {UserAuthData} from '@/types/UserModelTypes';
import { createContext, useContext } from 'react';

export const UserContext = createContext<UserAuthData | null>(null);

export function useUser() {
  return useContext(UserContext);
}