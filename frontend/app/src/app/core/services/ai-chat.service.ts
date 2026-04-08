import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AiChatMessage,
  AiChatMessageRequestDto
} from '../models/ai-chat.model';

@Injectable({
  providedIn: 'root'
})
export class AiChatService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/ai-chat';

  saveMessage(dto: AiChatMessageRequestDto): Observable<AiChatMessage> {
    return this.http.post<AiChatMessage>(`${this.baseUrl}/messages`, dto);
  }

  getHistory(userId: number): Observable<AiChatMessage[]> {
    return this.http.get<AiChatMessage[]>(`${this.baseUrl}/history/${userId}`);
  }

  clearHistory(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/history/${userId}`);
  }
}
