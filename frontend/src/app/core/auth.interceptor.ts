import { HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.includes('/i18n/')) {
    return next(req);
  }

  const authService = inject(AuthService);
  const platformId = inject(PLATFORM_ID);
  const token = authService.token();

  let lang = 'fr';
  if (isPlatformBrowser(platformId)) {
    lang = localStorage.getItem('ecommerce-lang') || 'fr';
  }

  const headers: Record<string, string> = {
    'Accept-Language': lang,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  req = req.clone({ setHeaders: headers });

  return next(req);
};
