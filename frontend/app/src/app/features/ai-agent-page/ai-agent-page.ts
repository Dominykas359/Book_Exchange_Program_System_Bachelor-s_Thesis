import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { forkJoin, of } from 'rxjs';
import {
  AiChatMessage,
  AiChatMessageRequestDto
} from '../../core/models/ai-chat.model';
import { NoticeResponseDto } from '../../core/models/notice.model';
import { PublicationSearchResultDto } from '../../core/models/publication.model';
import { SettingsResponseDto } from '../../core/models/settings.model';
import { AiChatService } from '../../core/services/ai-chat.service';
import { NoticeService } from '../../core/services/notice.service';
import { PublicationService } from '../../core/services/publication.service';
import { SettingsService } from '../../core/services/settings.service';
import { NoticeCardComponent } from '../../shared/components/notice-card/notice-card';
import { NoticeModalComponent } from '../../shared/components/notice-modal/notice-modal';

@Component({
  selector: 'app-ai-agent-page',
  standalone: true,
  imports: [CommonModule, FormsModule, NoticeCardComponent, NoticeModalComponent],
  templateUrl: './ai-agent-page.html',
  styleUrl: './ai-agent-page.scss',
})
export class AiAgentPage implements OnInit {
  private readonly publicationService = inject(PublicationService);
  private readonly noticeService = inject(NoticeService);
  private readonly settingsService = inject(SettingsService);
  private readonly aiChatService = inject(AiChatService);

  readonly settings = signal<SettingsResponseDto | null>(null);
  readonly messages = signal<AiChatMessage[]>([]);
  readonly query = signal('');
  readonly isLoading = signal(false);
  readonly isLoadingSettings = signal(false);
  readonly isLoadingHistory = signal(false);
  readonly errorMessage = signal('');
  readonly selectedNotice = signal<NoticeResponseDto | null>(null);

  readonly hasMessages = computed(() => this.messages().length > 0);

  ngOnInit(): void {
    this.loadSettings();
    this.loadHistory();
  }

  updateQuery(value: string): void {
    this.query.set(value);
  }

  sendMessage(): void {
    const trimmedQuery = this.query().trim();

    if (!trimmedQuery || this.isLoading()) {
      return;
    }

    const userId = this.getUserId();

    if (!userId) {
      this.errorMessage.set('No valid user found. Please sign in again.');
      return;
    }

    this.errorMessage.set('');
    this.query.set('');
    this.isLoading.set(true);

    const userMessageRequest: AiChatMessageRequestDto = {
      userId,
      role: 'user',
      text: trimmedQuery,
      noticeIds: []
    };

    this.aiChatService.saveMessage(userMessageRequest).pipe(
      tap((savedUserMessage) => {
        this.messages.update((messages) => [...messages, savedUserMessage]);
      }),
      switchMap(() => {
        const currentSettings = this.settings();

        return this.publicationService.search(
          trimmedQuery,
          currentSettings?.preferredModelKey ?? 'roberta',
          currentSettings?.defaultSearchLimit ?? 10,
          currentSettings?.defaultMinScore ?? 0.45
        );
      })
    ).subscribe({
      next: (results) => {
        this.loadNoticesFromResults(userId, trimmedQuery, results);
      },
      error: (error) => {
        console.error('Semantic search failed:', error);
        this.errorMessage.set('Failed to retrieve matching books.');

        this.saveAssistantMessage(
          userId,
          'Sorry, I could not retrieve matching books right now.',
          []
        );
      },
    });
  }

  clearChat(): void {
    const userId = this.getUserId();

    this.errorMessage.set('');
    this.query.set('');
    this.selectedNotice.set(null);

    if (!userId) {
      this.messages.set([]);
      return;
    }

    this.aiChatService.clearHistory(userId).subscribe({
      next: () => {
        this.messages.set([]);
      },
      error: (error) => {
        console.error('Failed to clear chat history:', error);
        this.errorMessage.set('Failed to clear chat history.');
      }
    });
  }

  openNotice(notice: NoticeResponseDto): void {
    this.selectedNotice.set(notice);
  }

  closeModal(): void {
    this.selectedNotice.set(null);
  }

  trackByMessageId(_: number, message: AiChatMessage): number {
    return message.id;
  }

  trackByNoticeId(_: number, notice: NoticeResponseDto): number {
    return notice.id;
  }

  private loadSettings(): void {
    const userId = this.getUserId();

    if (!userId) {
      console.warn('No valid userId found in localStorage. Using default AI search settings.');
      return;
    }

    this.isLoadingSettings.set(true);

    this.settingsService.getSettings(userId).subscribe({
      next: (settings) => {
        this.settings.set(settings);
        this.isLoadingSettings.set(false);
      },
      error: (error) => {
        console.error('Failed to load settings. Using defaults.', error);
        this.isLoadingSettings.set(false);
      },
    });
  }

  private loadHistory(): void {
    const userId = this.getUserId();

    if (!userId) {
      return;
    }

    this.isLoadingHistory.set(true);

    this.aiChatService.getHistory(userId).subscribe({
      next: (messages) => {
        this.messages.set(messages);
        this.isLoadingHistory.set(false);
      },
      error: (error) => {
        console.error('Failed to load chat history:', error);
        this.isLoadingHistory.set(false);
      }
    });
  }

  private getUserId(): number | null {
    const userIdRaw = localStorage.getItem('userId');

    if (userIdRaw) {
      const parsedUserId = Number(userIdRaw);

      if (Number.isInteger(parsedUserId) && parsedUserId > 0) {
        return parsedUserId;
      }
    }

    const userRaw = localStorage.getItem('user');

    if (!userRaw) {
      return null;
    }

    try {
      const user = JSON.parse(userRaw) as { id?: number };
      const parsedUserId = Number(user.id);

      if (Number.isInteger(parsedUserId) && parsedUserId > 0) {
        return parsedUserId;
      }

      return null;
    } catch {
      return null;
    }
  }

  private loadNoticesFromResults(
    userId: number,
    query: string,
    results: PublicationSearchResultDto[]
  ): void {
    if (results.length === 0) {
      this.saveAssistantMessage(
        userId,
        `I could not find books matching "${query}".`,
        []
      );
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
          this.saveAssistantMessage(
            userId,
            this.buildAssistantText(query, notices.length),
            notices
          );
        },
        error: (error) => {
          console.error('Failed to retrieve notices for search results:', error);
          this.errorMessage.set('Failed to retrieve matching notices.');

          this.saveAssistantMessage(
            userId,
            'Sorry, I could not retrieve matching notices right now.',
            []
          );
        },
      });
  }

  private saveAssistantMessage(
    userId: number,
    text: string,
    notices: NoticeResponseDto[]
  ): void {
    const assistantMessageRequest: AiChatMessageRequestDto = {
      userId,
      role: 'assistant',
      text,
      noticeIds: notices.map((notice) => notice.id)
    };

    this.aiChatService.saveMessage(assistantMessageRequest).subscribe({
      next: (savedAssistantMessage) => {
        const messageToDisplay: AiChatMessage = {
          ...savedAssistantMessage,
          notices
        };

        this.messages.update((messages) => [...messages, messageToDisplay]);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to save assistant message:', error);
        this.errorMessage.set('Failed to save assistant message.');

        const fallbackMessage: AiChatMessage = {
          id: Date.now(),
          role: 'assistant',
          text,
          createdAt: new Date().toISOString(),
          notices
        };

        this.messages.update((messages) => [...messages, fallbackMessage]);
        this.isLoading.set(false);
      }
    });
  }

  private buildAssistantText(query: string, noticeCount: number): string {
    if (noticeCount === 0) {
      return `I found matching publications for "${query}", but no linked notices were available.`;
    }

    return `I found ${noticeCount} notice${noticeCount > 1 ? 's' : ''} matching "${query}" semantically.`;
  }
}
