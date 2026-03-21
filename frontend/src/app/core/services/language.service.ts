import { Injectable, signal } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  private readonly STORAGE_KEY = 'ecommerce-lang';
  readonly currentLang = signal<string>(this.loadLang());

  constructor(private translate: TranslateService) {
    this.translate.setDefaultLang('fr');
    this.translate.use(this.currentLang());
  }

  setLanguage(lang: string): void {
    this.currentLang.set(lang);
    localStorage.setItem(this.STORAGE_KEY, lang);
    this.translate.use(lang).subscribe(() => {
      window.location.reload();
    });
  }

  private loadLang(): string {
    if (typeof window === 'undefined') return 'fr';
    return localStorage.getItem(this.STORAGE_KEY) || 'fr';
  }
}
