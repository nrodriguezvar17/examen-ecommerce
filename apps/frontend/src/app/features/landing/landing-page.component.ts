import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IconComponent, IconName } from '../../shared/icon/icon.component';

interface Feature {
  readonly icon: IconName;
  readonly title: string;
  readonly text: string;
}

/** Route `/` — landing that describes the store before entering the catalog. */
@Component({
  selector: 'app-landing-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, IconComponent],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.scss',
})
export class LandingPageComponent {
  protected readonly features: readonly Feature[] = [
    {
      icon: 'cart',
      title: 'Carrito en vivo',
      text: 'Agrega productos y ve el subtotal actualizarse al instante, sin recargar.',
    },
    {
      icon: 'target',
      title: 'Descuentos acumulativos',
      text: 'Categoría, volumen y cupón se aplican en cascada, con el desglose siempre a la vista.',
    },
    {
      icon: 'shield',
      title: 'Tope de ahorro del 35 %',
      text: 'El descuento consolidado nunca supera el 35 %: la regla es explícita y se te avisa.',
    },
    {
      icon: 'box',
      title: 'Checkout consistente',
      text: 'El backend valida stock, recalcula el total y guarda tu orden con un radicado único.',
    },
  ];

  protected readonly steps: readonly string[] = [
    'Explora el catálogo y arma tu carrito.',
    'Aplica un cupón y revisa el desglose del ahorro.',
    'Confirma la compra y haz seguimiento en “Mis compras”.',
  ];
}
