import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, of} from 'rxjs';
import { Search } from '../util/search';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  //subject that emits boolean whenever search item is made to show whether the keyword was found or not
  found = new BehaviorSubject<boolean>(true);

  //enum object against which searches keyword for category is compared
  searchConst = Search

  constructor(private router:Router) { }

  search(keyword:string){
    //get the index of the keyword from the enum if exists
    const index = Object.keys(this.searchConst).indexOf(keyword.toUpperCase());

    //check if the index exists
    if(index !== -1){
     //get the value that was searched
     const val = Object.values(this.searchConst)[index];
     console.log("category name is  :"+val)
     //build url with these parameters
     const url = `category/${val}/${index+1}`;
     this.router.navigateByUrl(url);
     
     return of(this.found.next(true))

    }else{
      //search key was not found

      return of(this.found.next(false))
    }
    
  }
  
}
