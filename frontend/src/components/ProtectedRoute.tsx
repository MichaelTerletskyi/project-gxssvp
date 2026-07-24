import { Navigate, Outlet } from 'react-router';
import { useAuthStore } from '../store/authStore';
import { NotFoundPage } from '../pages/NotFound/NotFoundPage';

interface Props {
    allowedRoles?: Array<'USER' | 'MODERATOR'>;
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