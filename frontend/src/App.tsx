import './index.css';

import { Routes, Route } from 'react-router';
import { LoginPage } from './pages/Auth/login/LoginPage.tsx';
import { RegisterPage } from './pages/Auth/register/RegisterPage.tsx';
import { UserDashboard } from './pages/User/UserDashboard.tsx';
import { ModeratorDashboard } from './pages/Moderator/ModeratorDashboard.tsx';
import { NotFoundPage } from './pages/NotFound/NotFoundPage';
import { ProtectedRoute } from './components/ProtectedRoute';
import { PublicRoute } from './components/PublicRoute';
import { RootFallback } from './components/RootFallback';

function App() {
    return (
        <Routes>
            <Route element={<PublicRoute />}>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
            </Route>

            <Route path="/user/*" element={<ProtectedRoute allowedRoles={['USER']} />}>
                <Route path="dashboard" element={<UserDashboard />} />
                <Route path="*" element={<NotFoundPage />} />
            </Route>

            <Route path="/moderator/*" element={<ProtectedRoute allowedRoles={['MODERATOR']} />}>
                <Route path="dashboard" element={<ModeratorDashboard />} />
                <Route path="*" element={<NotFoundPage />} />
            </Route>

            <Route path="*" element={<RootFallback />} />
        </Routes>
    );
}

export default App;