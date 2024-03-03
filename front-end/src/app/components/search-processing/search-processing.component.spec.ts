import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchProcessingComponent } from './search-processing.component';

describe('SearchProcessingComponent', () => {
  let component: SearchProcessingComponent;
  let fixture: ComponentFixture<SearchProcessingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SearchProcessingComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchProcessingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
