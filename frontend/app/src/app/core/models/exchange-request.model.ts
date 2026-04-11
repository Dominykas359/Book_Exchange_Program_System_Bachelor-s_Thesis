import { NoticeResponseDto } from './notice.model';
import { PublicationResponseDto } from './publication.model';
import { UserResponseDto } from './user.model';

export type ExchangeRequestStatus = 'PENDING' | 'DECLINED' | 'ACCEPTED';

export interface ExchangeRequestRequestDto {
  requestedTime: string;
  userId: number;
  requestedFromUserId: number;
  noticeId: number;
  givenPublicationId: number;
  receivedPublicationId: number;
}

export interface ExchangeRequestResponseDto {
  id: number;
  requestedTime: string;
  user: UserResponseDto;
  requestedFromUser: UserResponseDto;
  notice: NoticeResponseDto;
  givenPublication: PublicationResponseDto;
  receivedPublication: PublicationResponseDto;
  status: ExchangeRequestStatus;
}
