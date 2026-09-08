import { provideHttpClient } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { CatalogApi } from './core/api/catalog-api';
import { CheckoutApi } from './core/api/checkout-api';
import { HttpCatalogApi } from './core/api/http-catalog.api';
import { HttpCheckoutApi } from './core/api/http-checkout.api';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(),
    { provide: CatalogApi, useClass: HttpCatalogApi },
    { provide: CheckoutApi, useClass: HttpCheckoutApi },
  ],
};
