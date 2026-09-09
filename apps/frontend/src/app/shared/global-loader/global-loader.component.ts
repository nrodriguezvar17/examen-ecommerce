import { ChangeDetectionStrategy, Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { LoadingService } from '../../core/http/loading.service';

/** Keep the overlay on screen at least this long so fast requests are still perceptible. */
const MIN_VISIBLE_MS = 450;

/** Full-screen dimmed overlay with a spinner, shown while any HTTP request is in flight. */
@Component({
  selector: 'app-global-loader',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './global-loader.component.html',
  styleUrl: './global-loader.component.scss',
})
export class GlobalLoaderComponent {
  private readonly loading = inject(LoadingService).loading;

  protected readonly visible = signal(false);

  private shownAt = 0;
  private hideTimer: ReturnType<typeof setTimeout> | null = null;

  constructor() {
    inject(DestroyRef).onDestroy(() => this.clearHideTimer());

    effect(() => {
      if (this.loading()) {
        this.clearHideTimer();
        if (!this.visible()) {
          this.visible.set(true);
          this.shownAt = Date.now();
        }
      } else if (this.visible() && this.hideTimer === null) {
        const remaining = Math.max(0, MIN_VISIBLE_MS - (Date.now() - this.shownAt));
        this.hideTimer = setTimeout(() => {
          this.visible.set(false);
          this.hideTimer = null;
        }, remaining);
      }
    });
  }

  private clearHideTimer(): void {
    if (this.hideTimer !== null) {
      clearTimeout(this.hideTimer);
      this.hideTimer = null;
    }
  }
}
