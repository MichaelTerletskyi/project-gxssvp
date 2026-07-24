import { useAuthStore } from '../../store/authStore.ts';
import { useLogout } from '../../hooks/useAuth.ts';

export function ModeratorDashboard() {
    const { user } = useAuthStore();
    const logoutMutation = useLogout();

    return (
        <div>
            <h1>Moderator room</h1>
            <p>Greetings, {user?.username} ({user?.role})</p>
            <button onClick={() => logoutMutation.mutate()} disabled={logoutMutation.isPending}>
                {logoutMutation.isPending ? 'Logout...' : 'Logout'}
            </button>
        </div>
    );
}