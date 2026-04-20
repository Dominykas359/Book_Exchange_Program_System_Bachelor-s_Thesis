import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AcceptExchangeRequestDto,
  ExchangeRequestRequestDto,
  ExchangeRequestResponseDto,
  ExchangeRequestStatus
} from '../models/exchange-request.model';

@Injectable({
  providedIn: 'root'
})
export class ExchangeRequestService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/exchange-requests';

  createExchangeRequest(dto: ExchangeRequestRequestDto): Observable<ExchangeRequestResponseDto> {
    return this.http.post<ExchangeRequestResponseDto>(this.baseUrl, dto);
  }

  getExchangeRequestById(id: number): Observable<ExchangeRequestResponseDto> {
    return this.http.get<ExchangeRequestResponseDto>(`${this.baseUrl}/${id}`);
  }

  getExchangeRequestsByUserId(userId: number): Observable<ExchangeRequestResponseDto[]> {
    return this.http.get<ExchangeRequestResponseDto[]>(`${this.baseUrl}/user/${userId}`);
  }

  getExchangeRequestsByNoticeId(noticeId: number): Observable<ExchangeRequestResponseDto[]> {
    return this.http.get<ExchangeRequestResponseDto[]>(`${this.baseUrl}/notice/${noticeId}`);
  }

  getExchangeRequestsByStatus(status: ExchangeRequestStatus): Observable<ExchangeRequestResponseDto[]> {
    return this.http.get<ExchangeRequestResponseDto[]>(`${this.baseUrl}/status/${status}`);
  }

  acceptExchangeRequest(id: number, dto: AcceptExchangeRequestDto): Observable<ExchangeRequestResponseDto> {
    return this.http.patch<ExchangeRequestResponseDto>(`${this.baseUrl}/${id}/accept`, dto);
  }

  declineExchangeRequest(id: number): Observable<ExchangeRequestResponseDto> {
    return this.http.patch<ExchangeRequestResponseDto>(`${this.baseUrl}/${id}/decline`, {});
  }

  deleteExchangeRequest(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
