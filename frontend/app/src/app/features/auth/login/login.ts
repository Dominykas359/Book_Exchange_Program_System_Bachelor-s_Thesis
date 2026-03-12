import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule, HttpErrorResponse } from '@angular/common/http';

import { AuthenticationResponseDto } from '../../../core/models/user.model';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, HttpClientModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {

  private apiUrl = 'http://localhost:8080';

  isSubmitting = signal(false);
  serverError = signal<string>('');

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  submit() {

    if (this.isSubmitting() || this.form.invalid) return;

    this.isSubmitting.set(true);
    this.serverError.set('');

    this.http.post<AuthenticationResponseDto>(
      `${this.apiUrl}/auth/login`,
      this.form.getRawValue()
    ).subscribe({

      next: (res) => {

        // Save token
        localStorage.setItem('token', res.token);

        // Save user data
        const user = {
          id: res.id,
          email: res.email,
          firstName: res.firstName,
          lastName: res.lastName
        };

        localStorage.setItem('user', JSON.stringify(user));

        this.router.navigateByUrl('/dashboard');
      },

      error: (err: HttpErrorResponse) => {
        this.serverError.set(err.error?.message ?? 'Login failed.');
        this.isSubmitting.set(false);
      },

      complete: () => this.isSubmitting.set(false),
    });
  }
}
