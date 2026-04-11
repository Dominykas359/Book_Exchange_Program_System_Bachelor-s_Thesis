import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ExchangeRequestResponseDto } from '../../../core/models/exchange-request.model';
import { UserResponseDto } from '../../../core/models/user.model';

@Component({
  selector: 'app-exchange-request-modal',
  standalone: true,
  imports: [],
  templateUrl: './exchange-request-modal.html',
  styleUrl: './exchange-request-modal.scss'
})
export class ExchangeRequestModalComponent {
  @Input({ required: true }) exchangeRequest!: ExchangeRequestResponseDto;
  @Input() isActionLoading = false;
  @Input() actionErrorMessage = '';

  @Output() close = new EventEmitter<void>();
  @Output() accept = new EventEmitter<ExchangeRequestResponseDto>();
  @Output() decline = new EventEmitter<ExchangeRequestResponseDto>();
  @Output() delete = new EventEmitter<ExchangeRequestResponseDto>();

  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.close.emit();
    }
  }

  onAccept(): void {
    this.accept.emit(this.exchangeRequest);
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
    return this.exchangeRequest.status === 'PENDING';
  }
}
