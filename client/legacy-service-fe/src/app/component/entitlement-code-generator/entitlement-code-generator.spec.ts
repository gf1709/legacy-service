import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntitlementCodeGenerator } from './entitlement-code-generator';

describe('EntitlementCodeGenerator', () => {
  let component: EntitlementCodeGenerator;
  let fixture: ComponentFixture<EntitlementCodeGenerator>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntitlementCodeGenerator]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntitlementCodeGenerator);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
