import { useAuthStore } from '../../../store/authStore.ts';
import { useLogout } from '../../../hooks/useAuth.ts';

export function ExplorePage() {
    const user = useAuthStore((s) => s.user);
    const logoutMutation = useLogout();

    return (
        <div>
            <h1>Explore page, {user?.username}</h1>
            <p>Role: {user?.role}</p>
            <button onClick={() => logoutMutation.mutate()} disabled={logoutMutation.isPending}>
                {logoutMutation.isPending ? 'Logout...' : 'Logout'}
            </button>
        </div>
    );
}