import {User, UserAuthData} from '@/types/UserModelTypes'
import { create } from 'zustand';
import { persist } from 'zustand/middleware';



interface AuthState {
  user: UserAuthData | null;
  oldeUser:User | null;
  isAuthenticated: boolean;
  login: (user: UserAuthData) => Promise<void>;
  logout: () => void;
  logoutAll: () => void;
}

export const useAuth = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
        oldeUser:null,
      login: async (user: UserAuthData) => {
        try {
          set({
            user: user,
            oldeUser:user.user,
            isAuthenticated: true,
          });
        } catch (error) {
          console.error('Login error:', error);
        }
      },
      logout: () => {
        set({ user: null, isAuthenticated: false });
      },
        logoutAll: () => {
            set({ user: null, isAuthenticated: false,oldeUser:null });
        },
    }),
    {
      name: 'auth-storage',
    }
  )
);
