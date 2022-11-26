import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MultiService } from 'src/app/services/multi.service';
import { SubjectDate } from 'src/app/util/subject-date';

/*
This  component is responsible for  pupulating the view with a drop-down
list of dates whenever a course category is selected. Therefore it receives a dates
array from the backend end corresponding to the all the available dates
for the subjects for which their category was selected
*/


@Component({
  selector: 'app-date',
  templateUrl: './date.component.html',
  styleUrls: ['./date.component.css']
})
export class DateComponent implements OnInit {

  //Create an empty date array
  uniqueDates:Date[] = [];  
  //create an empty array dates instance
  availableDates: SubjectDate[] =[];

  //Inject the multi-service and activated Routes instances here
  constructor(private multiService: MultiService,
    private route: ActivatedRoute) { }

  ngOnInit(): void {

    this.route.paramMap.subscribe(()=>{
      this.getAvailableDates();
    })

  }


  //declare a method to subscribe to the multi-service and get the dates
  private getAvailableDates() {

    //check if the id param exists in the router
    const idExists = this.route.snapshot.paramMap.has("id");

    if (idExists) {
      const categoryId = Number(this.route.snapshot.paramMap.get("id")!);
      this.multiService.fetchAvailableExamDates(categoryId).subscribe(data=>{
        this.availableDates = data;
        

        this.uniqueDates = (this.availableDates.map(x=>x.examYear));

        console.log(JSON.stringify(this.uniqueDates));
       
       } );
                                        }
  }

  isUnique(current:Date, index:number, dates:SubjectDate[]):boolean{

    return current !== dates[index++].examYear;
  }
 
}
