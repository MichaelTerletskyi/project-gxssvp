import './index.css';

import {Routes, Route} from 'react-router';
import {LoginPage} from './pages/Auth/login/LoginPage.tsx';
import {RegisterPage} from './pages/Auth/register/RegisterPage.tsx';
import {UserProfilePage} from './pages/User/Profile/UserProfilePage.tsx';
import {SettingsPage} from './pages/User/Settings/SettingsPage.tsx';
import {BookmarksPage} from './pages/User/Bookmarks/BookmarksPage.tsx';
import {ModeratorDashboard} from './pages/Moderator/ModeratorDashboard.tsx';
import {NotFoundPage} from './pages/NotFound/NotFoundPage';
import {ProtectedRoute} from './components/ProtectedRoute';
import {PublicRoute} from './components/PublicRoute';
import {RootFallback} from './components/RootFallback';
import {AppLayout} from './layouts/AppLayout';
import {ChatPage} from "./pages/User/Chat/ChatPage.tsx";
import {FollowPage} from "./pages/User/Follow/FollowPage.tsx";
import {NotificationsPage} from "./pages/User/Notifications/NotificationsPage.tsx";
import {ExplorePage} from "./pages/User/Explore/ExplorePage.tsx";
import {HomePage} from "./pages/User/Home/HomePage.tsx";

function App() {
    return (
        <Routes>
            <Route element={<PublicRoute/>}>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/register" element={<RegisterPage/>}/>
            </Route>

            <Route path="/user/*" element={<ProtectedRoute allowedRoles={['USER']}/>}>
                <Route element={<AppLayout/>}>
                    <Route path="profile" element={<UserProfilePage/>}/>
                    <Route path="settings" element={<SettingsPage/>}/>
                    <Route path="bookmarks" element={<BookmarksPage/>}/>
                    <Route path="chat" element={<ChatPage/>}/>
                    <Route path="follow" element={<FollowPage/>}/>
                    <Route path="notifications" element={<NotificationsPage/>}/>
                    <Route path="explore" element={<ExplorePage/>}/>
                    <Route path="home" element={<HomePage/>}/>
                    <Route path="*" element={<NotFoundPage/>}/>
                </Route>
            </Route>

            <Route path="/moderator/*" element={<ProtectedRoute allowedRoles={['MODERATOR']}/>}>
                <Route element={<AppLayout/>}>
                    <Route path="dashboard" element={<ModeratorDashboard/>}/>
                    <Route path="*" element={<NotFoundPage/>}/>
                </Route>
            </Route>

            <Route path="*" element={<RootFallback/>}/>
        </Routes>
    );
}

export default App;