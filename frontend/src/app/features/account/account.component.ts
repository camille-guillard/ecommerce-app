import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-account',
  imports: [FormsModule, TranslateModule],
  templateUrl: './account.component.html',
  styleUrl: '../login/login.component.css',
})
export class AccountComponent implements OnInit {
  email = '';
  firstName = '';
  lastName = '';
  street = '';
  city = '';
  postalCode = '';
  loading = signal(false);

  constructor(
    protected authService: AuthService,
    private toastService: ToastService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUser();
    if (user) {
      this.email = user.email;
      this.firstName = user.firstName || '';
      this.lastName = user.lastName || '';
      this.street = user.street || '';
      this.city = user.city || '';
      this.postalCode = user.postalCode || '';
    }
  }

  save(): void {
    this.loading.set(true);
    this.authService
      .updateProfile({
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
          this.toastService.show(this.translate.instant('auth.profileUpdated'));
        },
        error: () => {
          this.loading.set(false);
        },
      });
  }
}
