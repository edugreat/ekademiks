import { TestBed } from '@angular/core/testing';

import { StudentAttemptService } from './student-attempt.service';

describe('StudentAttemptService', () => {
  let service: StudentAttemptService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StudentAttemptService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
