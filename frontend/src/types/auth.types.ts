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
    id: number;
    username: string;
    email: string;
    role: string;
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
    id: number;
    username: string;
    email: string;
    role: string;
    enabled: boolean;
    createdAt: string;
}

export interface ApiResponse<T> {
    success: boolean;
    message?: string;
    data?: T;
    timestamp: string;
}