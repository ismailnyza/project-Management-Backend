const BASE = '/api/v1';

function headers(): Record<string, string> {
  const h: Record<string, string> = { 'Content-Type': 'application/json' };
  const token = localStorage.getItem('token');
  if (token) h['Authorization'] = `Bearer ${token}`;
  return h;
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const res = await fetch(BASE + path, {
    method,
    headers: headers(),
    body: body ? JSON.stringify(body) : undefined,
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }));
    throw new Error(err.error || err.message || `HTTP ${res.status}`);
  }
  if (res.status === 204) return undefined as T;
  return res.json();
}

export const api = {
  // Auth
  register: (name: string, email: string, password: string) =>
    request<{ user: any; token: string; refreshToken: string }>('POST', '/auth/register', { name, email, password }),
  login: (email: string, password: string) =>
    request<{ user: any; token: string; refreshToken: string }>('POST', '/auth/login', { email, password }),
  refresh: (refreshToken: string) =>
    request<{ user: any; token: string; refreshToken: string }>('POST', '/auth/refresh', { refreshToken }),
  logout: (refreshToken: string) =>
    request<void>('POST', '/auth/logout', { refreshToken }),

  // Projects
  getProjects: () => request<any[]>('GET', '/projects'),
  getProject: (id: number) => request<any>('GET', `/projects/${id}`),
  createProject: (name: string, key: string) => request<any>('POST', '/projects', { name, key }),

  // Issues
  getBoardIssues: (projectId: number, params?: Record<string, string>) => {
    const qs = params ? '?' + new URLSearchParams(params).toString() : '';
    return request<any>('GET', `/projects/${projectId}/issues${qs}`);
  },
  getIssue: (id: number) => request<any>('GET', `/issues/${id}`),
  createIssue: (projectId: number, data: any) => request<any>('POST', `/projects/${projectId}/issues`, data),
  updateIssue: (id: number, data: any) => request<any>('PATCH', `/issues/${id}`, data),
  transitionIssue: (id: number, transitionId: number) =>
    request<any>('POST', `/issues/${id}/transition`, { transitionId }),
  deleteIssue: (id: number) => request<void>('DELETE', `/issues/${id}`),

  // Comments
  addComment: (issueId: number, body: string) => request<any>('POST', `/issues/${issueId}/comments`, { body }),
  deleteComment: (id: number) => request<void>('DELETE', `/comments/${id}`),

  // Workflow
  getTransitions: (projectId: number) => request<any[]>('GET', `/projects/${projectId}/workflow/transitions`),
  addTransition: (projectId: number, data: any) =>
    request<any>('POST', `/projects/${projectId}/workflow/transitions`, data),
  deleteTransition: (projectId: number, id: number) => request<void>('DELETE', `/projects/${projectId}/workflow/transitions/${id}`),

  // Users
  getUsers: () => request<any[]>('GET', '/users'),
};
