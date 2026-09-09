import { TestBed } from '@angular/core/testing';
import { StarRatingComponent } from './star-rating.component';

describe('StarRatingComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [StarRatingComponent] }).compileComponents();
  });

  it('fills the rounded number of stars and shows the count', () => {
    const fixture = TestBed.createComponent(StarRatingComponent);
    fixture.componentRef.setInput('average', 3.5);
    fixture.componentRef.setInput('count', 2);
    fixture.detectChanges();

    const filled = (fixture.nativeElement as HTMLElement).querySelectorAll('.filled');
    expect(filled).toHaveLength(4);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('(2)');
  });

  it('shows "Sin reseñas" when there are none', () => {
    const fixture = TestBed.createComponent(StarRatingComponent);
    fixture.componentRef.setInput('average', 0);
    fixture.componentRef.setInput('count', 0);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Sin reseñas');
  });
});
