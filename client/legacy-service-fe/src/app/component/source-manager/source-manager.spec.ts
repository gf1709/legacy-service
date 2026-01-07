import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SourceManager } from './source-manager';

describe('SourceManager', () => {
  let component: SourceManager;
  let fixture: ComponentFixture<SourceManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SourceManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SourceManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
