export interface PublicationRequestDto {
  title: string;
  author: string;
  releaseYear: string;
  language: string;
  description: string;
  pageCount: number;
  cover?: string;
  colored?: boolean;
  editionNumber?: number;
}

export interface PublicationResponseDto {
  id: number;
  title: string;
  author: string;
  releaseYear: string;
  language: string;
  description: string;
  pageCount: number;
  cover?: string;
  colored?: boolean;
  editionNumber?: number;
}

export interface PublicationSearchResultDto {
  publication: PublicationResponseDto;
  score: number;
}
