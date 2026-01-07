import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SqlScriptManager } from './sql-script-manager';

describe('SqlScriptManager', () => {
  let component: SqlScriptManager;
  let fixture: ComponentFixture<SqlScriptManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SqlScriptManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SqlScriptManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
