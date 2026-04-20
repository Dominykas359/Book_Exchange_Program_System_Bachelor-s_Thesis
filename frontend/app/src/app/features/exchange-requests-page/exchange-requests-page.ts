import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';

import { ExchangeRequestService } from '../../core/services/exchange-request.service';
import {
  AcceptExchangeRequestDto,
  ExchangeRequestResponseDto
} from '../../core/models/exchange-request.model';
import { UserResponseDto } from '../../core/models/user.model';
import { ExchangeRequestCardComponent } from '../../shared/components/exchange-request-card/exchange-request-card';
import { ExchangeRequestModalComponent } from '../../shared/components/exchange-request-modal/exchange-request-modal';

@Component({
  selector: 'app-exchange-requests-page',
  standalone: true,
  imports: [CommonModule, ExchangeRequestCardComponent, ExchangeRequestModalComponent],
  templateUrl: './exchange-requests-page.html',
  styleUrl: './exchange-requests-page.scss'
})
export class ExchangeRequestsPage implements OnInit {
  private readonly exchangeRequestService = inject(ExchangeRequestService);

  readonly exchangeRequests = signal<ExchangeRequestResponseDto[]>([]);
  readonly selectedExchangeRequest = signal<ExchangeRequestResponseDto | null>(null);
  readonly isLoading = signal(false);
  readonly errorMessage = signal('');
  readonly actionErrorMessage = signal('');
  readonly isActionLoading = signal(false);

  ngOnInit(): void {
    this.loadExchangeRequests();
  }

  loadExchangeRequests(): void {
    const user = this.getCurrentUser();

    if (!user) {
      this.errorMessage.set('User not found.');
      this.isLoading.set(false);
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    this.exchangeRequestService.getExchangeRequestsByUserId(user.id).subscribe({
      next: (data: ExchangeRequestResponseDto[]) => {
        this.exchangeRequests.set(data);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load exchange requests:', error);
        this.errorMessage.set('Failed to load exchange requests.');
        this.isLoading.set(false);
      }
    });
  }

  openExchangeRequest(exchangeRequest: ExchangeRequestResponseDto): void {
    this.actionErrorMessage.set('');
    this.selectedExchangeRequest.set(exchangeRequest);
  }

  closeModal(): void {
    this.actionErrorMessage.set('');
    this.selectedExchangeRequest.set(null);
  }

  acceptExchangeRequest(event: {
    exchangeRequest: ExchangeRequestResponseDto;
    dto: AcceptExchangeRequestDto;
  }): void {
    this.isActionLoading.set(true);
    this.actionErrorMessage.set('');

    this.exchangeRequestService
      .acceptExchangeRequest(event.exchangeRequest.id, event.dto)
      .pipe(finalize(() => this.isActionLoading.set(false)))
      .subscribe({
        next: (updatedExchangeRequest) => {
          this.exchangeRequests.set(
            this.exchangeRequests().map((item) =>
              item.id === updatedExchangeRequest.id ? updatedExchangeRequest : item
            )
          );
          this.selectedExchangeRequest.set(updatedExchangeRequest);
        },
        error: (error) => {
          console.error('Failed to accept exchange request:', error);

          const backendMessage =
            error?.error?.message ||
            error?.error?.error ||
            error?.message ||
            'Failed to accept exchange request.';

          this.actionErrorMessage.set(backendMessage);
        }
      });
  }

  declineExchangeRequest(exchangeRequest: ExchangeRequestResponseDto): void {
    this.isActionLoading.set(true);
    this.actionErrorMessage.set('');

    this.exchangeRequestService
      .declineExchangeRequest(exchangeRequest.id)
      .pipe(finalize(() => this.isActionLoading.set(false)))
      .subscribe({
        next: (updatedExchangeRequest) => {
          this.exchangeRequests.set(
            this.exchangeRequests().map((item) =>
              item.id === updatedExchangeRequest.id ? updatedExchangeRequest : item
            )
          );
          this.selectedExchangeRequest.set(updatedExchangeRequest);
        },
        error: (error) => {
          console.error('Failed to decline exchange request:', error);

          const backendMessage =
            error?.error?.message ||
            error?.error?.error ||
            error?.message ||
            'Failed to decline exchange request.';

          this.actionErrorMessage.set(backendMessage);
        }
      });
  }

  deleteExchangeRequest(exchangeRequest: ExchangeRequestResponseDto): void {
    this.isActionLoading.set(true);
    this.actionErrorMessage.set('');

    this.exchangeRequestService
      .deleteExchangeRequest(exchangeRequest.id)
      .pipe(finalize(() => this.isActionLoading.set(false)))
      .subscribe({
        next: () => {
          this.exchangeRequests.set(
            this.exchangeRequests().filter((item) => item.id !== exchangeRequest.id)
          );
          this.closeModal();
        },
        error: (error) => {
          console.error('Failed to delete exchange request:', error);

          const backendMessage =
            error?.error?.message ||
            error?.error?.error ||
            error?.message ||
            'Failed to delete exchange request.';

          this.actionErrorMessage.set(backendMessage);
        }
      });
  }

  private getCurrentUser(): UserResponseDto | null {
    const storedUser = localStorage.getItem('user');

    if (!storedUser) {
      return null;
    }

    try {
      return JSON.parse(storedUser) as UserResponseDto;
    } catch {
      return null;
    }
  }
}
