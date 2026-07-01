import { useState, useEffect, useCallback } from 'react';
import { api } from '../services/api';
import IssueModal from '../components/IssueModal';

const STATUSES = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];

export default function BoardPage({ projectId, onBack }: { projectId: number; onBack: () => void }) {
  const [issues, setIssues] = useState<any[]>([]);
  const [project, setProject] = useState<any>(null);
  const [selectedIssue, setSelectedIssue] = useState<number | null>(null);
  const [showCreate, setShowCreate] = useState(false);
  const [users, setUsers] = useState<any[]>([]);

  const load = useCallback(async () => {
    const [proj, board, userList] = await Promise.all([
      api.getProject(projectId),
      api.getBoardIssues(projectId, { size: '100' }),
      api.getUsers(),
    ]);
    setProject(proj);
    setIssues(board.content || board.tasks || []);
    setUsers(userList);
  }, [projectId]);

  useEffect(() => { load(); }, [load]);

  const issuesByStatus = (status: string) =>
    issues.filter(i => i.status === status);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    const form = e.target as HTMLFormElement;
    const typeEl = form.elements.namedItem('_type') as HTMLSelectElement;
    const titleEl = form.elements.namedItem('title') as HTMLInputElement;
    const prioEl = form.elements.namedItem('priority') as HTMLSelectElement;
    const assigneeEl = form.elements.namedItem('assigneeId') as HTMLSelectElement;
    const data: any = { type: typeEl.value, title: titleEl.value, priority: prioEl.value };
    if (assigneeEl.value) data.assigneeId = parseInt(assigneeEl.value);
    await api.createIssue(projectId, data);
    setShowCreate(false);
    load();
  };

  if (!project) return <div className="loading">Loading board...</div>;

  return (
    <div>
      <div className="board-header">
        <div>
          <button className="btn" onClick={onBack} style={{ marginRight: 12 }}>← Back</button>
          <span style={{ color: 'var(--muted)', fontSize: 12 }}>{project.key}</span>
        </div>
        <h2>{project.name}</h2>
        <button className="btn btn-primary" onClick={() => setShowCreate(!showCreate)}>+ New Issue</button>
      </div>

      {showCreate && (
        <form className="create-form" onSubmit={handleCreate}>
          <h3>Create Issue</h3>
          <div className="create-row">
            <select name="_type" defaultValue="TASK">
              <option value="TASK">Task</option><option value="BUG">Bug</option>
              <option value="STORY">Story</option><option value="EPIC">Epic</option>
            </select>
            <input name="title" placeholder="Issue title" required />
            <select name="priority" defaultValue="MEDIUM">
              <option value="CRITICAL">Critical</option><option value="HIGH">High</option>
              <option value="MEDIUM">Medium</option><option value="LOW">Low</option>
            </select>
            <select name="assigneeId">
              <option value="">Unassigned</option>
              {users.map(u => <option key={u.id} value={u.id}>{u.name}</option>)}
            </select>
            <button className="btn btn-primary" type="submit">Create</button>
          </div>
        </form>
      )}

      <div className="board-columns">
        {STATUSES.map(status => {
          const cols = issuesByStatus(status);
          return (
            <div key={status} className="column">
              <div className="column-header">
                <span>{status.replace('_', ' ')}</span>
                <span className="count">{cols.length}</span>
              </div>
              <div className="column-body">
                {cols.map(issue => (
                  <div key={issue.id} className="issue-card" onClick={() => setSelectedIssue(issue.id)}>
                    <div className="title">{issue.title}</div>
                    <div className="meta">
                      <span className={`priority priority-${issue.priority}`}>{issue.priority}</span>
                      <span className="type-badge">{issue.type}</span>
                      {issue.assigneeName && <span>{issue.assigneeName}</span>}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          );
        })}
      </div>

      {selectedIssue && (
        <IssueModal
          issueId={selectedIssue}
          projectId={projectId}
          users={users}
          onClose={() => { setSelectedIssue(null); load(); }}
        />
      )}
    </div>
  );
}
