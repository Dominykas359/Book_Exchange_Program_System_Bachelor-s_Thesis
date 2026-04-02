import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AiChatMessage } from '../../core/models/ai-chat.model';
import { NoticeResponseDto } from '../../core/models/notice.model';
import { PublicationSearchResultDto } from '../../core/models/publication.model';
import { NoticeService } from '../../core/services/notice.service';
import { PublicationService } from '../../core/services/publication.service';
import { NoticeCardComponent } from '../../shared/components/notice-card/notice-card';
import { NoticeModalComponent } from '../../shared/components/notice-modal/notice-modal';

@Component({
  selector: 'app-ai-agent-page',
  standalone: true,
  imports: [CommonModule, FormsModule, NoticeCardComponent, NoticeModalComponent],
  templateUrl: './ai-agent-page.html',
  styleUrl: './ai-agent-page.scss',
})
export class AiAgentPage {
  private readonly publicationService = inject(PublicationService);
  private readonly noticeService = inject(NoticeService);

  readonly messages = signal<AiChatMessage[]>([]);
  readonly query = signal('');
  readonly isLoading = signal(false);
  readonly errorMessage = signal('');
  readonly selectedNotice = signal<NoticeResponseDto | null>(null);

  readonly hasMessages = computed(() => this.messages().length > 0);

  updateQuery(value: string): void {
    this.query.set(value);
  }

  sendMessage(): void {
    const trimmedQuery = this.query().trim();

    if (!trimmedQuery || this.isLoading()) {
      return;
    }

    this.errorMessage.set('');

    const userMessage: AiChatMessage = {
      id: crypto.randomUUID(),
      role: 'user',
      text: trimmedQuery,
      createdAt: new Date().toISOString(),
    };

    this.messages.update((messages) => [...messages, userMessage]);
    this.query.set('');
    this.isLoading.set(true);

    this.publicationService.search(trimmedQuery).subscribe({
      next: (results) => {
        this.loadNoticesFromResults(trimmedQuery, results);
      },
      error: (error) => {
        console.error('Semantic search failed:', error);
        this.errorMessage.set('Failed to retrieve matching books.');

        const assistantMessage: AiChatMessage = {
          id: crypto.randomUUID(),
          role: 'assistant',
          text: 'Sorry, I could not retrieve matching books right now.',
          createdAt: new Date().toISOString(),
          notices: [],
        };

        this.messages.update((messages) => [...messages, assistantMessage]);
        this.isLoading.set(false);
      },
    });
  }

  clearChat(): void {
    this.messages.set([]);
    this.errorMessage.set('');
    this.query.set('');
    this.selectedNotice.set(null);
  }

  openNotice(notice: NoticeResponseDto): void {
    this.selectedNotice.set(notice);
  }

  closeModal(): void {
    this.selectedNotice.set(null);
  }

  trackByMessageId(_: number, message: AiChatMessage): string {
    return message.id;
  }

  trackByNoticeId(_: number, notice: NoticeResponseDto): number {
    return notice.id;
  }

  private loadNoticesFromResults(query: string, results: PublicationSearchResultDto[]): void {
    if (results.length === 0) {
      const assistantMessage: AiChatMessage = {
        id: crypto.randomUUID(),
        role: 'assistant',
        text: `I could not find books matching "${query}".`,
        createdAt: new Date().toISOString(),
        notices: [],
      };

      this.messages.update((messages) => [...messages, assistantMessage]);
      this.isLoading.set(false);
      return;
    }

    const noticeRequests = results.map((result) =>
      this.noticeService.getNoticeByPublicationId(result.publication.id).pipe(
        catchError((error) => {
          console.error(`Failed to load notice for publication ${result.publication.id}:`, error);
          return of(null);
        })
      )
    );

    forkJoin(noticeRequests)
      .pipe(
        map((notices) => notices.filter((notice): notice is NoticeResponseDto => notice !== null))
      )
      .subscribe({
        next: (notices) => {
          const assistantMessage: AiChatMessage = {
            id: crypto.randomUUID(),
            role: 'assistant',
            text: this.buildAssistantText(query, notices.length),
            createdAt: new Date().toISOString(),
            notices,
          };

          this.messages.update((messages) => [...messages, assistantMessage]);
          this.isLoading.set(false);
        },
        error: (error) => {
          console.error('Failed to retrieve notices for search results:', error);
          this.errorMessage.set('Failed to retrieve matching notices.');

          const assistantMessage: AiChatMessage = {
            id: crypto.randomUUID(),
            role: 'assistant',
            text: 'Sorry, I could not retrieve matching notices right now.',
            createdAt: new Date().toISOString(),
            notices: [],
          };

          this.messages.update((messages) => [...messages, assistantMessage]);
          this.isLoading.set(false);
        },
      });
  }

  private buildAssistantText(query: string, noticeCount: number): string {
    if (noticeCount === 0) {
      return `I found matching publications for "${query}", but no linked notices were available.`;
    }

    return `I found ${noticeCount} notice${noticeCount > 1 ? 's' : ''} matching "${query}" semantically.`;
  }
}
