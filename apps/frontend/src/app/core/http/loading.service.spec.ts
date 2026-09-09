import { TestBed } from '@angular/core/testing';
import { LoadingService } from './loading.service';

describe('LoadingService', () => {
  let service: LoadingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoadingService);
  });

  it('is not loading initially', () => {
    expect(service.loading()).toBe(false);
  });

  it('stays loading until every in-flight request ends', () => {
    service.begin();
    service.begin();
    expect(service.loading()).toBe(true);

    service.end();
    expect(service.loading()).toBe(true);

    service.end();
    expect(service.loading()).toBe(false);
  });

  it('never goes below zero', () => {
    service.end();
    service.end();
    service.begin();
    expect(service.loading()).toBe(true);
  });
});
