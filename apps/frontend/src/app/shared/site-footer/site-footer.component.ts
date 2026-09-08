import { ChangeDetectionStrategy, Component } from '@angular/core';

/** App footer: brand mark, context of the exercise and the current year. */
@Component({
  selector: 'app-site-footer',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './site-footer.component.html',
  styleUrl: './site-footer.component.scss',
})
export class SiteFooterComponent {
  protected readonly year = new Date().getFullYear();
}
