import { SubjectDate } from './subject-date';

describe('CategoryDate', () => {
  it('should create an instance', () => {
    expect(new SubjectDate(new Date)).toBeTruthy();
  });
});
