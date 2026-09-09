import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CartStore } from '../../core/state/cart.store';
import { CheckoutFacade } from '../../core/state/checkout.facade';
import { MoneyPipe } from '../../shared/money.pipe';
import { CouponFormComponent } from '../checkout/coupon-form.component';
import { DiscountBreakdownComponent } from '../checkout/discount-breakdown.component';
import { SavingsLimitAlertComponent } from '../checkout/savings-limit-alert.component';

@Component({
  selector: 'app-cart-panel',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MoneyPipe, CouponFormComponent, DiscountBreakdownComponent, SavingsLimitAlertComponent],
  templateUrl: './cart-panel.component.html',
  styleUrl: './cart-panel.component.scss',
})
export class CartPanelComponent {
  protected readonly cart = inject(CartStore);
  protected readonly checkout = inject(CheckoutFacade);

  confirm(): void {
    this.checkout.confirm();
  }
}
