import './AppLayout.css';

import { Outlet } from 'react-router';

import { Sidebar } from '../components/Sidebar/Sidebar';

export function AppLayout() {
  return (
    <div className="app-layout">
      <Sidebar />
      <main className="app-content">
        <Outlet />
      </main>
    </div>
  );
}
