import { Observable } from 'rxjs';
import { Product } from '../models/product.model';

/**
 * Port for the product catalog. Implementations: `HttpCatalogApi` (Fase 1, calls
 * `GET /api/products`) and `MockCatalogApi` (prototype, in-memory seed data).
 */
export abstract class CatalogApi {
  abstract products(): Observable<readonly Product[]>;
}
