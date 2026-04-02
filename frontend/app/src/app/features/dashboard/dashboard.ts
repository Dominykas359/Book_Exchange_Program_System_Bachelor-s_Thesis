import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NoticeService } from '../../core/services/notice.service';
import { NoticeResponseDto } from '../../core/models/notice.model';
import { NoticeCardComponent } from '../../shared/components/notice-card/notice-card';
import { NoticeModalComponent } from '../../shared/components/notice-modal/notice-modal';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NoticeCardComponent, NoticeModalComponent],
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
}
