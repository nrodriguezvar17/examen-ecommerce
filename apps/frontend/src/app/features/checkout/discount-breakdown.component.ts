import { PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { DiscountType } from '../../core/models/discount-breakdown.model';
import { CheckoutFacade } from '../../core/state/checkout.facade';
import { DISCOUNT_LABELS } from '../../shared/discount-labels';
import { MoneyPipe } from '../../shared/money.pipe';

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
    return DISCOUNT_LABELS[type];
  }
}
