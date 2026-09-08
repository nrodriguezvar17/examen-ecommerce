import { MoneyPipe } from './money.pipe';

describe('MoneyPipe', () => {
  const pipe = new MoneyPipe();

  it('formats a number as USD currency', () => {
    expect(pipe.transform(633.89)).toBe('$633.89');
    expect(pipe.transform(1200)).toBe('$1,200.00');
  });

  it('treats null and undefined as zero', () => {
    expect(pipe.transform(null)).toBe('$0.00');
    expect(pipe.transform(undefined)).toBe('$0.00');
  });
});
