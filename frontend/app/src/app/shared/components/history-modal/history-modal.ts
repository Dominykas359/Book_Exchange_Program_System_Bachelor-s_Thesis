import { Component, EventEmitter, Input, Output } from '@angular/core';
import { HistoryResponseDto } from '../../../core/models/history.model';
import { UserResponseDto } from '../../../core/models/user.model';
import { PublicationResponseDto } from '../../../core/models/publication.model';

@Component({
  selector: 'app-history-modal',
  standalone: true,
  imports: [],
  templateUrl: './history-modal.html',
  styleUrl: './history-modal.scss'
})
export class HistoryModalComponent {
  @Input({ required: true }) history!: HistoryResponseDto;
  @Output() close = new EventEmitter<void>();

  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.close.emit();
    }
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

  isCurrentUserRequester(): boolean {
    const currentUser = this.getCurrentUser();

    if (!currentUser) {
      return true;
    }

    return this.history.user?.id === currentUser.id;
  }

  getCurrentUserDisplayName(): string {
    return this.isCurrentUserRequester()
      ? this.getUserDisplayName(this.history.user)
      : this.getUserDisplayName(this.history.posterUser);
  }

  getPartnerDisplayName(): string {
    return this.isCurrentUserRequester()
      ? this.getUserDisplayName(this.history.posterUser)
      : this.getUserDisplayName(this.history.user);
  }

  getBookYouGaveAway(): PublicationResponseDto {
    return this.isCurrentUserRequester()
      ? this.history.givenPublication
      : this.history.receivedPublication;
  }

  getBookYouReceived(): PublicationResponseDto {
    return this.isCurrentUserRequester()
      ? this.history.receivedPublication
      : this.history.givenPublication;
  }

  getYourAddress(): string | null {
    return this.isCurrentUserRequester()
      ? this.history.requesterAddress || null
      : this.history.requestedFromUserAddress || null;
  }

  getOtherUserAddress(): string | null {
    return this.isCurrentUserRequester()
      ? this.history.requestedFromUserAddress || null
      : this.history.requesterAddress || null;
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
