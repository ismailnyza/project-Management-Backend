export interface User {
  id: number; name: string; email: string;
}
export interface AuthResponse {
  user: User; token: string; refreshToken: string;
}
export interface Project {
  id: number; name: string; key: string; description: string; ownerId: number;
}
export interface Transition {
  id: number; fromStatus: string; toStatus: string; name: string;
}
export interface ProjectDetail extends Project {
  workflow: Transition[];
}
export interface Issue {
  id: number; projectId: number; type: string; priority: string; status: string;
  title: string; description: string; assigneeId: number | null; assigneeName: string | null;
  reporterId: number; reporterName: string; parentId: number | null; sprintId: number | null;
  storyPoints: number | null; dueDate: string | null; version: number;
  createdAt: string; updatedAt: string;
}
export interface IssueDetail {
  issue: Issue; subtasks: Issue[]; comments: Comment[];
}
export interface Comment {
  id: number; issueId: number; userId: number; userName: string; body: string; createdAt: string;
}
export interface PageResponse<T> {
  content: T[]; page: number; size: number; totalElements: number; totalPages: number;
}
export interface Sprint {
  id: number; projectId: number; name: string; goal: string; startDate: string; endDate: string; active: boolean;
}
