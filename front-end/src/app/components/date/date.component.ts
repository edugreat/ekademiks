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

  //declare field category name here
  categoryName:string ='';

  //Create an empty date array
  uniqueDates:number[] = [];  
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


  /**
   * declares a method to subscribe to the multi-service and get the dates arraay
   */
  private getAvailableDates() {

    //check if the id and categoryName parameters exist in the router
    const idAndNameExist = this.route.snapshot.paramMap.has("id" && "categoryName");

    if (idAndNameExist) {
      //converts the id param to a number
      const categoryId = Number(this.route.snapshot.paramMap.get("id")!);
      
      //extract the categoryName parameter
       this.categoryName = this.route.snapshot.paramMap.get("categoryName")!;

      console.log(`category name is ${this.categoryName}`);

      this.multiService.fetchAvailableExamDates(categoryId).subscribe(data=>{
        this.availableDates = data;

        //extract only unique date values
        this.uniqueDates = (this.availableDates.map(x=>x.examYear)).map(isoDate=>new Date(isoDate)).filter(this.isUnique).map(isoDate=>isoDate.getFullYear());

       // this.uniqueDates.forEach(x=>console.log(x))
       
       } );
                                        }
  }

  /**
   * This method returns true if the index of the current array is equal to the current index.
   * It is a callback function to return unique array records
   * @param current current array value
   * @param index The index of the current array value
   * @param dates The array
   * @returns 
   */
  isUnique(current:Date, index:number, dates:Date[]):boolean{

    return (index === dates.indexOf(current))
  }

  getAvailableSubjects(input:string){
    console.log(input)//continue fro here
  }
 
}
