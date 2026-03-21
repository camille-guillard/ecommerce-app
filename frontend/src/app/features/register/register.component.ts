import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink, TranslateModule],
  templateUrl: './register.component.html',
  styleUrl: '../login/login.component.css',
})
export class RegisterComponent {
  username = '';
  password = '';
  email = '';
  firstName = '';
  lastName = '';
  street = '';
  city = '';
  postalCode = '';
  error = signal('');
  loading = signal(false);

  constructor(private authService: AuthService, private router: Router, private translate: TranslateService) {}

  submit(): void {
    this.error.set('');
    this.loading.set(true);

    this.authService
      .register({
        username: this.username,
        password: this.password,
        email: this.email,
        firstName: this.firstName,
        lastName: this.lastName,
        street: this.street,
        city: this.city,
        postalCode: this.postalCode,
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.router.navigate(['/']);
        },
        error: (err) => {
          this.loading.set(false);
          this.error.set(err.error || this.translate.instant('auth.registerError'));
        },
      });
  }
}
