import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DsplogManager } from './dsplog-manager';

describe('DsplogManager', () => {
  let component: DsplogManager;
  let fixture: ComponentFixture<DsplogManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DsplogManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DsplogManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
