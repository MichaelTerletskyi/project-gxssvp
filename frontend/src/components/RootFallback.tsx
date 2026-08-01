import { Navigate } from 'react-router';

import { NotFoundPage } from '../pages/NotFound/NotFoundPage.tsx';
import { useAuthStore } from '../store/authStore.ts';

export function RootFallback() {
  const { isAuthenticated } = useAuthStore();
  return isAuthenticated ? <NotFoundPage /> : <Navigate to="/login" replace />;
}
