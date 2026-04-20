import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NoticeResponseDto } from '../../../core/models/notice.model';
import { UserResponseDto } from '../../../core/models/user.model';

@Component({
  selector: 'app-notice-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notice-modal.html',
  styleUrl: './notice-modal.scss'
})
export class NoticeModalComponent {
  @Input({ required: true }) notice!: NoticeResponseDto;
  @Output() close = new EventEmitter<void>();

  closeModal(): void {
    this.close.emit();
  }

  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.closeModal();
    }
  }

  getPosterFullName(): string {
    const firstName = this.notice.poster?.firstName ?? '';
    const lastName = this.notice.poster?.lastName ?? '';
    const fullName = `${firstName} ${lastName}`.trim();

    return fullName || this.notice.poster?.email || 'Unknown user';
  }

  getStatusLabel(): string {
    return this.notice.status === 'CLOSED' ? 'Closed' : 'Open';
  }

  isClosed(): boolean {
    return this.notice.status === 'CLOSED';
  }

  isOwnNotice(): boolean {
    const currentUser = this.getCurrentUser();

    if (!currentUser) {
      return false;
    }

    return currentUser.id === this.notice.poster?.id;
  }

  canCreateExchangeRequest(): boolean {
    return !this.isClosed() && !this.isOwnNotice();
  }

  getCreateRequestDisabledReason(): string {
    if (this.isClosed()) {
      return 'This notice is closed. Exchange requests cannot be created.';
    }

    if (this.isOwnNotice()) {
      return 'You cannot create an exchange request for your own notice.';
    }

    return '';
  }

  openCreateExchangeRequestPage(): void {
    if (!this.canCreateExchangeRequest()) {
      return;
    }

    window.open(`/exchange-requests/create?noticeId=${this.notice.id}`, '_blank');
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
