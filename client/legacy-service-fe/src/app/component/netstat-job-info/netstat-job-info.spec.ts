import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NetstatJobInfo } from './netstat-job-info';

describe('NetstatJobInfo', () => {
  let component: NetstatJobInfo;
  let fixture: ComponentFixture<NetstatJobInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NetstatJobInfo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NetstatJobInfo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
