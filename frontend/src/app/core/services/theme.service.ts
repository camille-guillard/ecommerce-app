import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly STORAGE_KEY = 'ecommerce-theme';
  readonly isDark = signal<boolean>(this.loadTheme());

  toggle(): void {
    this.isDark.set(!this.isDark());
    this.applyTheme();
    this.saveTheme();
  }

  init(): void {
    this.applyTheme();
  }

  private applyTheme(): void {
    document.documentElement.setAttribute(
      'data-theme',
      this.isDark() ? 'dark' : 'light'
    );
  }

  private loadTheme(): boolean {
    if (typeof window === 'undefined') return true;
    const stored = localStorage.getItem(this.STORAGE_KEY);
    return stored === null ? true : stored === 'dark';
  }

  private saveTheme(): void {
    localStorage.setItem(this.STORAGE_KEY, this.isDark() ? 'dark' : 'light');
  }
}
