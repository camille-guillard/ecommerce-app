import { TestBed } from '@angular/core/testing';
import { ThemeService } from './theme.service';

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

describe('ThemeService', () => {
  let service: ThemeService;

  beforeEach(() => {
    localStorageMock.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(ThemeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should default to dark theme', () => {
    expect(service.isDark()).toBe(true);
  });

  it('should toggle to light theme', () => {
    service.toggle();
    expect(service.isDark()).toBe(false);
  });

  it('should toggle back to dark', () => {
    service.toggle();
    service.toggle();
    expect(service.isDark()).toBe(true);
  });

  it('should persist theme in localStorage', () => {
    service.toggle();
    expect(localStorageMock.getItem('ecommerce-theme')).toBe('light');
  });

  it('should apply data-theme attribute on init', () => {
    service.init();
    expect(document.documentElement.getAttribute('data-theme')).toBe('dark');
  });

  it('should apply light data-theme after toggle', () => {
    service.toggle();
    expect(document.documentElement.getAttribute('data-theme')).toBe('light');
  });
});
