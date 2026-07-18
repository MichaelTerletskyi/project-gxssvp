import './App.css'

import { Routes, Route } from 'react-router';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { UserDashboard } from './pages/UserDashboard';
import { ModeratorDashboard } from './pages/ModeratorDashboard';
import { ProtectedRoute } from './components/ProtectedRoute';

function App() {

  return (
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<UserDashboard />} />
        </Route>

        <Route element={<ProtectedRoute allowedRoles={['MODERATOR']} />}>
          <Route path="/moderator/dashboard" element={<ModeratorDashboard />} />
        </Route>
      </Routes>
  )
}

export default App
