import { NoticeResponseDto } from './notice.model';
import { PublicationResponseDto } from './publication.model';
import { UserResponseDto } from './user.model';

export type HistoryStatus = 'PENDING' | 'DECLINED' | 'ACCEPTED';

export interface HistoryRequestDto {
  userId: number;
  posterUserId: number;
  noticeId: number;
  timeExchanged: string;
  givenPublicationId: number;
  receivedPublicationId: number;
  status: HistoryStatus;
  requesterAddress?: string;
  requestedFromUserAddress?: string;
}

export interface HistoryResponseDto {
  id: number;
  timeExchanged: string;
  user: UserResponseDto;
  posterUser: UserResponseDto;
  notice: NoticeResponseDto;
  givenPublication: PublicationResponseDto;
  receivedPublication: PublicationResponseDto;
  status: HistoryStatus;
  requesterAddress?: string;
  requestedFromUserAddress?: string;
}
