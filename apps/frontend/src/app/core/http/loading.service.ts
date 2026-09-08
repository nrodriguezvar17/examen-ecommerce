import { Injectable, computed, signal } from '@angular/core';

/**
 * Tracks in-flight HTTP requests. The API interceptor calls {@link begin}/{@link end};
 * the global loader reads {@link loading}.
 */
@Injectable({ providedIn: 'root' })
export class LoadingService {
  private readonly inFlight = signal(0);

  readonly loading = computed((): boolean => this.inFlight() > 0);

  begin(): void {
    this.inFlight.update((count) => count + 1);
  }

  end(): void {
    this.inFlight.update((count) => Math.max(0, count - 1));
  }
}
