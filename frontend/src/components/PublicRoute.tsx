import { Navigate, Outlet } from 'react-router';

import { useAuthStore } from '../store/authStore';

export function PublicRoute() {
  const { isAuthenticated, user } = useAuthStore();

  if (isAuthenticated) {
    const redirectTo = user?.role === 'MODERATOR' ? '/moderator/dashboard' : '/user/dashboard';
    return <Navigate to={redirectTo} replace />;
  }

  return <Outlet />;
}
