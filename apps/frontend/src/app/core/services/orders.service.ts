import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';

/** Data-access service for confirmed orders ("Mis compras"). */
@Injectable({ providedIn: 'root' })
export class OrdersService {
  private readonly http = inject(HttpClient);

  /** `GET /api/orders` — the most recent orders, newest first. */
  list(): Observable<readonly Order[]> {
    return this.http.get<readonly Order[]>('/api/orders');
  }

  /** `GET /api/orders/{radicado}` — full detail of one order. */
  get(radicado: string): Observable<Order> {
    return this.http.get<Order>(`/api/orders/${radicado}`);
  }
}
