import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  NoticeFilter,
  NoticeRequestDto,
  NoticeResponseDto,
  PageResponseDto
} from '../models/notice.model';

@Injectable({
  providedIn: 'root'
})
export class NoticeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/notices';

  createNotice(dto: NoticeRequestDto): Observable<NoticeResponseDto> {
    return this.http.post<NoticeResponseDto>(this.baseUrl, dto);
  }

  getNotices(
    page = 0,
    size = 12,
    sortBy = 'timePosted',
    sortDir = 'desc',
    filters?: NoticeFilter
  ): Observable<PageResponseDto<NoticeResponseDto>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    if (filters?.posterId != null) {
      params = params.set('posterId', filters.posterId);
    }

    if (filters?.title) {
      params = params.set('title', filters.title);
    }

    if (filters?.author) {
      params = params.set('author', filters.author);
    }

    if (filters?.language) {
      params = params.set('language', filters.language);
    }

    if (filters?.releaseYearFrom) {
      params = params.set('releaseYearFrom', filters.releaseYearFrom);
    }

    if (filters?.releaseYearTo) {
      params = params.set('releaseYearTo', filters.releaseYearTo);
    }

    if (filters?.minPageCount != null) {
      params = params.set('minPageCount', filters.minPageCount);
    }

    if (filters?.maxPageCount != null) {
      params = params.set('maxPageCount', filters.maxPageCount);
    }

    if (filters?.cover) {
      params = params.set('cover', filters.cover);
    }

    if (filters?.colored != null) {
      params = params.set('colored', filters.colored);
    }

    if (filters?.postedFrom) {
      params = params.set('postedFrom', filters.postedFrom);
    }

    if (filters?.postedTo) {
      params = params.set('postedTo', filters.postedTo);
    }

    return this.http.get<PageResponseDto<NoticeResponseDto>>(this.baseUrl, { params });
  }

  getNoticeById(id: number): Observable<NoticeResponseDto> {
    return this.http.get<NoticeResponseDto>(`${this.baseUrl}/${id}`);
  }

  getNoticeByPublicationId(publicationId: number): Observable<NoticeResponseDto> {
    return this.http.get<NoticeResponseDto>(`${this.baseUrl}/publication/${publicationId}`);
  }

  getNoticesByPosterId(posterId: number): Observable<NoticeResponseDto[]> {
    return this.http.get<NoticeResponseDto[]>(`${this.baseUrl}/poster/${posterId}`);
  }
}
