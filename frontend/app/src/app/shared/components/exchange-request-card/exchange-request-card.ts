import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ExchangeRequestResponseDto } from '../../../core/models/exchange-request.model';
import { UserResponseDto } from '../../../core/models/user.model';

@Component({
  selector: 'app-exchange-request-card',
  standalone: true,
  imports: [],
  templateUrl: './exchange-request-card.html',
  styleUrl: './exchange-request-card.scss'
})
export class ExchangeRequestCardComponent {
  @Input({ required: true }) exchangeRequest!: ExchangeRequestResponseDto;
  @Output() cardClick = new EventEmitter<ExchangeRequestResponseDto>();

  onClick(): void {
    this.cardClick.emit(this.exchangeRequest);
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
}
