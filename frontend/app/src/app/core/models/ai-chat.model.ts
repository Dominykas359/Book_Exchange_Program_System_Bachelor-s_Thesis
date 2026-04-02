import { NoticeResponseDto } from './notice.model';

export type AiChatRole = 'user' | 'assistant';

export interface AiChatMessage {
  id: string;
  role: AiChatRole;
  text: string;
  createdAt: string;
  notices?: NoticeResponseDto[];
}
