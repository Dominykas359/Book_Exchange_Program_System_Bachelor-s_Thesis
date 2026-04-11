import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExchangeRequestService } from '../../core/services/exchange-request.service';
import { ExchangeRequestResponseDto } from '../../core/models/exchange-request.model';
import { ExchangeRequestCardComponent } from '../../shared/components/exchange-request-card/exchange-request-card';
import { ExchangeRequestModalComponent } from '../../shared/components/exchange-request-modal/exchange-request-modal';

@Component({
  selector: 'app-exchange-requests-page',
  standalone: true,
  imports: [CommonModule, ExchangeRequestCardComponent, ExchangeRequestModalComponent],
  templateUrl: './exchange-requests-page.html',
  styleUrl: './exchange-requests-page.scss',
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
    const storedUser = localStorage.getItem('user');

    if (!storedUser) {
      this.errorMessage.set('User not found.');
      return;
    }

    const user = JSON.parse(storedUser);

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

  acceptExchangeRequest(exchangeRequest: ExchangeRequestResponseDto): void {
    this.isActionLoading.set(true);
    this.actionErrorMessage.set('');

    this.exchangeRequestService.acceptExchangeRequest(exchangeRequest.id).subscribe({
      next: () => {
        this.isActionLoading.set(false);
        this.closeModal();
        this.loadExchangeRequests();
      },
      error: (error) => {
        console.error('Failed to accept exchange request:', error);
        this.actionErrorMessage.set('Failed to accept exchange request.');
        this.isActionLoading.set(false);
      }
    });
  }

  declineExchangeRequest(exchangeRequest: ExchangeRequestResponseDto): void {
    this.isActionLoading.set(true);
    this.actionErrorMessage.set('');

    this.exchangeRequestService.declineExchangeRequest(exchangeRequest.id).subscribe({
      next: () => {
        this.isActionLoading.set(false);
        this.closeModal();
        this.loadExchangeRequests();
      },
      error: (error) => {
        console.error('Failed to decline exchange request:', error);
        this.actionErrorMessage.set('Failed to decline exchange request.');
        this.isActionLoading.set(false);
      }
    });
  }

  deleteExchangeRequest(exchangeRequest: ExchangeRequestResponseDto): void {
    this.isActionLoading.set(true);
    this.actionErrorMessage.set('');

    this.exchangeRequestService.deleteExchangeRequest(exchangeRequest.id).subscribe({
      next: () => {
        this.isActionLoading.set(false);
        this.closeModal();
        this.loadExchangeRequests();
      },
      error: (error) => {
        console.error('Failed to delete exchange request:', error);
        this.actionErrorMessage.set('Failed to delete exchange request.');
        this.isActionLoading.set(false);
      }
    });
  }
}
