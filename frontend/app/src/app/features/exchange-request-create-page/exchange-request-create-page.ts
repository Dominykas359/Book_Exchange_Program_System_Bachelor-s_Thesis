import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize, switchMap } from 'rxjs';

import { NoticeService } from '../../core/services/notice.service';
import { PublicationService } from '../../core/services/publication.service';
import { ExchangeRequestService } from '../../core/services/exchange-request.service';

import { NoticeResponseDto } from '../../core/models/notice.model';
import {
  PublicationRequestDto,
  PublicationResponseDto
} from '../../core/models/publication.model';
import { ExchangeRequestRequestDto } from '../../core/models/exchange-request.model';
import { UserResponseDto } from '../../core/models/user.model';

@Component({
  selector: 'app-exchange-request-create-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './exchange-request-create-page.html',
  styleUrl: './exchange-request-create-page.scss'
})
export class ExchangeRequestCreatePage implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly noticeService = inject(NoticeService);
  private readonly publicationService = inject(PublicationService);
  private readonly exchangeRequestService = inject(ExchangeRequestService);

  readonly notice = signal<NoticeResponseDto | null>(null);
  readonly isLoadingNotice = signal(true);
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal('');

  readonly form = this.formBuilder.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(255)]],
    author: ['', [Validators.required, Validators.maxLength(255)]],
    releaseYear: ['', [Validators.required]],
    language: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['', [Validators.required, Validators.maxLength(50000)]],
    pageCount: [1, [Validators.required, Validators.min(1)]],

    cover: [''],
    colored: [false],
    editionNumber: [1, [Validators.min(1)]],

    requesterAddress: ['', [Validators.required, Validators.maxLength(2000)]]
  });

  ngOnInit(): void {
    const noticeIdParam = this.route.snapshot.queryParamMap.get('noticeId');

    if (!noticeIdParam) {
      this.errorMessage.set('Notice id was not provided.');
      this.isLoadingNotice.set(false);
      return;
    }

    const noticeId = Number(noticeIdParam);

    if (Number.isNaN(noticeId) || noticeId <= 0) {
      this.errorMessage.set('Invalid notice id.');
      this.isLoadingNotice.set(false);
      return;
    }

    this.loadNotice(noticeId);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const currentNotice = this.notice();
    const currentUser = this.getCurrentUser();

    if (!currentNotice) {
      this.errorMessage.set('Notice was not loaded.');
      return;
    }

    if (!currentUser) {
      this.errorMessage.set('User not found. Please log in again.');
      return;
    }

    if (currentUser.id === currentNotice.poster.id) {
      this.errorMessage.set('You cannot create an exchange request for your own notice.');
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set('');

    const raw = this.form.getRawValue();

    const publicationPayload: PublicationRequestDto = {
      title: raw.title.trim(),
      author: raw.author.trim(),
      releaseYear: raw.releaseYear,
      language: raw.language.trim(),
      description: raw.description.trim(),
      pageCount: Number(raw.pageCount),
      cover: raw.cover.trim() ? raw.cover.trim() : undefined,
      colored: raw.colored,
      editionNumber: raw.editionNumber
    };

    this.publicationService.createPublication(publicationPayload).pipe(
      switchMap((createdPublication: PublicationResponseDto) => {
        const payload: ExchangeRequestRequestDto = {
          requestedTime: new Date().toISOString().split('T')[0],
          userId: currentUser.id,
          requestedFromUserId: currentNotice.poster.id,
          noticeId: currentNotice.id,
          givenPublicationId: createdPublication.id,
          receivedPublicationId: currentNotice.publication.id,
          requesterAddress: raw.requesterAddress.trim()
        };

        return this.exchangeRequestService.createExchangeRequest(payload);
      }),
      finalize(() => this.isSubmitting.set(false))
    ).subscribe({
      next: () => {
        this.router.navigate(['/exchange-requests']);
      },
      error: (error: any) => {
        console.error('Failed to create exchange request:', error);

        const backendMessage =
          error?.error?.message ||
          error?.error?.error ||
          error?.message ||
          'Failed to create exchange request.';

        this.errorMessage.set(backendMessage);
      }
    });
  }

  hasError(controlName: keyof typeof this.form.controls, errorCode: string): boolean {
    const control = this.form.controls[controlName];
    return control.touched && control.hasError(errorCode);
  }

  getPosterFullName(): string {
    const currentNotice = this.notice();

    if (!currentNotice) {
      return '';
    }

    const firstName = currentNotice.poster?.firstName ?? '';
    const lastName = currentNotice.poster?.lastName ?? '';
    const fullName = `${firstName} ${lastName}`.trim();

    return fullName || currentNotice.poster?.email || 'Unknown user';
  }

  private loadNotice(noticeId: number): void {
    this.isLoadingNotice.set(true);
    this.errorMessage.set('');

    this.noticeService.getNoticeById(noticeId).subscribe({
      next: (notice: NoticeResponseDto) => {
        this.notice.set(notice);
        this.isLoadingNotice.set(false);
      },
      error: (error: any) => {
        console.error('Failed to load notice:', error);
        this.errorMessage.set('Failed to load notice.');
        this.isLoadingNotice.set(false);
      }
    });
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
