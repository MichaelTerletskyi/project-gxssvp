import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '../store/authStore';
import type {ApiResponse, RefreshTokenResponse} from './auth.types.ts';

export const httpClient = axios.create({
    baseURL: '/rest/api/v1',
});

const refreshClient = axios.create({
    baseURL: '/rest/api/v1',
});

httpClient.interceptors.request.use((config) => {
    const token = useAuthStore.getState().accessToken;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

let isRefreshing = false;
let queue: Array<(token: string) => void> = [];

httpClient.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

        if (error.response?.status !== 401 || originalRequest._retry) {
            return Promise.reject(error);
        }

        const currentRefreshToken = useAuthStore.getState().refreshToken;
        if (!currentRefreshToken) {
            useAuthStore.getState().logout();
            return Promise.reject(error);
        }

        if (isRefreshing) {
            return new Promise((resolve) => {
                queue.push((token) => {
                    originalRequest.headers.Authorization = `Bearer ${token}`;
                    resolve(httpClient(originalRequest));
                });
            });
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
            const { data } = await refreshClient.post<ApiResponse<RefreshTokenResponse>>(
                '/auth/refresh',
                { refreshToken: currentRefreshToken }
            );

            const { accessToken, refreshToken } = data.data!;
            useAuthStore.getState().setTokens(accessToken, refreshToken);

            queue.forEach((cb) => cb(accessToken));
            queue = [];

            originalRequest.headers.Authorization = `Bearer ${accessToken}`;
            return httpClient(originalRequest);
        } catch (refreshError) {
            queue = [];
            useAuthStore.getState().logout();
            window.location.href = '/login';
            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
);