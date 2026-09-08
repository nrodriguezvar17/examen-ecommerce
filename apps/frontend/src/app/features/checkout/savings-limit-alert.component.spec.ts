import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { CheckoutFacade } from '../../core/state/checkout.facade';
import { SavingsLimitAlertComponent } from './savings-limit-alert.component';

describe('SavingsLimitAlertComponent (HU4)', () => {
  const capReached = signal(false);

  beforeEach(async () => {
    capReached.set(false);
    await TestBed.configureTestingModule({
      imports: [SavingsLimitAlertComponent],
      providers: [{ provide: CheckoutFacade, useValue: { capReached } }],
    }).compileComponents();
  });

  it('is hidden while the cap is not reached', () => {
    const fixture = TestBed.createComponent(SavingsLimitAlertComponent);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).querySelector('[role="alert"]')).toBeNull();
  });

  it('shows the exact message with role="alert" when the cap is reached', () => {
    capReached.set(true);
    const fixture = TestBed.createComponent(SavingsLimitAlertComponent);
    fixture.detectChanges();

    const alert = (fixture.nativeElement as HTMLElement).querySelector('[role="alert"]');
    expect(alert).not.toBeNull();
    expect(alert?.textContent?.trim()).toContain(
      '¡Enhorabuena! Has alcanzado el límite máximo de ahorro permitido (35%)',
    );
  });
});
