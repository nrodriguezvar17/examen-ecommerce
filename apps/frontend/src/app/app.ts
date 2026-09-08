import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CartPanelComponent } from './features/cart/cart-panel.component';
import { ProductListComponent } from './features/catalog/product-list.component';

@Component({
  selector: 'app-root',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ProductListComponent, CartPanelComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
