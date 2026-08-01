import type {
  ApiResponse,
  AuthResponse,
  LoginRequest,
  RefreshTokenRequest,
  RefreshTokenResponse,
  RegisterRequest,
} from './auth.types.ts';
import { httpClient } from './httpClient';

export const authApi = {
  login: (data: LoginRequest) =>
    httpClient.post<ApiResponse<AuthResponse>>('/auth/login', data).then((r) => r.data),

  register: (data: RegisterRequest) =>
    httpClient.post<ApiResponse<AuthResponse>>('/auth/register', data).then((r) => r.data),

  refreshToken: (data: RefreshTokenRequest) =>
    httpClient.post<ApiResponse<RefreshTokenResponse>>('/auth/refresh', data).then((r) => r.data),

  logout: (data: RefreshTokenRequest) =>
    httpClient.post<ApiResponse<void>>('/auth/logout', data).then((r) => r.data),
};
