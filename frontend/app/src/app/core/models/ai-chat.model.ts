import { NoticeResponseDto } from './notice.model';

export type AiChatRole = 'user' | 'assistant';

export interface AiChatMessage {
  id: number;
  role: AiChatRole;
  text: string;
  createdAt: string;
  notices?: NoticeResponseDto[];
}

export interface AiChatMessageRequestDto {
  userId: number;
  role: AiChatRole;
  text: string;
  noticeIds: number[];
}
