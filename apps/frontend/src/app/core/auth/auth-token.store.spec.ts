import { TestBed } from '@angular/core/testing';
import { AuthTokenStore } from './auth-token.store';

describe('AuthTokenStore', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
  });

  it('starts without a token', () => {
    expect(TestBed.inject(AuthTokenStore).token()).toBeNull();
  });

  it('stores and clears the token, persisting to localStorage', () => {
    const store = TestBed.inject(AuthTokenStore);

    store.setToken('abc.123');
    expect(store.token()).toBe('abc.123');
    expect(localStorage.getItem('davitienda.token')).toBe('abc.123');

    store.clear();
    expect(store.token()).toBeNull();
    expect(localStorage.getItem('davitienda.token')).toBeNull();
  });

  it('reads an existing token from localStorage on creation', () => {
    localStorage.setItem('davitienda.token', 'persisted');
    expect(TestBed.inject(AuthTokenStore).token()).toBe('persisted');
  });
});
