import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface Feature {
  readonly icon: string;
  readonly title: string;
  readonly text: string;
}

/** Route `/` — landing that describes the store before entering the catalog. */
@Component({
  selector: 'app-landing-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.scss',
})
export class LandingPageComponent {
  protected readonly features: readonly Feature[] = [
    {
      icon: '🛒',
      title: 'Carrito en vivo',
      text: 'Agrega productos y ve el subtotal actualizarse al instante, sin recargar.',
    },
    {
      icon: '🎯',
      title: 'Descuentos acumulativos',
      text: 'Categoría, volumen y cupón se aplican en cascada, con el desglose siempre a la vista.',
    },
    {
      icon: '🛡️',
      title: 'Tope de ahorro del 35 %',
      text: 'El descuento consolidado nunca supera el 35 %: la regla es explícita y se te avisa.',
    },
    {
      icon: '📦',
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
