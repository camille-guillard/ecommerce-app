import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../core/models/order.model';
import { StarRatingComponent } from '../../shared/components/star-rating/star-rating.component';

@Component({
  selector: 'app-orders',
  imports: [DecimalPipe, DatePipe, RouterLink, TranslateModule, StarRatingComponent],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css',
})
export class OrdersComponent implements OnInit {
  orders = signal<Order[]>([]);
  loading = signal(true);

  constructor(private orderService: OrderService, private translate: TranslateService) {}

  getStatusLabel(status: string): string {
    return this.translate.instant('orders.status.' + status);
  }

  ngOnInit(): void {
    this.orderService.getOrders().subscribe((orders) => {
      this.orders.set(orders);
      this.loading.set(false);
    });
  }
}
