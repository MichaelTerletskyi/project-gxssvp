import { Navigate, Outlet } from 'react-router';
import { useAuthStore } from '../store/authStore';

interface Props {
    allowedRoles?: Array<'USER' | 'MODERATOR'>;
}

export function ProtectedRoute({ allowedRoles }: Props) {
    const { isAuthenticated, user } = useAuthStore();

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    if (allowedRoles && user && !allowedRoles.includes(user.role)) {
        return <Navigate to="/dashboard" replace />;
    }

    return <Outlet />;
}