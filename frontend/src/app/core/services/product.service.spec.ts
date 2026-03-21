import { TestBed } from '@angular/core/testing';
import { importProvidersFrom } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TranslateModule } from '@ngx-translate/core';
import { ProductService } from './product.service';

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

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), importProvidersFrom(TranslateModule.forRoot())],
    });
    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call API with no params when none provided', () => {
    service.getProducts({}).subscribe();

    const req = httpMock.expectOne((r) => r.url === '/api/products');
    expect(req.request.method).toBe('GET');
  });

  it('should pass categoryId param', () => {
    service.getProducts({ categoryId: 1 }).subscribe();

    const req = httpMock.expectOne((r) => r.url === '/api/products');
    expect(req.request.params.get('categoryId')).toBe('1');
  });

  it('should pass search param', () => {
    service.getProducts({ search: 'cuir' }).subscribe();

    const req = httpMock.expectOne((r) => r.url === '/api/products');
    expect(req.request.params.get('search')).toBe('cuir');
  });

  it('should pass pagination params', () => {
    service.getProducts({ page: 2, size: 12 }).subscribe();

    const req = httpMock.expectOne((r) => r.url === '/api/products');
    expect(req.request.params.get('page')).toBe('2');
    expect(req.request.params.get('size')).toBe('12');
  });

  it('should pass sort param', () => {
    service.getProducts({ sort: 'price,asc' }).subscribe();

    const req = httpMock.expectOne((r) => r.url === '/api/products');
    expect(req.request.params.get('sort')).toBe('price,asc');
  });

  it('should call getProductById with correct URL', () => {
    service.getProductById(42).subscribe();

    const req = httpMock.expectOne((r) => r.url === '/api/products/42');
    expect(req.request.method).toBe('GET');
  });
});
