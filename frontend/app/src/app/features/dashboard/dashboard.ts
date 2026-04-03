import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NoticeService } from '../../core/services/notice.service';
import { NoticeFilter, NoticeResponseDto, PageResponseDto } from '../../core/models/notice.model';
import { NoticeCardComponent } from '../../shared/components/notice-card/notice-card';
import { NoticeModalComponent } from '../../shared/components/notice-modal/notice-modal';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, NoticeCardComponent, NoticeModalComponent],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  private readonly noticeService = inject(NoticeService);

  readonly notices = signal<NoticeResponseDto[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal('');
  readonly selectedNotice = signal<NoticeResponseDto | null>(null);

  readonly currentPage = signal(0);
  readonly pageSize = signal(12);
  readonly totalPages = signal(0);
  readonly isFirst = signal(true);
  readonly isLast = signal(true);

  readonly filters = signal<NoticeFilter>({
    title: '',
    author: '',
    language: '',
    releaseYearFrom: '',
    releaseYearTo: '',
    minPageCount: undefined,
    maxPageCount: undefined,
    cover: '',
    colored: undefined
  });

  ngOnInit(): void {
    this.loadNotices();
  }

  loadNotices(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.noticeService.getNotices(
      this.currentPage(),
      this.pageSize(),
      'timePosted',
      'desc',
      this.filters()
    ).subscribe({
      next: (data: PageResponseDto<NoticeResponseDto>) => {
        this.notices.set(data.content);
        this.totalPages.set(data.totalPages);
        this.isFirst.set(data.first);
        this.isLast.set(data.last);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load notices:', error);
        this.errorMessage.set('Failed to load notices.');
        this.isLoading.set(false);
      }
    });
  }

  applyFilters(): void {
    this.currentPage.set(0);
    this.loadNotices();
  }

  clearFilters(): void {
    this.filters.set({
      title: '',
      author: '',
      language: '',
      releaseYearFrom: '',
      releaseYearTo: '',
      minPageCount: undefined,
      maxPageCount: undefined,
      cover: '',
      colored: undefined
    });
    this.currentPage.set(0);
    this.loadNotices();
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(value => value - 1);
      this.loadNotices();
    }
  }

  nextPage(): void {
    if (this.currentPage() + 1 < this.totalPages()) {
      this.currentPage.update(value => value + 1);
      this.loadNotices();
    }
  }

  updateTextFilter<K extends keyof NoticeFilter>(key: K, value: string): void {
    this.filters.update(current => ({
      ...current,
      [key]: value
    }));
  }

  updateNumberFilter<K extends keyof NoticeFilter>(key: K, value: string): void {
    this.filters.update(current => ({
      ...current,
      [key]: value === '' ? undefined : Number(value)
    }));
  }

  updateBooleanFilter(value: string): void {
    this.filters.update(current => ({
      ...current,
      colored: value === '' ? undefined : value === 'true'
    }));
  }

  openNotice(notice: NoticeResponseDto): void {
    this.selectedNotice.set(notice);
  }

  closeModal(): void {
    this.selectedNotice.set(null);
  }
}
