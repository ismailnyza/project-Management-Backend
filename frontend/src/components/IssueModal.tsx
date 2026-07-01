import { useState, useEffect } from 'react';
import { api } from '../services/api';

export default function IssueModal({ issueId, projectId, users, onClose }: {
  issueId: number; projectId: number; users: any[]; onClose: () => void;
}) {
  const [detail, setDetail] = useState<any>(null);
  const [transitions, setTransitions] = useState<any[]>([]);
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([
      api.getIssue(issueId),
      api.getTransitions(projectId),
    ]).then(([d, t]) => {
      setDetail(d);
      setTransitions(t);
    }).catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, [issueId, projectId]);

  const handleTransition = async (transitionId: number) => {
    try {
      await api.transitionIssue(issueId, transitionId);
      const d = await api.getIssue(issueId);
      setDetail(d);
      setError('');
    } catch (e: any) { setError(e.message); }
  };

  const handleComment = async () => {
    if (!comment.trim()) return;
    try {
      await api.addComment(issueId, comment);
      setComment('');
      const d = await api.getIssue(issueId);
      setDetail(d);
    } catch (e: any) { setError(e.message); }
  };

  if (loading) return <div className="modal-overlay"><div className="modal"><div className="modal-body loading">Loading...</div></div></div>;
  if (!detail) return null;

  const issue = detail.issue;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <div>
            <span className={`type-badge`} style={{ marginRight: 8 }}>{issue.type}</span>
            <span className={`priority priority-${issue.priority}`}>{issue.priority}</span>
          </div>
          <div>
            <button onClick={onClose}>×</button>
          </div>
        </div>
        <div className="modal-body">
          <h3 style={{ fontSize: 18, marginBottom: 16 }}>{issue.title}</h3>

          {error && <div className="error" style={{ marginBottom: 12 }}>{error}</div>}

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginBottom: 20 }}>
            <div className="field"><label>Status</label><span>{issue.status}</span></div>
            <div className="field"><label>Assignee</label><span>{issue.assigneeName || 'Unassigned'}</span></div>
            <div className="field"><label>Reporter</label><span>{issue.reporterName}</span></div>
            <div className="field"><label>Story Points</label><span>{issue.storyPoints || '-'}</span></div>
          </div>

          {issue.description && (
            <div className="field"><label>Description</label>
              <div style={{ fontSize: 13, whiteSpace: 'pre-wrap' }}>{issue.description}</div>
            </div>
          )}

          {/* Workflow transitions */}
          {transitions.filter((t: any) => t.fromStatus === issue.status).length > 0 && (
            <div>
              <label style={{ fontSize: 12, color: 'var(--muted)', textTransform: 'uppercase', marginBottom: 8, display: 'block' }}>
                Available Transitions
              </label>
              <div className="transitions">
                {transitions.filter((t: any) => t.fromStatus === issue.status).map((t: any) => (
                  <button key={t.id} className="transition-btn" onClick={() => handleTransition(t.id)}>
                    {t.name} → {t.toStatus.replace('_', ' ')}
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Subtasks */}
          {detail.subtasks && detail.subtasks.length > 0 && (
            <div style={{ marginTop: 20 }}>
              <label style={{ fontSize: 12, color: 'var(--muted)', textTransform: 'uppercase', marginBottom: 8, display: 'block' }}>
                Subtasks ({detail.subtasks.length})
              </label>
              {detail.subtasks.map((s: any) => (
                <div key={s.id} className="issue-card" style={{ cursor: 'default' }}>
                  <div className="title">{s.title}</div>
                  <div className="meta">
                    <span className={`priority priority-${s.priority}`}>{s.priority}</span>
                    <span>{s.status}</span>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Comments */}
          <div style={{ marginTop: 24 }}>
            <label style={{ fontSize: 12, color: 'var(--muted)', textTransform: 'uppercase', marginBottom: 8, display: 'block' }}>
              Comments ({detail.comments?.length || 0})
            </label>
            {detail.comments?.map((c: any) => (
              <div key={c.id} className="comment">
                <span className="user">{c.userName}</span>
                <span className="time">{new Date(c.createdAt).toLocaleString()}</span>
                <div className="body">{c.body}</div>
              </div>
            ))}
            <div style={{ marginTop: 12, display: 'flex', gap: 8 }}>
              <input
                placeholder="Add a comment..."
                value={comment}
                onChange={e => setComment(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleComment()}
                style={{
                  flex: 1, padding: '8px 10px', background: 'var(--bg)',
                  border: '1px solid var(--border)', borderRadius: 'var(--radius)',
                  color: 'var(--text)', fontSize: 13
                }}
              />
              <button className="btn btn-primary" onClick={handleComment}>Post</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
