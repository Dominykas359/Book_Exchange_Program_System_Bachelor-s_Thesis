import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  AcceptExchangeRequestDto,
  ExchangeRequestResponseDto
} from '../../../core/models/exchange-request.model';
import { UserResponseDto } from '../../../core/models/user.model';

@Component({
  selector: 'app-exchange-request-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './exchange-request-modal.html',
  styleUrl: './exchange-request-modal.scss'
})
export class ExchangeRequestModalComponent {
  @Input({ required: true }) exchangeRequest!: ExchangeRequestResponseDto;
  @Input() isActionLoading = false;
  @Input() actionErrorMessage = '';

  @Output() close = new EventEmitter<void>();
  @Output() accept = new EventEmitter<{
    exchangeRequest: ExchangeRequestResponseDto;
    dto: AcceptExchangeRequestDto;
  }>();
  @Output() decline = new EventEmitter<ExchangeRequestResponseDto>();
  @Output() delete = new EventEmitter<ExchangeRequestResponseDto>();

  readonly acceptForm: AcceptExchangeRequestDto = {
    requestedFromUserAddress: ''
  };

  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.close.emit();
    }
  }

  onAccept(): void {
    this.accept.emit({
      exchangeRequest: this.exchangeRequest,
      dto: {
        requestedFromUserAddress: this.acceptForm.requestedFromUserAddress.trim()
      }
    });
  }

  onDecline(): void {
    this.decline.emit(this.exchangeRequest);
  }

  onDelete(): void {
    this.delete.emit(this.exchangeRequest);
  }

  getUserDisplayName(user: UserResponseDto | null | undefined): string {
    if (!user) {
      return 'Unknown user';
    }

    const candidate = user as any;

    if (candidate.name) {
      return candidate.name;
    }

    if (candidate.username) {
      return candidate.username;
    }

    const fullName = [candidate.firstName, candidate.lastName]
      .filter((value: string | undefined) => !!value)
      .join(' ')
      .trim();

    if (fullName) {
      return fullName;
    }

    if (candidate.email) {
      return candidate.email;
    }

    return 'Unknown user';
  }

  getStatusLabel(): string {
    switch (this.exchangeRequest.status) {
      case 'ACCEPTED':
        return 'Accepted';
      case 'DECLINED':
        return 'Declined';
      default:
        return 'Pending';
    }
  }

  getStatusClass(): string {
    switch (this.exchangeRequest.status) {
      case 'ACCEPTED':
        return 'accepted';
      case 'DECLINED':
        return 'declined';
      default:
        return 'pending';
    }
  }

  canAcceptOrDecline(): boolean {
    return this.exchangeRequest.status === 'PENDING' && this.isCurrentUserRequestedFromUser();
  }

  isCurrentUserRequester(): boolean {
    const currentUser = this.getCurrentUser();

    if (!currentUser) {
      return false;
    }

    return this.exchangeRequest.user?.id === currentUser.id;
  }

  isCurrentUserRequestedFromUser(): boolean {
    const currentUser = this.getCurrentUser();

    if (!currentUser) {
      return false;
    }

    return this.exchangeRequest.requestedFromUser?.id === currentUser.id;
  }

  getPendingActionMessage(): string {
    if (this.exchangeRequest.status !== 'PENDING') {
      return '';
    }

    if (this.isCurrentUserRequester()) {
      return 'You created this exchange request, so you cannot accept or decline it.';
    }

    if (!this.isCurrentUserRequestedFromUser()) {
      return 'Only the user who received this exchange request can accept or decline it.';
    }

    return '';
  }

  private getCurrentUser(): UserResponseDto | null {
    const rawUser = localStorage.getItem('user');

    if (!rawUser) {
      return null;
    }

    try {
      return JSON.parse(rawUser) as UserResponseDto;
    } catch {
      return null;
    }
  }
}
