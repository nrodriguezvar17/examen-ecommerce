import { ComponentFixture, TestBed } from '@angular/core/testing';
import { IconComponent } from './icon.component';

describe('IconComponent', () => {
  let fixture: ComponentFixture<IconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [IconComponent] }).compileComponents();
    fixture = TestBed.createComponent(IconComponent);
  });

  it('renders an svg for the given name and is decorative by default', () => {
    fixture.componentRef.setInput('name', 'cart');
    fixture.detectChanges();
    const svg = (fixture.nativeElement as HTMLElement).querySelector('svg')!;

    expect(svg).not.toBeNull();
    expect(svg.querySelectorAll('circle, path, line, polyline, polygon').length).toBeGreaterThan(0);
    expect(svg.getAttribute('aria-hidden')).toBe('true');
  });

  it('exposes a label to assistive tech when provided', () => {
    fixture.componentRef.setInput('name', 'check');
    fixture.componentRef.setInput('label', 'Listo');
    fixture.detectChanges();
    const svg = (fixture.nativeElement as HTMLElement).querySelector('svg')!;

    expect(svg.getAttribute('role')).toBe('img');
    expect(svg.getAttribute('aria-label')).toBe('Listo');
    expect(svg.getAttribute('aria-hidden')).toBeNull();
  });
});
