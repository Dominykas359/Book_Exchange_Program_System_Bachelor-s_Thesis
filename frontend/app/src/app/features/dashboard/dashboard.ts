import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NoticeService } from '../../core/services/notice.service';
import { NoticeResponseDto } from '../../core/models/notice.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  private readonly noticeService = inject(NoticeService);

  readonly notices = signal<NoticeResponseDto[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal('');
  readonly selectedNotice = signal<NoticeResponseDto | null>(null);

  ngOnInit(): void {
    this.loadNotices();
  }

  loadNotices(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.noticeService.getAllNotices().subscribe({
      next: (data) => {
        this.notices.set(data);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load notices:', error);
        this.errorMessage.set('Failed to load notices.');
        this.isLoading.set(false);
      }
    });
  }

  openNotice(notice: NoticeResponseDto): void {
    this.selectedNotice.set(notice);
  }

  closeModal(): void {
    this.selectedNotice.set(null);
  }

  getReleaseYear(date: string | undefined): string {
    if (!date) return '-';
    return date.split('-')[0];
  }

  getPosterFullName(notice: NoticeResponseDto): string {
    const firstName = notice.poster?.firstName ?? '';
    const lastName = notice.poster?.lastName ?? '';
    const fullName = `${firstName} ${lastName}`.trim();
    return fullName || notice.poster?.email || 'Unknown user';
  }

  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.closeModal();
    }
  }
}
