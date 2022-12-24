import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchOutputComponent } from './search-output.component';

describe('SearchOutputComponent', () => {
  let component: SearchOutputComponent;
  let fixture: ComponentFixture<SearchOutputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SearchOutputComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchOutputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
