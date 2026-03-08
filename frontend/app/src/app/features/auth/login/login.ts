import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule, HttpErrorResponse } from '@angular/common/http';

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

    this.http.post(`${this.apiUrl}/auth/login`, this.form.getRawValue()).subscribe({
      next: (res: any) => {
        // If your backend returns token:
        // localStorage.setItem('token', res.token);

        this.router.navigateByUrl('/dashboard'); // change to /dashboard when you add it
      },
      error: (err: HttpErrorResponse) => {
        this.serverError.set(err.error?.message ?? 'Login failed.');
        this.isSubmitting.set(false);
      },
      complete: () => this.isSubmitting.set(false),
    });
  }
}
