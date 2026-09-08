import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { LoadingService } from '../../core/http/loading.service';
import { GlobalLoaderComponent } from './global-loader.component';

describe('GlobalLoaderComponent', () => {
  const loading = signal(false);

  beforeEach(async () => {
    loading.set(false);
    await TestBed.configureTestingModule({
      imports: [GlobalLoaderComponent],
      providers: [{ provide: LoadingService, useValue: { loading } }],
    }).compileComponents();
  });

  it('is hidden when nothing is loading', () => {
    const fixture = TestBed.createComponent(GlobalLoaderComponent);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).querySelector('[role="progressbar"]')).toBeNull();
  });

  it('shows the progress bar while loading', () => {
    loading.set(true);
    const fixture = TestBed.createComponent(GlobalLoaderComponent);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).querySelector('[role="progressbar"]')).not.toBeNull();
  });
});
