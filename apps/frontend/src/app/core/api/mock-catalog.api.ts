import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';
import { Product } from '../models/product.model';
import { CatalogApi } from './catalog-api';

/**
 * PROTOTYPE ONLY. Serves the same catalog as the Flyway seed (V4__seed_catalog.sql) from
 * memory. Replaced by `HttpCatalogApi` (`GET /api/products`) in Fase 1.
 */
@Injectable()
export class MockCatalogApi extends CatalogApi {
  private readonly seed: readonly Product[] = [
    { id: 1, sku: 'TEC-LAP-014', name: 'Laptop Pro 14"', displayName: 'Laptop Pro 14" (Core i7, 16 GB)',
      description: 'Portátil ultraligero de 14" con 16 GB de RAM y SSD de 512 GB.',
      unitPrice: 1200, category: 'Tecnología', stock: 8, ratingAverage: 4.5, reviewCount: 2 },
    { id: 2, sku: 'TEC-MOU-001', name: 'Mouse inalámbrico', displayName: 'Mouse inalámbrico silencioso',
      description: 'Mouse óptico inalámbrico con clic silencioso y 12 meses de batería.',
      unitPrice: 25, category: 'Tecnología', stock: 40, ratingAverage: 5, reviewCount: 1 },
    { id: 3, sku: 'TEC-KEY-002', name: 'Teclado mecánico', displayName: 'Teclado mecánico RGB',
      description: 'Teclado mecánico con switches marrones y retroiluminación RGB.',
      unitPrice: 80, category: 'Tecnología', stock: 15, ratingAverage: 0, reviewCount: 0 },
    { id: 4, sku: 'TEC-AUR-003', name: 'Auriculares Bluetooth', displayName: 'Auriculares Bluetooth ANC',
      description: 'Over-ear con cancelación activa de ruido y 30 h de autonomía.',
      unitPrice: 60, category: 'Tecnología', stock: 3, ratingAverage: 3.5, reviewCount: 2 },
    { id: 5, sku: 'TEC-MON-027', name: 'Monitor 27 pulgadas', displayName: 'Monitor 27" QHD 144 Hz',
      description: 'Monitor IPS 27" 2560x1440 a 144 Hz.',
      unitPrice: 300, category: 'Tecnología', stock: 5, ratingAverage: 5, reviewCount: 1 },
    { id: 6, sku: 'PAP-CUA-A05', name: 'Cuaderno A5', displayName: 'Cuaderno A5 tapa dura',
      description: 'Cuaderno A5 de 120 hojas, papel de 90 g.',
      unitPrice: 5, category: 'Papelería', stock: 100, ratingAverage: 5, reviewCount: 1 },
    { id: 7, sku: 'PAP-BOL-X03', name: 'Bolígrafos de gel (x3)', displayName: 'Bolígrafos de gel (pack x3)',
      description: 'Pack de 3 bolígrafos de gel, tinta negra, trazo 0.7 mm.',
      unitPrice: 4.5, category: 'Papelería', stock: 2, ratingAverage: 0, reviewCount: 0 },
    { id: 8, sku: 'LIB-NOV-001', name: 'La sombra del viento', displayName: 'La sombra del viento',
      description: 'Novela best-seller, edición de bolsillo.',
      unitPrice: 18, category: 'Libros', stock: 25, ratingAverage: 5, reviewCount: 1 },
    { id: 9, sku: 'HOG-LAM-001', name: 'Lámpara de escritorio LED', displayName: 'Lámpara de escritorio LED',
      description: 'Lámpara LED regulable, 3 temperaturas de color y puerto USB.',
      unitPrice: 35, category: 'Hogar', stock: 20, ratingAverage: 4, reviewCount: 1 },
    { id: 10, sku: 'DEP-BAL-005', name: 'Balón de fútbol talla 5', displayName: 'Balón de fútbol talla 5',
      description: 'Balón cosido a máquina, talla 5, uso recreativo.',
      unitPrice: 22, category: 'Deportes', stock: 12, ratingAverage: 4, reviewCount: 1 },
  ];

  products(): Observable<readonly Product[]> {
    return of(this.seed).pipe(delay(150));
  }
}
