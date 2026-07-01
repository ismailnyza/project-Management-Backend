import { useState, useEffect } from 'react';
import { api } from '../services/api';

export default function ProjectsPage({ onOpenBoard }: { onOpenBoard: (id: number) => void }) {
  const [projects, setProjects] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [name, setName] = useState('');
  const [key, setKey] = useState('');

  useEffect(() => { api.getProjects().then(setProjects).finally(() => setLoading(false)); }, []);

  const create = async () => {
    if (!name || !key) return;
    await api.createProject(name, key.toUpperCase());
    const list = await api.getProjects();
    setProjects(list);
    setShowCreate(false); setName(''); setKey('');
  };

  if (loading) return <div className="loading">Loading projects...</div>;

  return (
    <div className="projects-page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Projects</h2>
        <button className="btn btn-primary" onClick={() => setShowCreate(!showCreate)}>
          + New Project
        </button>
      </div>

      {showCreate && (
        <div className="create-form" style={{ marginTop: 16 }}>
          <h3>Create Project</h3>
          <div className="create-row">
            <input placeholder="Project name" value={name} onChange={e => setName(e.target.value)} />
            <input placeholder="Key (e.g. PROJ)" value={key} onChange={e => setKey(e.target.value)} maxLength={10} />
            <button className="btn btn-primary" onClick={create}>Create</button>
          </div>
        </div>
      )}

      {projects.length === 0 && <div className="empty">No projects yet. Create one to get started.</div>}

      {projects.map(p => (
        <div key={p.id} className="project-card" onClick={() => onOpenBoard(p.id)}>
          <div className="name">{p.name}</div>
          <div className="key">{p.key}</div>
          {p.description && <div className="desc">{p.description}</div>}
        </div>
      ))}
    </div>
  );
}
