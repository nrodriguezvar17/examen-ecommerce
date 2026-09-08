import { PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { DiscountType } from '../../core/models/discount-breakdown.model';
import { CheckoutFacade } from '../../core/state/checkout.facade';
import { MoneyPipe } from '../../shared/money.pipe';

const LABELS: Readonly<Record<DiscountType, string>> = {
  CATEGORY: 'Descuento de categoría',
  VOLUME: 'Descuento por volumen',
  COUPON: 'Descuento por cupón',
};

@Component({
  selector: 'app-discount-breakdown',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MoneyPipe, PercentPipe],
  templateUrl: './discount-breakdown.component.html',
  styleUrl: './discount-breakdown.component.scss',
})
export class DiscountBreakdownComponent {
  protected readonly breakdown = inject(CheckoutFacade).breakdown;

  protected label(type: DiscountType): string {
    return LABELS[type];
  }
}
