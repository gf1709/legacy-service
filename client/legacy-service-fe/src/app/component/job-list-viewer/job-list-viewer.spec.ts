import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobListViewer } from './job-list-viewer';

describe('JobListViewer', () => {
  let component: JobListViewer;
  let fixture: ComponentFixture<JobListViewer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobListViewer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobListViewer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
