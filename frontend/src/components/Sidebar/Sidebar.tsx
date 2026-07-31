import { NavLink } from 'react-router';
import {
    Home,
    Search,
    Bell,
    UserPlus,
    MessageCircle,
    Bookmark,
    Rocket,
    User,
    Settings,
} from 'lucide-react';
import './Sidebar.css';

const primaryItems = [
    { to: '/', label: 'Home', icon: Home, hasDot: true },
    { to: '/explore', label: 'Explore', icon: Search },
    { to: '/notifications', label: 'Notifications', icon: Bell },
    { to: '/follow', label: 'Follow', icon: UserPlus },
    { to: '/chat', label: 'Chat', icon: MessageCircle },
    { to: '/bookmarks', label: 'Bookmarks', icon: Bookmark },
    { to: '/creator-studio', label: 'Creator Studio', icon: Rocket },
];

const secondaryItems = [
    { to: '/profile', label: 'Profile', icon: User },
    { to: '/settings', label: 'Settings', icon: Settings },
];

export function Sidebar() {
    return (
        <aside className="sidebar">
            <nav className="sidebar-nav">
                <div className="sidebar-group">
                    {primaryItems.map(({ to, label, icon: Icon, hasDot }) => (
                        <NavLink
                            key={to}
                            to={to}
                            className={({ isActive }) =>
                                `sidebar-item${isActive ? ' active' : ''}`
                            }
                        >
                            <Icon aria-hidden="true" />
                            {label}
                            {hasDot && <span className="sidebar-dot" aria-hidden="true" />}
                        </NavLink>
                    ))}
                </div>

                <div className="sidebar-group">
                    {secondaryItems.map(({ to, label, icon: Icon }) => (
                        <NavLink
                            key={to}
                            to={to}
                            className={({ isActive }) =>
                                `sidebar-item bold${isActive ? ' active' : ''}`
                            }
                        >
                            <Icon aria-hidden="true" />
                            {label}
                        </NavLink>
                    ))}
                </div>
            </nav>

            <button type="button" className="sidebar-post">
                Post
            </button>
        </aside>
    );
}