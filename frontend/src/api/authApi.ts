import { httpClient } from './httpClient';
import type {
    ApiResponse,
    AuthResponse,
    LoginRequest,
    RegisterRequest,
    RefreshTokenRequest,
    RefreshTokenResponse,
} from './auth.types.ts';

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