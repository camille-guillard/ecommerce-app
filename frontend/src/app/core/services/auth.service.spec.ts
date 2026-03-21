import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] ?? null,
    setItem: (key: string, value: string) => { store[key] = value; },
    removeItem: (key: string) => { delete store[key]; },
    clear: () => { store = {}; },
  };
})();

Object.defineProperty(globalThis, 'localStorage', { value: localStorageMock, writable: true });

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorageMock.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
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

  it('should start as not logged in', () => {
    expect(service.isLoggedIn()).toBe(false);
    expect(service.currentUser()).toBeNull();
  });

  it('should login and store token', () => {
    const mockResponse = {
      token: 'jwt-token-123',
      user: { id: 1, username: 'user001', email: 'u@test.com', firstName: 'A', lastName: 'B', address: '1 rue' },
    };

    service.login('user001', 'user001').subscribe();

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ username: 'user001', password: 'user001' });
    req.flush(mockResponse);

    expect(service.isLoggedIn()).toBe(true);
    expect(service.currentUser()?.username).toBe('user001');
    expect(service.token()).toBe('jwt-token-123');
  });

  it('should logout and clear state', () => {
    const mockResponse = {
      token: 'jwt-token-123',
      user: { id: 1, username: 'user001', email: 'u@test.com', firstName: 'A', lastName: 'B', address: '1 rue' },
    };

    service.login('user001', 'user001').subscribe();
    httpMock.expectOne('/api/auth/login').flush(mockResponse);

    service.logout();

    expect(service.isLoggedIn()).toBe(false);
    expect(service.currentUser()).toBeNull();
    expect(service.token()).toBeNull();
  });

  it('should call register endpoint', () => {
    service.register({
      username: 'newuser',
      password: 'pass',
      email: 'n@t.com',
      firstName: 'X',
      lastName: 'Y',
      street: 'rue',
      city: 'Paris',
      postalCode: '75001',
    }).subscribe();

    const req = httpMock.expectOne('/api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body.username).toBe('newuser');
  });

  it('should call updateProfile endpoint', () => {
    service.updateProfile({
      email: 'new@t.com',
      firstName: 'New',
      lastName: 'Name',
      street: 'rue',
      city: 'Lyon',
      postalCode: '69001',
    }).subscribe();

    const req = httpMock.expectOne('/api/auth/me');
    expect(req.request.method).toBe('PUT');
  });
});
