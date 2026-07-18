export type Role = 'USER' | 'MODERATOR';

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    type: string;
    id: string;
    username: string;
    email: string;
    role: Role;
}

export interface RefreshTokenRequest {
    refreshToken: string;
}

export interface RefreshTokenResponse {
    accessToken: string;
    refreshToken: string;
    type: string;
}

export interface UserResponse {
    id: string;
    username: string;
    email: string;
    role: Role;
    enabled: boolean;
    createdAt: string;
}

export interface ApiResponse<T> {
    success: boolean;
    message?: string;
    data?: T;
    timestamp: string;
}