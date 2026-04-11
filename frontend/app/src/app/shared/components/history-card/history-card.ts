import { Component, EventEmitter, Input, Output } from '@angular/core';
import { HistoryResponseDto } from '../../../core/models/history.model';
import { UserResponseDto } from '../../../core/models/user.model';

@Component({
  selector: 'app-history-card',
  standalone: true,
  imports: [],
  templateUrl: './history-card.html',
  styleUrl: './history-card.scss'
})
export class HistoryCardComponent {
  @Input({ required: true }) history!: HistoryResponseDto;
  @Output() cardClick = new EventEmitter<HistoryResponseDto>();

  onClick(): void {
    this.cardClick.emit(this.history);
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
    switch (this.history.status) {
      case 'ACCEPTED':
        return 'Accepted';
      case 'DECLINED':
        return 'Declined';
      default:
        return 'Pending';
    }
  }

  getStatusClass(): string {
    switch (this.history.status) {
      case 'ACCEPTED':
        return 'accepted';
      case 'DECLINED':
        return 'declined';
      default:
        return 'pending';
    }
  }
}
