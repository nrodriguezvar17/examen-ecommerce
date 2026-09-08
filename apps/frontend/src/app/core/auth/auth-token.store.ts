import { Injectable, signal } from '@angular/core';

const STORAGE_KEY = 'davitienda.token';

/**
 * Holds the authentication token. The API interceptor reads it to add the
 * `Authorization: Bearer` header. Persisted in `localStorage` so it survives reloads.
 */
@Injectable({ providedIn: 'root' })
export class AuthTokenStore {
  private readonly _token = signal<string | null>(readStored());

  readonly token = this._token.asReadonly();

  setToken(token: string): void {
    this._token.set(token);
    write(token);
  }

  clear(): void {
    this._token.set(null);
    write(null);
  }
}

function readStored(): string | null {
  try {
    return localStorage.getItem(STORAGE_KEY);
  } catch {
    return null; // storage unavailable (private mode, disabled cookies)
  }
}

function write(token: string | null): void {
  try {
    if (token) {
      localStorage.setItem(STORAGE_KEY, token);
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  } catch {
    // storage unavailable — the in-memory signal still works for this session
  }
}
