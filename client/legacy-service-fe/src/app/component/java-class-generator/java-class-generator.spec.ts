import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JavaClassGenerator } from './java-class-generator';

describe('JavaClassGenerator', () => {
  let component: JavaClassGenerator;
  let fixture: ComponentFixture<JavaClassGenerator>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JavaClassGenerator]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JavaClassGenerator);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
