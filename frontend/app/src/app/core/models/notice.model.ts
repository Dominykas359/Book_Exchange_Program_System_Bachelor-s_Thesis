import { PublicationResponseDto } from './publication.model';
import { UserResponseDto } from './user.model';

export interface NoticeRequestDto {
  timePosted: string;
  wishInReturn: string;
  posterId: number;
  publicationId: number;
}

export interface NoticeResponseDto {
  id: number;
  timePosted: string;
  wishInReturn: string;
  poster: UserResponseDto;
  publication: PublicationResponseDto;
}
