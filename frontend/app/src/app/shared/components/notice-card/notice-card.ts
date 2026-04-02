import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NoticeResponseDto } from '../../../core/models/notice.model';

@Component({
  selector: 'app-notice-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notice-card.html',
  styleUrl: './notice-card.scss'
})
export class NoticeCardComponent {
  @Input({ required: true }) notice!: NoticeResponseDto;
  @Output() cardClick = new EventEmitter<NoticeResponseDto>();

  onClick(): void {
    this.cardClick.emit(this.notice);
  }

  getReleaseYear(date: string | undefined): string {
    if (!date) {
      return '-';
    }

    return date.split('-')[0];
  }

  getPosterFullName(): string {
    const firstName = this.notice.poster?.firstName ?? '';
    const lastName = this.notice.poster?.lastName ?? '';
    const fullName = `${firstName} ${lastName}`.trim();

    return fullName || this.notice.poster?.email || 'Unknown user';
  }
}
