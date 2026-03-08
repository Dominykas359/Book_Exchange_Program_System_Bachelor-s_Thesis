import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule, HttpErrorResponse } from '@angular/common/http';

import { ErrorType, validatePasswords } from '../../../shared/validation/password.validation';

@Component({
  standalone: true,
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, HttpClientModule],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  // CHANGE THIS to your backend url
  private apiUrl = 'http://localhost:8080';

  readonly ErrorType = ErrorType; // so template can use ErrorType.EmailRegistered etc

  isSubmitting = signal(false);

  emailError = signal<{ message: string; errorType: ErrorType }>({
    message: '',
    errorType: ErrorType.NoError,
  });

  passwordError = signal<{ message: string; errorType: ErrorType }>({
    message: '',
    errorType: ErrorType.NoError,
  });

  serverError = signal<string>('');

  form: FormGroup;

  hasBlockingError = computed(() =>
    this.emailError().errorType !== ErrorType.NoError ||
    this.passwordError().errorType !== ErrorType.NoError
  );

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    // ✅ create form in constructor so fb exists
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      birthday: [new Date().toISOString().split('T')[0], [Validators.required]],
      password: ['', [Validators.required]],
      confirmPassword: ['', [Validators.required]],
    });
  }

  handlePasswordValidation() {
    const v = this.form.getRawValue() as any;
    const validationError = validatePasswords(v.password ?? '', v.confirmPassword ?? '');

    if (validationError === ErrorType.TooWeakPassword) {
      this.passwordError.set({
        message: 'Password must be at least 8 characters and include uppercase, lowercase and a number.',
        errorType: ErrorType.TooWeakPassword,
      });
      return;
    }

    if (validationError === ErrorType.PasswordNotMatch) {
      this.passwordError.set({
        message: 'Passwords do not match.',
        errorType: ErrorType.PasswordNotMatch,
      });
      return;
    }

    this.passwordError.set({
      message: '',
      errorType: ErrorType.NoError,
    });
  }

  checkEmail() {
    const email = (this.form.get('email')?.value ?? '').trim();
    if (!email) return;
    if (this.form.get('email')?.invalid) return;

    this.emailError.set({ message: '', errorType: ErrorType.NoError });

    this.http.get(`${this.apiUrl}/auth/check-email/${encodeURIComponent(email)}`).subscribe({
      next: () => {
        this.emailError.set({
          message: 'Email is already registered.',
          errorType: ErrorType.EmailRegistered,
        });
      },
      error: (err: HttpErrorResponse) => {
        // assume 404 => not found => ok
        if (err.status === 404) {
          this.emailError.set({ message: '', errorType: ErrorType.NoError });
        }
      },
    });
  }

  submit() {
    if (this.isSubmitting()) return;

    this.serverError.set('');
    this.handlePasswordValidation();

    if (this.form.invalid || this.hasBlockingError()) return;

    this.isSubmitting.set(true);

    const v = this.form.getRawValue() as any;
    const payload = {
      email: v.email,
      password: v.password,
      firstName: v.firstName,
      lastName: v.lastName,
      birthday: v.birthday,
    };

    this.http.post(`${this.apiUrl}/auth/registration`, payload).subscribe({
      next: () => this.router.navigateByUrl('/login'),
      error: (err: HttpErrorResponse) => {
        this.serverError.set(err.error?.message ?? 'Registration failed.');
        this.isSubmitting.set(false);
      },
      complete: () => this.isSubmitting.set(false),
    });
  }
}
