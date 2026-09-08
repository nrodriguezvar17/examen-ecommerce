import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { CatalogApi } from './catalog-api';

/** Real implementation of {@link CatalogApi}: calls `GET /api/products` on the backend. */
@Injectable()
export class HttpCatalogApi extends CatalogApi {
  private readonly http = inject(HttpClient);

  products(): Observable<readonly Product[]> {
    return this.http.get<readonly Product[]>('/api/products');
  }
}
