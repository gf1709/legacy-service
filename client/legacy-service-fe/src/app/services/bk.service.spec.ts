import { TestBed } from '@angular/core/testing';

import { BkService } from './bk.service';

describe('BkService', () => {
  let service: BkService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BkService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
