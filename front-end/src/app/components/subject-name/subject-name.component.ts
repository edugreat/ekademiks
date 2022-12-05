import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MultiService } from 'src/app/services/multi.service';
import { SubjectName } from 'src/app/util/subject-name';

@Component({
  selector: 'app-subject-name',
  templateUrl: './subject-name.component.html',
  styleUrls: ['./subject-name.component.css']
})
export class SubjectNameComponent implements OnInit{
//declares and initialize an array of SubjectName 
subjectName: SubjectName[] = [];

//declares & initializes the categoryName here
categoryName:string='';

//declares an initializes a string array for storing unique Subject names
uniqueSubjectName:string[] = [];

constructor(private multiService:MultiService,
  private route:ActivatedRoute){}

  ngOnInit(): void {
   this.route.paramMap.subscribe(()=>{
    this.getAvailableSubjects();
   })
    //invokes initializing method(s) here
    
  }


  getAvailableSubjects(){
   
    const hasInputs = this.route.snapshot.paramMap.has("categoryName" && "examYear");

    if(hasInputs){

      //gets the category name from the activated route object
    this.categoryName = this.route.snapshot.paramMap.get("categoryName")!;

   //gets the exam year from the activated route object
   const examYear = this.route.snapshot.paramMap.get("examYear")!;

   //call the service method to retrieve our SubjectName array
   this.multiService.fetchSubjectNames(examYear, this.categoryName).subscribe(data => {
     
    this.subjectName = data;

    this.uniqueSubjectName = this.subjectName.map(x => x.name).filter(this.isUniqueName)
    
    console.log("is names are"+this.uniqueSubjectName);

    console.log(JSON.stringify(this.subjectName));
   });

    }

    

   
 }



   

//implements method callback that filters unique string array
/**
* 
* @param current current array element to test
* @param index current array index
* @param names the string array to test its records
* @returns boolean
*/
isUniqueName(current:string, index:number, names:string[]):boolean{

 return (index === names.indexOf(current))
}

}
