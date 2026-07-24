import {useAuthStore} from "../store/authStore.ts";
import {NotFoundPage} from "../pages/NotFound/NotFoundPage.tsx";
import {Navigate} from "react-router";

export function RootFallback() {
    const { isAuthenticated } = useAuthStore();
    return isAuthenticated ? <NotFoundPage /> : <Navigate to="/login" replace />;
}