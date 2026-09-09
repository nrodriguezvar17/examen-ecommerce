import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingService } from '../../core/http/loading.service';
import { GlobalLoaderComponent } from './global-loader.component';

describe('GlobalLoaderComponent', () => {
  const loading = signal(false);
  let fixture: ComponentFixture<GlobalLoaderComponent>;

  const overlay = (): Element | null =>
    (fixture.nativeElement as HTMLElement).querySelector('[role="status"]');

  beforeEach(async () => {
    vi.useFakeTimers();
    loading.set(false);
    await TestBed.configureTestingModule({
      imports: [GlobalLoaderComponent],
      providers: [{ provide: LoadingService, useValue: { loading } }],
    }).compileComponents();
    fixture = TestBed.createComponent(GlobalLoaderComponent);
    fixture.detectChanges();
  });

  afterEach(() => vi.useRealTimers());

  it('is hidden when nothing is loading', () => {
    expect(overlay()).toBeNull();
  });

  it('shows the overlay while loading', () => {
    loading.set(true);
    fixture.detectChanges();
    expect(overlay()).not.toBeNull();
  });

  it('keeps the overlay for a minimum time after loading stops', () => {
    loading.set(true);
    fixture.detectChanges();

    loading.set(false);
    fixture.detectChanges();
    expect(overlay()).not.toBeNull();

    vi.advanceTimersByTime(500);
    fixture.detectChanges();
    expect(overlay()).toBeNull();
  });
});
