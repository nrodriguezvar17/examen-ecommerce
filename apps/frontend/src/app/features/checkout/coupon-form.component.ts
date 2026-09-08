import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CheckoutFacade } from '../../core/state/checkout.facade';

@Component({
  selector: 'app-coupon-form',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule],
  templateUrl: './coupon-form.component.html',
  styleUrl: './coupon-form.component.scss',
})
export class CouponFormComponent {
  protected readonly facade = inject(CheckoutFacade);
  protected code = '';

  apply(): void {
    this.facade.applyCoupon(this.code);
  }

  clear(): void {
    this.code = '';
    this.facade.clearCoupon();
  }
}
