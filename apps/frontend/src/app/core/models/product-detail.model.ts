import { Product } from './product.model';

/** A single product review, as returned by `GET /api/products/{id}`. */
export interface Review {
  readonly id: number;
  readonly rating: number;
  readonly title: string | null;
  readonly comment: string | null;
  readonly reviewerName: string;
  readonly createdAt: string;
}

/** Full product view for the detail page: catalog data (incl. image) + brand + reviews. */
export interface ProductDetail extends Product {
  readonly brand: string | null;
  readonly reviews: readonly Review[];
}
