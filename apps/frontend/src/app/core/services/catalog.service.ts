import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { ProductDetail } from '../models/product-detail.model';

/** Data-access service for the product catalog (HU1) and the product detail screen. */
@Injectable({ providedIn: 'root' })
export class CatalogService {
  private readonly http = inject(HttpClient);

  products(): Observable<readonly Product[]> {
    return this.http.get<readonly Product[]>('/api/products');
  }

  detail(id: number): Observable<ProductDetail> {
    return this.http.get<ProductDetail>(`/api/products/${id}`);
  }
}
