import { useState, useEffect, useCallback } from 'react';
import { api } from './services/api';
import LoginPage from './pages/LoginPage';
import ProjectsPage from './pages/ProjectsPage';
import BoardPage from './pages/BoardPage';

export default function App() {
  const [user, setUser] = useState<any>(null);
  const [page, setPage] = useState<'login' | 'projects' | 'board'>('login');
  const [projectId, setProjectId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');
    if (token && savedUser) {
      setUser(JSON.parse(savedUser));
      setPage('projects');
    }
    setLoading(false);
  }, []);

  const handleLogin = useCallback((userData: any, token: string, refreshToken: string) => {
    localStorage.setItem('token', token);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    setPage('projects');
  }, []);

  const handleLogout = useCallback(() => {
    const rt = localStorage.getItem('refreshToken');
    if (rt) api.logout(rt).catch(() => {});
    localStorage.clear();
    setUser(null);
    setPage('login');
    setProjectId(null);
  }, []);

  const openBoard = useCallback((id: number) => {
    setProjectId(id);
    setPage('board');
  }, []);

  if (loading) return <div className="app"><div className="loading">Loading...</div></div>;

  if (page === 'login') return <LoginPage onLogin={handleLogin} />;

  return (
    <div>
      <nav className="nav">
        <h1 onClick={() => setPage('projects')} style={{ cursor: 'pointer' }}>PM Board</h1>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <span className="user">{user?.name}</span>
          <button onClick={handleLogout}>Logout</button>
        </div>
      </nav>
      <div className="app">
        {page === 'projects' && <ProjectsPage onOpenBoard={openBoard} />}
        {page === 'board' && projectId && <BoardPage projectId={projectId} onBack={() => setPage('projects')} />}
      </div>
    </div>
  );
}
