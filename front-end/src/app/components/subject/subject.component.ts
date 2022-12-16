import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { StudentAttempt } from 'src/app/model/student-attempt';
import { Subject } from 'src/app/model/subject';
import { MultiService } from 'src/app/services/multi.service';
import { SearchService } from 'src/app/services/search.service';
import { StudentAttemptService } from 'src/app/services/student-attempt.service';

@Component({
  selector: 'app-subject',
  templateUrl: './subject.component.html',
  styleUrls: ['./subject.component.css']
})
export class SubjectComponent implements OnInit {
  
  //determines when to display the custom 'page-not-found' as well as itself
found = true;

//declares and initializes empty Subject array
subjects:Subject[] =[];
examYear:string ='';
categoryId:number = 0;
subjectName:string='';

//declares array to hold students attempts
attempts:StudentAttempt[] =[];

  constructor(private multiService:MultiService, 
    private router:ActivatedRoute,
    private attemptService:StudentAttemptService,
    private searchService:SearchService) {
  this.searchService.found.subscribe(x => this.found = x);
    }

  ngOnInit(): void {

    //inittializes the component with a call to this method
    this.router.paramMap.subscribe(()=>{
      this.getSubject();
    })
  }

  getSubject(){
    //confirm that the required router parameters are present
    const hasAll = (this.router.snapshot.paramMap.has("categoryId"))
    &&
    (this.router.snapshot.paramMap.has("subjectName"))
    &&(this.router.snapshot.paramMap.has("examYear"));

    //implements the method if the condition is true
    if(hasAll){
      
      //get the parameters
     const examYear =  this.examYear = this.router.snapshot.paramMap.get("examYear")!;
     const subjectName =  this.subjectName = this.router.snapshot.paramMap.get("subjectName")!
     const categoryId = this.categoryId = +this.router.snapshot.paramMap.get("categoryId")!
      
      this.multiService.fetchSubjects(examYear, subjectName, categoryId).subscribe(response=>{
      this.subjects = response
      })
      
    }

  }
  /**
   * Method that saves student's selected options
   * @param event The event initiated by the stuent's choosing of options
   */
  saveAttempt(event:Event){
   const target = event.target as HTMLInputElement;
    this.attemptService.persistAttempts(target,this.examYear, this.subjectName,this.categoryId);

  }
}
