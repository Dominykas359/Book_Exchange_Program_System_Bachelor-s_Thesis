import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NoticeRequestDto, NoticeResponseDto } from '../models/notice.model';

@Injectable({
  providedIn: 'root'
})
export class NoticeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/notices';

  createNotice(dto: NoticeRequestDto): Observable<NoticeResponseDto> {
    return this.http.post<NoticeResponseDto>(this.baseUrl, dto);
  }

  getAllNotices(): Observable<NoticeResponseDto[]> {
    return this.http.get<NoticeResponseDto[]>(this.baseUrl);
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
