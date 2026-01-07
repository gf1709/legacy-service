import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZztrutManager } from './zztrut-manager';

describe('ZztrutManager', () => {
  let component: ZztrutManager;
  let fixture: ComponentFixture<ZztrutManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZztrutManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZztrutManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
