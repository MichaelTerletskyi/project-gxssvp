import { useAuthStore } from '../store/authStore';
import { useLogout } from '../hooks/useAuth';

export function UserDashboard() {
    const user = useAuthStore((s) => s.user);
    const logoutMutation = useLogout();

    return (
        <div>
            <h1>Greetings, {user?.username}</h1>
            <p>Role: {user?.role}</p>
            <button onClick={() => logoutMutation.mutate()} disabled={logoutMutation.isPending}>
                {logoutMutation.isPending ? 'Logout...' : 'Logout'}
            </button>
        </div>
    );
}