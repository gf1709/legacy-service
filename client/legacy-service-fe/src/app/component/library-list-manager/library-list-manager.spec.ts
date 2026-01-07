import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibraryListManager } from './library-list-manager';

describe('LibraryListManager', () => {
  let component: LibraryListManager;
  let fixture: ComponentFixture<LibraryListManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LibraryListManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LibraryListManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
