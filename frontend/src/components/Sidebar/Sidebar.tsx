import './Sidebar.css';

import {
  Bell,
  Bookmark,
  Home,
  MessageCircle,
  Search,
  Settings,
  User,
  UserPlus,
} from 'lucide-react';
import { NavLink } from 'react-router';

const primaryItems = [
  { to: '/user/home', label: 'Home', icon: Home, hasDot: true },
  { to: '/user/explore', label: 'Explore', icon: Search },
  { to: '/user/notifications', label: 'Notifications', icon: Bell },
  { to: '/user/follow', label: 'Follow', icon: UserPlus },
  { to: '/user/chat', label: 'Chat', icon: MessageCircle },
  { to: '/user/bookmarks', label: 'Bookmarks', icon: Bookmark },
];

const secondaryItems = [
  { to: '/user/profile', label: 'Profile', icon: User },
  { to: '/user/settings', label: 'Settings', icon: Settings },
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
              className={({ isActive }) => `sidebar-item${isActive ? ' active' : ''}`}
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
              className={({ isActive }) => `sidebar-item bold${isActive ? ' active' : ''}`}
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
