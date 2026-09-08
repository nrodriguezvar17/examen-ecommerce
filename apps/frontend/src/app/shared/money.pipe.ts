import { Pipe, PipeTransform } from '@angular/core';

/** Formats a number as USD currency (the assignment uses "$ USD"). */
@Pipe({ name: 'money' })
export class MoneyPipe implements PipeTransform {
  private readonly formatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  });

  transform(value: number | null | undefined): string {
    return this.formatter.format(value ?? 0);
  }
}
