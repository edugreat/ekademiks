import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubjectNameComponent } from './subject-name.component';

describe('SubjectNameComponent', () => {
  let component: SubjectNameComponent;
  let fixture: ComponentFixture<SubjectNameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubjectNameComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubjectNameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
