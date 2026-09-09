import { TestBed } from '@angular/core/testing';
import { SiteFooterComponent } from './site-footer.component';

describe('SiteFooterComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [SiteFooterComponent] }).compileComponents();
  });

  it('shows the brand, the exercise context and the current year', () => {
    const fixture = TestBed.createComponent(SiteFooterComponent);
    fixture.detectChanges();

    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('Davitienda');
    expect(text).toContain('Grupo Bolívar');
    expect(text).toContain(String(new Date().getFullYear()));
  });
});
