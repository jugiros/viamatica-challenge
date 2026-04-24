import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { AuthService } from './auth.service';
import { UserModel, LoginRequest, AuthResponse } from '../models';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let mockRouter: jasmine.SpyObj<Router>;

  const mockUser: UserModel = {
    id: 1,
    name: 'Test User',
    email: 'test@example.com',
    role: 'USER',
    active: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z'
  };

  const mockAdminUser: UserModel = {
    id: 2,
    name: 'Admin User',
    email: 'admin@example.com',
    role: 'ADMIN',
    active: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z'
  };

  const mockAuthResponse: AuthResponse = {
    accessToken: 'mock-access-token',
    refreshToken: 'mock-refresh-token',
    expiresIn: 3600,
    user: mockUser
  };

  beforeEach(() => {
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: Router, useValue: mockRouter }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Signals', () => {
    it('currentUser debe ser null inicialmente', () => {
      expect(service.currentUser()).toBeNull();
    });

    it('isLoading debe ser false inicialmente', () => {
      expect(service.isLoading()).toBe(false);
    });

    it('token debe ser null inicialmente', () => {
      expect(service.token()).toBeNull();
    });
  });

  describe('Computed Signals', () => {
    it('isAuthenticated debe ser false cuando currentUser es null', () => {
      expect(service.isAuthenticated()).toBe(false);
    });

    it('isAuthenticated debe ser true cuando currentUser tiene valor', () => {
      service.setAuthData(mockAuthResponse);
      expect(service.isAuthenticated()).toBe(true);
    });

    it('isAdmin debe ser false cuando currentUser no es ADMIN', () => {
      service.setAuthData(mockAuthResponse);
      expect(service.isAdmin()).toBe(false);
    });

    it('isAdmin debe ser true cuando currentUser es ADMIN', () => {
      const adminResponse: AuthResponse = {
        accessToken: 'mock-access-token',
        refreshToken: 'mock-refresh-token',
        user: mockAdminUser
      };
      service.setAuthData(adminResponse);
      expect(service.isAdmin()).toBe(true);
    });
  });

  describe('login', () => {
    it('debe hacer POST a /login y actualizar isLoading', () => {
      const credentials: LoginRequest = {
        email: 'test@example.com',
        password: 'password'
      };

      service.login(credentials).subscribe();

      expect(service.isLoading()).toBe(true);

      const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(credentials);
      
      req.flush(mockAuthResponse);
    });

    it('debe retornar Observable con AuthResponse', (done) => {
      const credentials: LoginRequest = {
        email: 'test@example.com',
        password: 'password'
      };

      service.login(credentials).subscribe(response => {
        expect(response).toEqual(mockAuthResponse);
        done();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/login');
      req.flush(mockAuthResponse);
    });
  });

  describe('register', () => {
    it('debe hacer POST a /register y actualizar isLoading', () => {
      const registerData = {
        name: 'Test User',
        email: 'test@example.com',
        password: 'password',
        role: 'USER' as const
      };

      service.register(registerData).subscribe();

      expect(service.isLoading()).toBe(true);

      const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/register');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(registerData);
      
      req.flush(mockAuthResponse);
    });
  });

  describe('logout', () => {
    it('debe limpiar currentUser y token', () => {
      service.setAuthData(mockAuthResponse);
      expect(service.currentUser()).not.toBeNull();
      expect(service.token()).not.toBeNull();

      service.logout();

      expect(service.currentUser()).toBeNull();
      expect(service.token()).toBeNull();
    });

    it('debe navegar a /login', () => {
      service.logout();
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('refreshToken', () => {
    it('debe hacer POST a /refresh', () => {
      const refreshToken = 'mock-refresh-token';

      service.refreshToken(refreshToken).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/refresh');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ refreshToken });
      
      req.flush(mockAuthResponse);
    });
  });

  describe('setAuthData', () => {
    it('debe actualizar currentUser, token y isLoading', () => {
      service.setAuthData(mockAuthResponse);

      expect(service.currentUser()).toEqual(mockUser);
      expect(service.token()).toBe(mockAuthResponse.accessToken);
      expect(service.isLoading()).toBe(false);
    });
  });

  describe('clearAuthData', () => {
    it('debe limpiar currentUser, token y establecer isLoading a false', () => {
      service.setAuthData(mockAuthResponse);
      expect(service.currentUser()).not.toBeNull();

      service.clearAuthData();

      expect(service.currentUser()).toBeNull();
      expect(service.token()).toBeNull();
      expect(service.isLoading()).toBe(false);
    });
  });
});
