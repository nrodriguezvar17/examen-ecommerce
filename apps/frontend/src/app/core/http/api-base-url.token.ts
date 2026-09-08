import { InjectionToken } from '@angular/core';

/**
 * Base URL prepended to relative `/api` requests by the API interceptor.
 * Empty string = same origin (the dev server proxies `/api` to the backend).
 */
export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', {
  providedIn: 'root',
  factory: (): string => '',
});
