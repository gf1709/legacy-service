import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IfsManager } from './ifs-manager';

describe('IfsManager', () => {
  let component: IfsManager;
  let fixture: ComponentFixture<IfsManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IfsManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IfsManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
