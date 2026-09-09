import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CheckoutFacade } from '../../core/state/checkout.facade';
import { IconComponent } from '../../shared/icon/icon.component';

/**
 * HU4 — persistent, distinctive notification shown while the 35% savings cap is reached.
 * The message text is fixed by the assignment and must not change.
 */
@Component({
  selector: 'app-savings-limit-alert',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [IconComponent],
  templateUrl: './savings-limit-alert.component.html',
  styleUrl: './savings-limit-alert.component.scss',
})
export class SavingsLimitAlertComponent {
  protected readonly capReached = inject(CheckoutFacade).capReached;
}
