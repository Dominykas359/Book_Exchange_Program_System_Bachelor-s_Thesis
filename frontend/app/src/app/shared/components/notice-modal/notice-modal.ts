import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NoticeResponseDto } from '../../../core/models/notice.model';

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
}
