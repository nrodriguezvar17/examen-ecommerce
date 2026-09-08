import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

@Component({
  selector: 'app-star-rating',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './star-rating.component.html',
  styleUrl: './star-rating.component.scss',
})
export class StarRatingComponent {
  readonly average = input.required<number>();
  readonly count = input.required<number>();

  protected readonly stars = [1, 2, 3, 4, 5];
  protected readonly rounded = computed((): number => Math.round(this.average()));
}
