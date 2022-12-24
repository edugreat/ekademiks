import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { MultiService } from 'src/app/services/multi.service';
import { SearchService } from 'src/app/services/search.service';
import { SubjectDate } from 'src/app/util/subject-date';

@Component({
  selector: 'app-search-output',
  templateUrl: './search-output.component.html',
  styleUrls: ['./search-output.component.css']
})
export class SearchOutputComponent implements OnInit{
  catId = 0;
  subjectName ='';
  subjectDate:SubjectDate[] = [];
  dates:number[] =[];
  //hides the view component when the custom 'page-not-found' is shown
  found = true;
constructor(private route:ActivatedRoute,
            private multiService:MultiService,
            private searchService:SearchService,
            private router:Router,
            private location:Location){}

  ngOnInit(): void {
    this.route.paramMap.subscribe(()=>{
      this.outputDates();
      this.searchService.found.subscribe(result =>this.found =result)
    })
  }
  //outputs date based on the user selection after 'search operation'
outputDates(){
  const hasAllParams = this.route.snapshot.paramMap.has('cate_name'&&'cate_id');
const cate_name  = this.route.snapshot.paramMap.get('cate_name')!;
const cate_id = this.catId=  Number(this.route.snapshot.paramMap.get('cate_id'));
//retrieve the searched keyword from the url parameter
this.subjectName = this.route.snapshot.paramMap.get('sub')!;
if(hasAllParams && cate_name && cate_id){
  console.log("cat name "+cate_name+" subject "+this.subjectName);
  this.multiService.searchDates(cate_name, this.subjectName).subscribe(response =>{
    this.subjectDate = response;
    (this.subjectDate.length === 0)
                 ? 
       this.searchService.found.next(false)
                 :
      this.dates = this.subjectDate.map(sub=> sub.examYear)
     .map(iso=>new Date(iso)).map(dates => dates.getFullYear());
     console.log(JSON.stringify(this.dates))
     this.searchService.found.next(true) ; 
  } )}

}
goToQuestion(event:Event){
const target = event.target as HTMLOptionElement;
const year = Number(target.value);
if(year){
this.router.navigateByUrl(
  `/question/${this.catId}/${this.subjectName}/${year}`);

 
}

}
//routes back the browser
goBack(){
  this.location.back();
}
}
