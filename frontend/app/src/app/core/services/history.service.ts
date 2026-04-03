import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HistoryRequestDto, HistoryResponseDto } from '../models/history.model';

@Injectable({
  providedIn: 'root'
})
export class HistoryService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/history';

  createHistory(dto: HistoryRequestDto): Observable<HistoryResponseDto> {
    return this.http.post<HistoryResponseDto>(this.baseUrl, dto);
  }

  getHistoryById(id: number): Observable<HistoryResponseDto> {
    return this.http.get<HistoryResponseDto>(`${this.baseUrl}/${id}`);
  }

  getHistoryByUserId(userId: number): Observable<HistoryResponseDto[]> {
    return this.http.get<HistoryResponseDto[]>(`${this.baseUrl}/user/${userId}`);
  }

  getMyHistory(): Observable<HistoryResponseDto[]> {
    return this.http.get<HistoryResponseDto[]>(`${this.baseUrl}/me`);
  }
}
