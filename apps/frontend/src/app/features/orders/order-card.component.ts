import { DatePipe, PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input, signal } from '@angular/core';
import { DiscountType } from '../../core/models/discount-breakdown.model';
import { Order } from '../../core/models/order.model';
import { DISCOUNT_LABELS } from '../../shared/discount-labels';
import { MoneyPipe } from '../../shared/money.pipe';

/** One order in "Mis compras": a summary row that expands to the checkout-style breakdown. */
@Component({
  selector: 'app-order-card',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MoneyPipe, DatePipe, PercentPipe],
  templateUrl: './order-card.component.html',
  styleUrl: './order-card.component.scss',
})
export class OrderCardComponent {
  readonly order = input.required<Order>();

  protected readonly open = signal(false);
  protected readonly itemCount = computed((): number =>
    this.order().lines.reduce((total, line) => total + line.quantity, 0),
  );

  protected toggle(): void {
    this.open.update((value) => !value);
  }

  protected label(type: DiscountType): string {
    return DISCOUNT_LABELS[type];
  }
}
