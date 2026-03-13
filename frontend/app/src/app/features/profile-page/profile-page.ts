import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { UserService } from '../../core/services/user.service';
import {
  ChangePasswordRequestDto,
  UserResponseDto,
  UserUpdateRequestDto
} from '../../core/models/user.model';
import { ErrorType, validatePasswords } from '../../shared/validation/password.validation';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.scss'
})
export class ProfilePage {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  readonly ErrorType = ErrorType;

  readonly infoError = signal('');
  readonly infoSuccess = signal('');
  readonly passwordSuccess = signal('');
  readonly deleteError = signal('');
  readonly isSavingInfo = signal(false);
  readonly isChangingPassword = signal(false);
  readonly isDeleting = signal(false);
  readonly showDeleteConfirm = signal(false);

  readonly emailError = signal<{ message: string; errorType: ErrorType }>({
    message: '',
    errorType: ErrorType.NoError
  });

  readonly passwordError = signal<{ message: string; errorType: ErrorType }>({
    message: '',
    errorType: ErrorType.NoError
  });

  private readonly currentUser = this.getCurrentUser();

  readonly infoForm = this.fb.nonNullable.group({
    email: [this.currentUser?.email ?? '', [Validators.required, Validators.email]],
    firstName: [this.currentUser?.firstName ?? '', [Validators.required]],
    lastName: [this.currentUser?.lastName ?? '', [Validators.required]]
  });

  readonly passwordForm = this.fb.nonNullable.group({
    password: ['', [Validators.required]],
    confirmPassword: ['', [Validators.required]]
  });

  readonly hasBlockingInfoError = computed(() =>
    this.emailError().errorType !== ErrorType.NoError
  );

  readonly hasBlockingPasswordError = computed(() =>
    this.passwordError().errorType !== ErrorType.NoError
  );

  saveInfo(): void {
    this.infoError.set('');
    this.infoSuccess.set('');

    if (this.infoForm.invalid || this.hasBlockingInfoError()) {
      this.infoForm.markAllAsTouched();
      return;
    }

    if (!this.currentUser) {
      this.infoError.set('User not found. Please log in again.');
      return;
    }

    this.isSavingInfo.set(true);

    const payload: UserUpdateRequestDto = {
      email: this.infoForm.getRawValue().email.trim(),
      firstName: this.infoForm.getRawValue().firstName.trim(),
      lastName: this.infoForm.getRawValue().lastName.trim()
    };

    this.userService.updateUser(this.currentUser.id, payload).subscribe({
      next: (updatedUser) => {
        localStorage.setItem('user', JSON.stringify(updatedUser));
        this.infoSuccess.set('Profile information updated successfully.');
        this.isSavingInfo.set(false);
      },
      error: (error) => {
        console.error(error);
        this.infoError.set(error?.error?.message || 'Failed to update profile.');
        this.isSavingInfo.set(false);
      }
    });
  }

  handlePasswordValidation(): void {
    const raw = this.passwordForm.getRawValue();
    const validationError = validatePasswords(raw.password, raw.confirmPassword);

    if (validationError === ErrorType.TooWeakPassword) {
      this.passwordError.set({
        message: 'Password must be at least 8 characters and include uppercase, lowercase and a number.',
        errorType: ErrorType.TooWeakPassword
      });
      return;
    }

    if (validationError === ErrorType.PasswordNotMatch) {
      this.passwordError.set({
        message: 'Passwords do not match.',
        errorType: ErrorType.PasswordNotMatch
      });
      return;
    }

    this.passwordError.set({
      message: '',
      errorType: ErrorType.NoError
    });
  }

  changePassword(): void {
    this.passwordSuccess.set('');
    this.handlePasswordValidation();

    if (this.passwordForm.invalid || this.hasBlockingPasswordError()) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    if (!this.currentUser) {
      this.passwordError.set({
        message: 'User not found. Please log in again.',
        errorType: ErrorType.PasswordNotMatch
      });
      return;
    }

    this.isChangingPassword.set(true);

    const payload: ChangePasswordRequestDto = {
      password: this.passwordForm.getRawValue().password
    };

    this.userService.changePassword(this.currentUser.id, payload).subscribe({
      next: () => {
        this.passwordSuccess.set('Password changed successfully.');
        this.passwordForm.reset({
          password: '',
          confirmPassword: ''
        });
        this.passwordError.set({
          message: '',
          errorType: ErrorType.NoError
        });
        this.isChangingPassword.set(false);
      },
      error: (error) => {
        console.error(error);
        this.passwordError.set({
          message: error?.error?.message || 'Failed to change password.',
          errorType: ErrorType.PasswordNotMatch
        });
        this.isChangingPassword.set(false);
      }
    });
  }

  checkEmail(): void {
    const email = this.infoForm.getRawValue().email.trim();

    if (!email) return;
    if (this.infoForm.controls.email.invalid) return;

    if (email === this.currentUser?.email) {
      this.emailError.set({
        message: '',
        errorType: ErrorType.NoError
      });
      return;
    }

    this.emailError.set({
      message: '',
      errorType: ErrorType.NoError
    });

    this.userService.getUserByEmail(email).subscribe({
      next: () => {
        this.emailError.set({
          message: 'Email is already registered.',
          errorType: ErrorType.EmailRegistered
        });
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 404) {
          this.emailError.set({
            message: '',
            errorType: ErrorType.NoError
          });
        }
      }
    });
  }

  openDeleteConfirm(): void {
    this.deleteError.set('');
    this.showDeleteConfirm.set(true);
  }

  closeDeleteConfirm(): void {
    this.showDeleteConfirm.set(false);
  }

  confirmDelete(): void {
    if (!this.currentUser) {
      this.deleteError.set('User not found. Please log in again.');
      return;
    }

    this.isDeleting.set(true);

    this.userService.deleteUser(this.currentUser.id).subscribe({
      next: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        this.showDeleteConfirm.set(false);
        this.router.navigate(['/login']);
        this.isDeleting.set(false);
      },
      error: (error) => {
        console.error(error);
        this.deleteError.set(error?.error?.message || 'Failed to delete account.');
        this.isDeleting.set(false);
      }
    });
  }

  hasInfoError(controlName: keyof typeof this.infoForm.controls, errorName: string): boolean {
    const control = this.infoForm.controls[controlName];
    return control.touched && control.hasError(errorName);
  }

  hasPasswordFieldError(controlName: keyof typeof this.passwordForm.controls, errorName: string): boolean {
    const control = this.passwordForm.controls[controlName];
    return control.touched && control.hasError(errorName);
  }

  private getCurrentUser(): UserResponseDto | null {
    const raw = localStorage.getItem('user');

    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as UserResponseDto;
    } catch {
      return null;
    }
  }
}
