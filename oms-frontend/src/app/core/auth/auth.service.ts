import { Injectable, inject } from '@angular/core';
import { signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { UserModel, LoginRequest, RegisterRequest, AuthResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly API_URL = 'http://localhost:8080/api/v1/auth';

  // Signals para estado de auth
  private readonly _currentUser = signal<UserModel | null>(null);
  private readonly _isLoading = signal<boolean>(false);
  private readonly _token = signal<string | null>(null); // Token solo en memoria (más seguro)

  // Signals de solo lectura
  readonly currentUser = this._currentUser.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly token = this._token.asReadonly();

  // Computeds
  readonly isAuthenticated = computed(() => this._currentUser() !== null);
  readonly isAdmin = computed(() => this._currentUser()?.role === 'ADMIN');

  constructor() {
    // Token NO se persiste en storage (localStorage/sessionStorage son vulnerables a XSS)
    // Solo se mantiene en memoria usando signals
  }

  login(credentials: LoginRequest) {
    this._isLoading.set(true);
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials);
  }

  register(data: RegisterRequest) {
    this._isLoading.set(true);
    return this.http.post<AuthResponse>(`${this.API_URL}/register`, data);
  }

  logout() {
    this._currentUser.set(null);
    this._token.set(null);
    this.router.navigate(['/login']);
  }

  refreshToken(refreshToken: string) {
    return this.http.post<AuthResponse>(`${this.API_URL}/refresh`, { refreshToken });
  }

  setAuthData(response: AuthResponse) {
    this._token.set(response.accessToken);
    this._currentUser.set(response.user);
    this._isLoading.set(false);
  }

  clearAuthData() {
    this._currentUser.set(null);
    this._token.set(null);
    this._isLoading.set(false);
  }
}
