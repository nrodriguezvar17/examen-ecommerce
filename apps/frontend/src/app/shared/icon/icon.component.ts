import { ChangeDetectionStrategy, Component, input } from '@angular/core';

export type IconName =
  | 'cart'
  | 'target'
  | 'shield'
  | 'box'
  | 'menu'
  | 'close'
  | 'chevron'
  | 'plus'
  | 'minus'
  | 'trash'
  | 'check'
  | 'star'
  | 'award';

/**
 * Inline SVG icon (Feather-style, 24×24, `currentColor`). Replaces emoji so the icons
 * inherit color and scale with font-size. Decorative by default; pass `label` to expose it.
 */
@Component({
  selector: 'app-icon',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './icon.component.html',
  styleUrl: './icon.component.scss',
})
export class IconComponent {
  readonly name = input.required<IconName>();
  readonly label = input<string | null>(null);
}
