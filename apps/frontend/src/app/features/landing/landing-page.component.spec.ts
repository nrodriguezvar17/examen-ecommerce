import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { LandingPageComponent } from './landing-page.component';

describe('LandingPageComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingPageComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('describes the store and links into the catalog', () => {
    const fixture = TestBed.createComponent(LandingPageComponent);
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;

    expect(el.querySelector('h1')?.textContent).toContain('Davitienda');
    expect(el.textContent).toContain('descuentos acumulativos');
    expect(el.querySelectorAll('.feature')).toHaveLength(4);

    const toStore = Array.from(el.querySelectorAll('a')).filter(
      (a) => a.getAttribute('href') === '/store',
    );
    expect(toStore.length).toBeGreaterThan(0);
  });
});
