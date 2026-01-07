import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpoolManager } from './spool-manager';

describe('SpoolManager', () => {
  let component: SpoolManager;
  let fixture: ComponentFixture<SpoolManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpoolManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SpoolManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
