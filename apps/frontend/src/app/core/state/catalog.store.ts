import { Injectable, computed, inject, signal } from '@angular/core';
import { Product } from '../models/product.model';
import { CatalogService } from '../services/catalog.service';

/**
 * Catalog state. <b>Observer</b> pattern (signals). Owns the product list so it can be
 * reloaded after a purchase — stock changes on the server (HU3).
 */
@Injectable({ providedIn: 'root' })
export class CatalogStore {
  private readonly service = inject(CatalogService);

  private readonly _products = signal<readonly Product[]>([]);
  private readonly _loaded = signal(false);

  readonly products = this._products.asReadonly();
  readonly loading = computed((): boolean => !this._loaded());

  /** Fetches the catalog and replaces the current list. Safe to call repeatedly. */
  reload(): void {
    this.service.products().subscribe((products) => {
      this._products.set(products);
      this._loaded.set(true);
    });
  }
}
