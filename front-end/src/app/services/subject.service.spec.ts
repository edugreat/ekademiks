import { TestBed } from '@angular/core/testing';

import { MultiService } from './multi.service';

describe('SubjectService', () => {
  let service: MultiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MultiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
