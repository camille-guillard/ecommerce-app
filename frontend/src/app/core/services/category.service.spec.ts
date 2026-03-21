import { TestBed } from '@angular/core/testing';
import { importProvidersFrom } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TranslateModule } from '@ngx-translate/core';
import { CategoryService } from './category.service';

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

describe('CategoryService', () => {
  let service: CategoryService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), importProvidersFrom(TranslateModule.forRoot())],
    });
    service = TestBed.inject(CategoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call /api/categories', () => {
    service.getCategories().subscribe();

    const req = httpMock.expectOne((r) => r.url === '/api/categories');
    expect(req.request.method).toBe('GET');
  });

  it('should return categories', () => {
    const mockCategories = [
      { id: 1, name: 'food', displayName: 'Alimentaire' },
      { id: 2, name: 'drinks', displayName: 'Boissons' },
    ];

    service.getCategories().subscribe((cats) => {
      expect(cats).toHaveLength(2);
      expect(cats[0].displayName).toBe('Alimentaire');
    });

    const req = httpMock.expectOne((r) => r.url === '/api/categories');
    req.flush(mockCategories);
  });
});
