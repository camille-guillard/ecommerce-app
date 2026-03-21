import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthResponse, User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'ecommerce-token';
  private readonly USER_KEY = 'ecommerce-user';

  private readonly _token = signal<string | null>(this.loadToken());
  private readonly _currentUser = signal<User | null>(this.loadUser());

  readonly token = this._token.asReadonly();
  readonly currentUser = this._currentUser.asReadonly();
  readonly isLoggedIn = computed(() => !!this._token());
  readonly isAdmin = computed(() => this._currentUser()?.roles?.includes('ADMIN') ?? false);

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/api/auth/login', { username, password })
      .pipe(tap((res) => this.setSession(res)));
  }

  register(data: {
    username: string;
    password: string;
    email: string;
    firstName: string;
    lastName: string;
    street: string;
    city: string;
    postalCode: string;
  }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/api/auth/register', data)
      .pipe(tap((res) => this.setSession(res)));
  }

  updateProfile(data: {
    email: string;
    firstName: string;
    lastName: string;
    street: string;
    city: string;
    postalCode: string;
  }): Observable<User> {
    return this.http.put<User>('/api/auth/me', data).pipe(
      tap((user) => {
        this._currentUser.set(user);
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
      })
    );
  }

  logout(): void {
    this._token.set(null);
    this._currentUser.set(null);
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }

  private setSession(res: AuthResponse): void {
    this._token.set(res.token);
    this._currentUser.set(res.user);
    localStorage.setItem(this.TOKEN_KEY, res.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(res.user));
  }

  private loadToken(): string | null {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private loadUser(): User | null {
    if (typeof window === 'undefined') return null;
    const stored = localStorage.getItem(this.USER_KEY);
    return stored ? JSON.parse(stored) : null;
  }
}
