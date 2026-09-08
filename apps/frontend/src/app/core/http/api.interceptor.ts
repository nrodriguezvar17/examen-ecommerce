import { HttpContextToken, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';
import { AuthTokenStore } from '../auth/auth-token.store';
import { API_BASE_URL } from './api-base-url.token';
import { LoadingService } from './loading.service';

/** Set to `true` on a request's `HttpContext` to keep it out of the global loader. */
export const SKIP_LOADER = new HttpContextToken<boolean>(() => false);

/**
 * The single HTTP interceptor of the app. For every request it:
 *  1. rewrites a relative `/api/...` URL to `${API_BASE_URL}/api/...`,
 *  2. attaches `Authorization: Bearer <token>` when a token is present,
 *  3. drives the global loader (unless `SKIP_LOADER` is set).
 */
export const apiInterceptor: HttpInterceptorFn = (request, next) => {
  const baseUrl = inject(API_BASE_URL);
  const token = inject(AuthTokenStore).token();
  const loading = inject(LoadingService);

  const url =
    baseUrl && request.url.startsWith('/') && !request.url.startsWith(baseUrl)
      ? `${baseUrl}${request.url}`
      : request.url;

  const outgoing = request.clone({
    url,
    setHeaders: token ? { Authorization: `Bearer ${token}` } : {},
  });

  const tracked = !request.context.get(SKIP_LOADER);
  if (tracked) {
    loading.begin();
  }

  return next(outgoing).pipe(
    finalize(() => {
      if (tracked) {
        loading.end();
      }
    }),
  );
};
