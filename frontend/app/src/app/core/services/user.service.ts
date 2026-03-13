import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ChangePasswordRequestDto,
  UserResponseDto,
  UserUpdateRequestDto
} from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/users';

  getUserById(id: number): Observable<UserResponseDto> {
    return this.http.get<UserResponseDto>(`${this.baseUrl}/${id}`);
  }

  getUserByEmail(email: string): Observable<UserResponseDto> {
    const params = new HttpParams().set('email', email);
    return this.http.get<UserResponseDto>(`${this.baseUrl}/email`, { params });
  }

  updateUser(id: number, payload: UserUpdateRequestDto): Observable<UserResponseDto> {
    return this.http.put<UserResponseDto>(`${this.baseUrl}/${id}`, payload);
  }

  changePassword(id: number, payload: ChangePasswordRequestDto): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${id}/password`, payload);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
