import { Component, OnInit } from '@angular/core';
import { Category } from 'src/app/model/category';
import { MultiService } from 'src/app/services/multi.service';

@Component({
  selector: 'app-subject-category',
  templateUrl: './subject-category.component.html',
  styleUrls: ['./subject-category.component.css']
})
export class SubjectCategoryComponent implements OnInit {

//Declare an array of course categories here
public courseCategories:Category[] = [];

  constructor(private multiService:MultiService) { }

  ngOnInit(): void {
    this.getCourseCategories();
  }


  /**
   * Declare a method to subscribe to the SubjectService
   * and fetch the course categories
   */
  public getCourseCategories( ){

    this.multiService.getCourseCategories().subscribe(
      response => this.courseCategories = response
    )

  }
}
