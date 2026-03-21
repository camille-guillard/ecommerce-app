import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { WishlistService } from '../../core/services/wishlist.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink, TranslateModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  username = 'admin';
  password = 'admin';
  error = signal('');
  loading = signal(false);

  private redirectUrl: string;

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private wishlistService: WishlistService,
    private router: Router,
    private route: ActivatedRoute,
    private translate: TranslateService
  ) {
    this.redirectUrl = this.route.snapshot.queryParamMap.get('redirect') || '/';
  }

  submit(): void {
    this.error.set('');
    this.loading.set(true);

    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.loading.set(false);
        this.cartService.setLoggedIn(true);
        this.cartService.mergeOnLogin();
        this.wishlistService.load();
        this.router.navigateByUrl(this.redirectUrl);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(typeof err.error === 'string' ? err.error : this.translate.instant('auth.loginError'));
      },
    });
  }
}
