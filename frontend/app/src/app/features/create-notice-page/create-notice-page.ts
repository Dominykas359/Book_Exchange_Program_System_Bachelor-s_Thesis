import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize, switchMap } from 'rxjs';

import { PublicationService } from '../../core/services/publication.service';
import { NoticeService } from '../../core/services/notice.service';
import {
  PublicationRequestDto,
  PublicationResponseDto
} from '../../core/models/publication.model';
import { NoticeRequestDto } from '../../core/models/notice.model';
import { UserResponseDto } from '../../core/models/user.model';

@Component({
  selector: 'app-create-notice-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-notice-page.html',
  styleUrl: './create-notice-page.scss'
})
export class CreateNoticePageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly publicationService = inject(PublicationService);
  private readonly noticeService = inject(NoticeService);
  private readonly router = inject(Router);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal('');

  readonly form = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(255)]],
    author: ['', [Validators.required, Validators.maxLength(255)]],
    releaseYear: ['', [Validators.required]],
    language: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['', [Validators.required, Validators.maxLength(50000)]],
    pageCount: [1, [Validators.required, Validators.min(1)]],

    cover: [''],
    colored: [false],
    editionNumber: [1, [Validators.min(1)]],

    wishInReturn: ['', [Validators.required, Validators.maxLength(1000)]]
  });

  submit(): void {
    this.errorMessage.set('');

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.logInvalidControls();
      this.errorMessage.set('Please fill in all required fields correctly.');
      return;
    }

    const currentUser = this.getCurrentUser();

    if (!currentUser) {
      this.errorMessage.set('User not found. Please log in again.');
      return;
    }

    this.isSubmitting.set(true);

    const raw = this.form.getRawValue();

    const publicationPayload: PublicationRequestDto = {
      title: raw.title.trim(),
      author: raw.author.trim(),
      releaseYear: raw.releaseYear,
      language: raw.language.trim(),
      description: raw.description.trim(),
      pageCount: Number(raw.pageCount),
      cover: raw.cover?.trim() ? raw.cover.trim() : undefined,
      colored: raw.colored,
      editionNumber: raw.editionNumber
    };

    this.publicationService.createPublication(publicationPayload).pipe(
      switchMap((createdPublication: PublicationResponseDto) => {
        const noticePayload: NoticeRequestDto = {
          timePosted: new Date().toISOString().split('T')[0],
          wishInReturn: raw.wishInReturn.trim(),
          posterId: currentUser.id,
          publicationId: createdPublication.id
        };

        return this.noticeService.createNotice(noticePayload);
      }),
      finalize(() => this.isSubmitting.set(false))
    ).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Create notice flow failed:', error);

        const backendMessage =
          error?.error?.message ||
          error?.error?.error ||
          error?.message ||
          'Failed to create publication and notice.';

        this.errorMessage.set(backendMessage);
      }
    });
  }

  hasError(controlName: keyof typeof this.form.controls, errorName: string): boolean {
    const control = this.form.controls[controlName];
    return control.touched && control.hasError(errorName);
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

  private logInvalidControls(): void {
    Object.entries(this.form.controls).forEach(([name, control]) => {
      if (control.invalid) {
        console.log(`${name} invalid`, control.errors, control.value);
      }
    });
  }
}
