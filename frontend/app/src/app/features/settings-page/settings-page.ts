import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SettingsResponseDto } from '../../core/models/settings.model';
import { SettingsService } from '../../core/services/settings.service';

type SearchModelKey = 'bert' | 'distilbert' | 'roberta';

interface SearchModelOption {
  key: SearchModelKey;
  label: string;
  description: string;
}

@Component({
  selector: 'app-settings-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './settings-page.html',
  styleUrl: './settings-page.scss',
})
export class SettingsPage implements OnInit {
  private readonly settingsService = inject(SettingsService);

  readonly modelOptions: SearchModelOption[] = [
    {
      key: 'bert',
      label: 'BERT',
      description:
        'Recommended default. Gives the most reliable semantic matching for most users and is the safest choice for general book searches.',
    },
    {
      key: 'distilbert',
      label: 'DistilBERT',
      description:
        'A lighter model. Good when you want a simpler, faster-feeling option, but semantic matches may be broader or less accurate.',
    },
    {
      key: 'roberta',
      label: 'RoBERTa',
      description:
        'An alternative semantic model that can rank some searches differently. Useful if users want to try a different matching style.',
    },
  ];

  readonly resultCountOptions = [5, 10, 20, 30];

  readonly selectedModel = signal<SearchModelKey>('bert');
  readonly strictness = signal<number>(50);
  readonly resultsCount = signal<number>(10);
  readonly saveMessage = signal('');
  readonly errorMessage = signal('');
  readonly isLoading = signal(false);

  readonly strictnessLabel = computed(() => {
    const value = this.strictness();

    if (value <= 30) {
      return 'Loose';
    }

    if (value <= 70) {
      return 'Balanced';
    }

    return 'Strict';
  });

  readonly strictnessDescription = computed(() => {
    const value = this.strictness();

    if (value <= 30) {
      return 'Shows broader semantic matches. Better for discovery, but may include less relevant results.';
    }

    if (value <= 70) {
      return 'Balances relevance and variety. A good default for most searches.';
    }

    return 'Shows only tighter semantic matches. Better precision, but can miss broader relevant items.';
  });

  readonly selectedModelInfo = computed(() =>
    this.modelOptions.find((model) => model.key === this.selectedModel()) ?? this.modelOptions[0]
  );

  ngOnInit(): void {
    this.loadFromBackend();
  }

  updateModel(model: SearchModelKey): void {
    this.selectedModel.set(model);
    this.saveMessage.set('');
    this.errorMessage.set('');
  }

  updateStrictness(value: string | number): void {
    this.strictness.set(Number(value));
    this.saveMessage.set('');
    this.errorMessage.set('');
  }

  updateResultsCount(value: string | number): void {
    this.resultsCount.set(Number(value));
    this.saveMessage.set('');
    this.errorMessage.set('');
  }

  saveSettings(): void {
    const userId = this.getUserId();

    if (!userId) {
      this.errorMessage.set('User is not logged in.');
      this.saveMessage.set('');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.saveMessage.set('');

    this.settingsService.updateSettings(userId, {
      theme: 'light',
      language: 'en',
      emailNotifications: true,
      preferredModelKey: this.selectedModel(),
      defaultSearchLimit: this.resultsCount(),
      defaultMinScore: this.strictnessToMinScore(this.strictness()),
    }).subscribe({
      next: () => {
        this.saveMessage.set('Settings saved.');
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to save settings:', error);
        this.errorMessage.set('Failed to save settings.');
        this.isLoading.set(false);
      },
    });
  }

  resetDefaults(): void {
    this.selectedModel.set('bert');
    this.resultsCount.set(10);
    this.strictness.set(50);
    this.saveMessage.set('');
    this.errorMessage.set('');
  }

  trackByModelKey(_: number, model: SearchModelOption): string {
    return model.key;
  }

  private loadFromBackend(): void {
    const userId = this.getUserId();

    if (!userId) {
      this.errorMessage.set('User is not logged in.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.saveMessage.set('');

    this.settingsService.getSettings(userId).subscribe({
      next: (settings: SettingsResponseDto) => {
        this.selectedModel.set(this.toSearchModelKey(settings.preferredModelKey));
        this.resultsCount.set(this.toValidResultsCount(settings.defaultSearchLimit));
        this.strictness.set(this.minScoreToStrictness(settings.defaultMinScore));
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load settings:', error);
        this.errorMessage.set('Failed to load settings.');
        this.isLoading.set(false);
      },
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

  private strictnessToMinScore(strictness: number): number {
    return Number((strictness / 100).toFixed(2));
  }

  private minScoreToStrictness(minScore: number): number {
    return Math.round(minScore * 100);
  }

  private toSearchModelKey(value: string): SearchModelKey {
    if (value === 'bert' || value === 'distilbert' || value === 'roberta') {
      return value;
    }

    return 'bert';
  }

  private toValidResultsCount(value: number): number {
    return this.resultCountOptions.includes(value) ? value : 10;
  }
}
