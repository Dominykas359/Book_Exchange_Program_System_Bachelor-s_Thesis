import { PublicationResponseDto } from './publication.model';
import { UserResponseDto } from './user.model';

export type NoticeStatus = 'OPEN' | 'CLOSED';

export interface NoticeRequestDto {
  timePosted: string;
  wishInReturn: string;
  posterId: number;
  publicationId: number;
  status?: NoticeStatus;
}

export interface NoticeResponseDto {
  id: number;
  timePosted: string;
  wishInReturn: string;
  poster: UserResponseDto;
  publication: PublicationResponseDto;
  status: NoticeStatus;
}

export interface PageResponseDto<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface NoticeFilter {
  posterId?: number;
  title?: string;
  author?: string;
  language?: string;
  releaseYearFrom?: string;
  releaseYearTo?: string;
  minPageCount?: number;
  maxPageCount?: number;
  cover?: string;
  colored?: boolean;
  postedFrom?: string;
  postedTo?: string;
}
