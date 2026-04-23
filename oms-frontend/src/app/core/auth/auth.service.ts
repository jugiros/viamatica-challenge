import { Injectable, inject } from '@angular/core';
import { signal, computed, effect } from '@angular/core';
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
  private readonly _token = signal<string | null>(this.getTokenFromStorage());

  // Signals de solo lectura
  readonly currentUser = this._currentUser.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly token = this._token.asReadonly();

  // Computeds
  readonly isAuthenticated = computed(() => this._currentUser() !== null);
  readonly isAdmin = computed(() => this._currentUser()?.role === 'ADMIN');

  constructor() {
    // Efecto para persistir el token en localStorage
    effect(() => {
      const token = this._token();
      if (token) {
        localStorage.setItem('token', token);
      } else {
        localStorage.removeItem('token');
      }
    });

    // Cargar usuario desde localStorage si existe token
    this.loadUserFromStorage();
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

  private getTokenFromStorage(): string | null {
    return localStorage.getItem('token');
  }

  private loadUserFromStorage() {
    const token = this.getTokenFromStorage();
    if (token) {
      // Aquí podríamos decodificar el JWT para obtener el usuario
      // Por ahora, solo establecemos el token
      this._token.set(token);
    }
  }
}
