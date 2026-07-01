import { useState } from 'react';
import { api } from '../services/api';

export default function LoginPage({ onLogin }: { onLogin: (user: any, token: string, refreshToken: string) => void }) {
  const [isRegister, setIsRegister] = useState(false);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = isRegister
        ? await api.register(name, email, password)
        : await api.login(email, password);
      onLogin(res.user, res.token, res.refreshToken);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h2>{isRegister ? 'Create Account' : 'Sign In'}</h2>
        {isRegister && (
          <input type="text" placeholder="Full name" value={name}
            onChange={e => setName(e.target.value)} required />
        )}
        <input type="email" placeholder="Email" value={email}
          onChange={e => setEmail(e.target.value)} required />
        <input type="password" placeholder="Password" value={password}
          onChange={e => setPassword(e.target.value)} required minLength={6} />
        <button type="submit" disabled={loading}>
          {loading ? 'Loading...' : isRegister ? 'Register' : 'Login'}
        </button>
        {error && <div className="error">{error}</div>}
        <div className="switch">
          {isRegister ? 'Already have an account? ' : "Don't have an account? "}
          <a onClick={() => { setIsRegister(!isRegister); setError(''); }}>
            {isRegister ? 'Sign in' : 'Register'}
          </a>
        </div>
      </form>
    </div>
  );
}
