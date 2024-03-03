import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of, ReplaySubject, Subject} from 'rxjs';
import { NameAndId } from '../util/name-and-id';
import { Search } from '../util/search';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  //subject that emits boolean whenever search item is made to show whether the keyword was found or not
  found = new BehaviorSubject<boolean>(true);
  //emits observable to clear off the search box once questions are populated
  private _questionDisplay = new Subject<boolean>();
//emits array of NameAndId
searchResponse = new Subject<NameAndId[]>();
  //enum object against which searches keyword for category is compared
  searchConst = Search

  //endpoint for the search
  searchEndpoint = 'http://localhost:8080/subject/category';
  constructor(private router:Router, private httpClient:HttpClient) { }

  search(keyword:string){
    //get the index of the keyword from the enum if exists
    const index = Object.keys(this.searchConst).indexOf(keyword.toUpperCase());

    //check if the index exists
    if(index !== -1){
     //get the value that was searched
     const val = Object.values(this.searchConst)[index];
     //build url with these parameters
     const url = `category/${val}/${index+1}`;
     this.router.navigateByUrl(url);
     
    this.found.next(true)

    }else{
      //route to the backend for further search
     this.searchSubjectCategory(keyword);
    }
    
  }
  /**
   * 
   * @param keyword the keyword the student wishes to lookup
   */
  private searchSubjectCategory(keyword:string){
  const obsResponse = this.httpClient.get<NameAndId[]>(`${this.searchEndpoint}?name=${keyword}`);
  let data:NameAndId[] = [];
  obsResponse.subscribe(x => {
    data = x;
    this.searchResponse.next(data)
  });
  
  
  
}
//getter for the observable
public get questionDisplay() {
  return this._questionDisplay;
}
//setter for the observable
public set questionDisplay(value) {
  this._questionDisplay = value;
}
  
}
