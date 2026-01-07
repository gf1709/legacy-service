import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiziSibankManager } from './servizi-sibank-manager';

describe('ServiziSibankManager', () => {
  let component: ServiziSibankManager;
  let fixture: ComponentFixture<ServiziSibankManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServiziSibankManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServiziSibankManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
