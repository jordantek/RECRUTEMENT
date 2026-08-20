import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/lib/auth';

export function AuthLayout() {
  const { isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className={`flex items-center p-0 m-0 justify-center font-sans w-screen h-screen bg-gray-100`}>

          <Outlet />

    </div>
  );
}