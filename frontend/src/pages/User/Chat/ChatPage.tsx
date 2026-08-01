import { useAuthStore } from '../../../store/authStore.ts';
import { useLogout } from '../../../hooks/useAuth.ts';

export function ChatPage() {
    const user = useAuthStore((s) => s.user);
    const logoutMutation = useLogout();

    return (
        <div>
            <h1>Chat page, {user?.username}</h1>
            <p>Role: {user?.role}</p>
            <button onClick={() => logoutMutation.mutate()} disabled={logoutMutation.isPending}>
                {logoutMutation.isPending ? 'Logout...' : 'Logout'}
            </button>
        </div>
    );
}