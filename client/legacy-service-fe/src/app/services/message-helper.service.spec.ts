import { TestBed } from '@angular/core/testing';

import { MessageHelperService } from './message-helper.service';

describe('MessageHelperService', () => {
  let service: MessageHelperService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessageHelperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
