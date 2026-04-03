import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HistoryService } from '../../core/services/history.service';
import { HistoryResponseDto } from '../../core/models/history.model';
import { HistoryCardComponent } from '../../shared/components/history-card/history-card';
import { HistoryModalComponent } from '../../shared/components/history-modal/history-modal';

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
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.historyService.getMyHistory().subscribe({
      next: (data: HistoryResponseDto[]) => {
        this.historyItems.set(data);
        this.isLoading.set(false);
      },
      error: (error) => {
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
}
