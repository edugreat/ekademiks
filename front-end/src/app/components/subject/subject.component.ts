import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'src/app/model/subject';
import { MultiService } from 'src/app/services/multi.service';

@Component({
  selector: 'app-subject',
  templateUrl: './subject.component.html',
  styleUrls: ['./subject.component.css']
})
export class SubjectComponent implements OnInit {
//declares and initializes empty Subject array
subjects:Subject[] =[];

  constructor(private multiService:MultiService, private router:ActivatedRoute) { }

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
    (this.router.snapshot.paramMap.has("examName"))
    &&(this.router.snapshot.paramMap.has("examYear"));

    //implements the method if the condition is true
    if(hasAll){
      
      //get the parameters
      const examYear = this.router.snapshot.paramMap.get("examYear")!;
      const examName = this.router.snapshot.paramMap.get("examName")!
      const categoryId = +this.router.snapshot.paramMap.get("categoryId")!
      
      this.multiService.fetchSubjects(examYear, examName, categoryId).subscribe(response=>{
      this.subjects = response
      })
    }

  }
}
