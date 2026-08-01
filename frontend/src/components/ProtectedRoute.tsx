import { Navigate, Outlet } from 'react-router';

import { NotFoundPage } from '../pages/NotFound/NotFoundPage';
import { useAuthStore } from '../store/authStore';

interface Props {
  allowedRoles?: ('USER' | 'MODERATOR')[];
}

export function ProtectedRoute({ allowedRoles }: Props) {
  const { isAuthenticated, user } = useAuthStore();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && user && !allowedRoles.includes(user.role)) {
    return <NotFoundPage />;
  }

  return <Outlet />;
}
