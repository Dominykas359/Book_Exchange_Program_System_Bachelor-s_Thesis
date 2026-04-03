import { PublicationResponseDto } from './publication.model';

export interface HistoryRequestDto {
  userId: number;
  posterUserId: number;
  noticeId: number;
  timeExchanged: string;
  givenPublicationId: number;
  receivedPublicationId: number;
}

export interface HistoryResponseDto {
  id: number;
  userId: number;
  posterUserId: number;
  noticeId: number;
  timeExchanged: string;
  givenPublication: PublicationResponseDto;
  receivedPublication: PublicationResponseDto;
}
