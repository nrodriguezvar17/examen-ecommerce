import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { LoadingService } from '../../core/http/loading.service';

/** Indeterminate top progress bar, visible while any HTTP request is in flight. */
@Component({
  selector: 'app-global-loader',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './global-loader.component.html',
  styleUrl: './global-loader.component.scss',
})
export class GlobalLoaderComponent {
  protected readonly loading = inject(LoadingService).loading;
}
