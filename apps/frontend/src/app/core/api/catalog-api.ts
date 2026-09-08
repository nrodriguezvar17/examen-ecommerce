import { Observable } from 'rxjs';
import { Product } from '../models/product.model';

/** Port for the product catalog. Implementation: {@link HttpCatalogApi} (`GET /api/products`). */
export abstract class CatalogApi {
  abstract products(): Observable<readonly Product[]>;
}
