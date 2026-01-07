import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobManager } from './job-manager';

describe('JobManager', () => {
  let component: JobManager;
  let fixture: ComponentFixture<JobManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
