import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  PublicationRequestDto,
  PublicationResponseDto,
  PublicationSearchResultDto
} from '../models/publication.model';

@Injectable({
  providedIn: 'root'
})
export class PublicationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/publications';

  createPublication(dto: PublicationRequestDto): Observable<PublicationResponseDto> {
    return this.http.post<PublicationResponseDto>(this.baseUrl, dto);
  }

  search(
    q: string,
    modelKey: string = 'roberta',
    limit: number = 10,
    minScore: number = 0.45
  ): Observable<PublicationSearchResultDto[]> {
    const params = new HttpParams()
      .set('q', q)
      .set('modelKey', modelKey)
      .set('limit', limit)
      .set('minScore', minScore);

    return this.http.get<PublicationSearchResultDto[]>(
      `${this.baseUrl}/search`,
      { params }
    );
  }
}
