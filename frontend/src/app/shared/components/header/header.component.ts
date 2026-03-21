import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { CartService } from '../../../core/services/cart.service';
import { ThemeService } from '../../../core/services/theme.service';
import { AuthService } from '../../../core/services/auth.service';
import { LanguageService } from '../../../core/services/language.service';
import { WishlistService } from '../../../core/services/wishlist.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, TranslateModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent {
  constructor(
    protected cartService: CartService,
    protected themeService: ThemeService,
    protected authService: AuthService,
    protected langService: LanguageService,
    private wishlistService: WishlistService,
    private router: Router
  ) {}

  menuOpen = signal(false);

  toggleMenu(): void {
    this.menuOpen.set(!this.menuOpen());
  }

  closeMenu(): void {
    this.menuOpen.set(false);
  }

  toggleTheme(): void {
    this.themeService.toggle();
  }

  logout(): void {
    this.authService.logout();
    this.cartService.setLoggedIn(false);
    this.cartService.clearOnLogout();
    this.wishlistService.clear();
    this.router.navigate(['/']);
  }

  setLang(lang: string): void {
    this.langService.setLanguage(lang);
  }
}
