import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MultiService } from 'src/app/services/multi.service';
import { SearchService } from 'src/app/services/search.service';
import { SubjectDate } from 'src/app/util/subject-date';
import { SubjectName } from 'src/app/util/subject-name';

/*
This  component is responsible for  pupulating the view with a drop-down
list of dates whenever a course category is selected. Therefore it receives a dates
array from the backend end corresponding to the all the available dates
for the subjects for which their category was selected. It also populates the subject name
*/


@Component({
  selector: 'app-date',
  templateUrl: './date.component.html',
  styleUrls: ['./date.component.css']
})
export class DateComponent implements OnInit {

  //determines when to display custom 'page-not-found' as well as itself
  found = true;

  //declare field category name here
  categoryName: string = '';

  //Create an empty date array
  uniqueDates: number[] = [];
  //create an empty array dates instance
  availableDates: SubjectDate[] = [];

  //declares and initialize an array of SubjectName 
  subjectName: SubjectName[] = [];

  //declares the categoryId
  categoryId?:number;

  //Inject the multi-service and activated Routes instances here
  constructor(private multiService: MultiService,
    private route: ActivatedRoute, private router:Router,
    private searchService:SearchService
    ) { 
      
    }

  ngOnInit(): void {

    this.route.paramMap.subscribe(() => {
           this.getAvailableDates();
           this.searchService.found.subscribe(result => this.found = result);
              })
    
  }
  
  /**
   * declares a method to subscribe to the multi-service and get the dates arraay
   */
  private getAvailableDates() {

    //check if the id and categoryName parameters exist in the router
    const idAndCategoryNameExist = this.route.snapshot.paramMap.has("id" && "categoryName");
    //extract the categoryName parameter
    this.categoryName = this.route.snapshot.paramMap.get("categoryName")!;

      //converts the id param to a number
      this.categoryId = Number(this.route.snapshot.paramMap.get("id")!);
    
      if (idAndCategoryNameExist && !Number.isNaN(this.categoryId)) {
      this.multiService.fetchAvailableExamDates(this.categoryId, this.categoryName).subscribe(data => {
        this.availableDates = data;
        //extract only unique date values
        this.uniqueDates = (this.availableDates.map(dates => dates.examYear)
                            .map(uniqueIso => new Date(uniqueIso))
                            .map(isoDates => isoDates.getFullYear()));
        /**
         * clears possible previous display of custom 'page-not-found'
         * if the was returned, else no data was found
         */
        this.uniqueDates.length > 0? this.searchService.found.next(true) : this.searchService.found.next(false);
        
        
      });

    }else{
      //the user might have forwarded bad url param for 'id'
      this.searchService.found.next(false);
    }
  }
  
  //method that routes to the a component
  goToSubject(examYear:string){
  
    this.router.navigateByUrl(`exam/${this.categoryName}/${examYear}/${this.categoryId}`);


  }

}
