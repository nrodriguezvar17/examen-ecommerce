import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Order } from '../../core/models/order.model';
import { OrdersService } from '../../core/services/orders.service';
import { OrderCardComponent } from './order-card.component';

/** Route `/compras` — the list of confirmed orders with their line-by-line breakdown. */
@Component({
  selector: 'app-orders-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [OrderCardComponent, RouterLink],
  templateUrl: './orders-page.component.html',
  styleUrl: './orders-page.component.scss',
})
export class OrdersPageComponent {
  private readonly orders = toSignal<readonly Order[] | null>(inject(OrdersService).list(), {
    initialValue: null,
  });

  protected readonly loading = computed((): boolean => this.orders() === null);
  protected readonly list = computed((): readonly Order[] => this.orders() ?? []);
  protected readonly isEmpty = computed(
    (): boolean => !this.loading() && this.list().length === 0,
  );
}
