import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HistoryService } from '../../core/services/history.service';
import { HistoryResponseDto } from '../../core/models/history.model';
import { HistoryCardComponent } from '../../shared/components/history-card/history-card';
import { HistoryModalComponent } from '../../shared/components/history-modal/history-modal';
import { UserResponseDto } from '../../core/models/user.model';

@Component({
  selector: 'app-history-page',
  standalone: true,
  imports: [CommonModule, HistoryCardComponent, HistoryModalComponent],
  templateUrl: './history-page.html',
  styleUrl: './history-page.scss',
})
export class HistoryPage implements OnInit {
  private readonly historyService = inject(HistoryService);

  readonly historyItems = signal<HistoryResponseDto[]>([]);
  readonly selectedHistory = signal<HistoryResponseDto | null>(null);
  readonly isLoading = signal(false);
  readonly errorMessage = signal('');

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    const currentUser = this.getCurrentUser();

    if (!currentUser) {
      this.errorMessage.set('User not found. Please log in again.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    this.historyService.getHistoryByUserId(currentUser.id).subscribe({
      next: (data: HistoryResponseDto[]) => {
        this.historyItems.set(data);
        this.isLoading.set(false);
      },
      error: (error: any) => {
        console.error('Failed to load history:', error);
        this.errorMessage.set('Failed to load exchange history.');
        this.isLoading.set(false);
      }
    });
  }

  openHistory(history: HistoryResponseDto): void {
    this.selectedHistory.set(history);
  }

  closeModal(): void {
    this.selectedHistory.set(null);
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
