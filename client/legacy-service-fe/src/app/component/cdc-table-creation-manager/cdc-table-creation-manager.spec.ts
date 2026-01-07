import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CdcTableCreationManager } from './cdc-table-creation-manager';

describe('CdcTableCreationManager', () => {
  let component: CdcTableCreationManager;
  let fixture: ComponentFixture<CdcTableCreationManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CdcTableCreationManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CdcTableCreationManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
