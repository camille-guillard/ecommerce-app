import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/components/header/header.component';
import { FloatingCartComponent } from './shared/components/floating-cart/floating-cart.component';
import { ToastComponent } from './shared/components/toast/toast.component';
import { ThemeService } from './core/services/theme.service';
import { AuthService } from './core/services/auth.service';
import { CartService } from './core/services/cart.service';
import { LanguageService } from './core/services/language.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, FloatingCartComponent, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class AppComponent implements OnInit {
  constructor(
    private themeService: ThemeService,
    private authService: AuthService,
    private cartService: CartService,
    private langService: LanguageService
  ) {}

  ngOnInit(): void {
    this.themeService.init();
    if (this.authService.isLoggedIn()) {
      this.cartService.setLoggedIn(true);
    }
  }
}
