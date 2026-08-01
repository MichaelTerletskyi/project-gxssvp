import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router';

import type { LoginRequest, RegisterRequest } from '../api/auth.types';
import { authApi } from '../api/authApi';
import { useAuthStore } from '../store/authStore';

export function useLogin() {
  const navigate = useNavigate();
  const login = useAuthStore((s) => s.login);

  return useMutation({
    mutationFn: (data: LoginRequest) => authApi.login(data),
    onSuccess: (response) => {
      const { accessToken, refreshToken, id, username, email, role } = response.data!;
      login(accessToken, refreshToken, { id, username, email, role });
      navigate(role === 'MODERATOR' ? '/moderator/dashboard' : '/user/dashboard');
    },
  });
}

export function useRegister() {
  const navigate = useNavigate();
  const login = useAuthStore((s) => s.login);

  return useMutation({
    mutationFn: (data: RegisterRequest) => authApi.register(data),
    onSuccess: (response) => {
      const { accessToken, refreshToken, id, username, email, role } = response.data!;
      login(accessToken, refreshToken, { id, username, email, role });
      navigate('/dashboard');
    },
  });
}

export function useLogout() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const logout = useAuthStore((s) => s.logout);
  const refreshToken = useAuthStore((s) => s.refreshToken);

  return useMutation({
    mutationFn: async (): Promise<void> => {
      if (!refreshToken) {
        return;
      }
      await authApi.logout({ refreshToken });
    },
    onSettled: () => {
      logout();
      queryClient.clear();
      navigate('/login');
    },
  });
}
