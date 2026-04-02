import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  SettingsRequestDto,
  SettingsResponseDto
} from '../models/settings.model';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/users';

  getSettings(userId: number): Observable<SettingsResponseDto> {
    return this.http.get<SettingsResponseDto>(
      `${this.baseUrl}/${userId}/settings`
    );
  }

  createSettings(userId: number, dto: SettingsRequestDto): Observable<SettingsResponseDto> {
    return this.http.post<SettingsResponseDto>(
      `${this.baseUrl}/${userId}/settings`,
      dto
    );
  }

  updateSettings(userId: number, dto: SettingsRequestDto): Observable<SettingsResponseDto> {
    return this.http.put<SettingsResponseDto>(
      `${this.baseUrl}/${userId}/settings`,
      dto
    );
  }
}
