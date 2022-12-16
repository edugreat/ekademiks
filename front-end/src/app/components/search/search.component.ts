import { Component } from '@angular/core';
import { SearchService } from 'src/app/services/search.service';


@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent {
   constructor(private searchService:SearchService) { }

  /**
   * 
   * @param keyword the keyword (subject) the student wishes to lookup for
   */
  searchFor(keyword: string) {
    this.searchService.search(keyword);
        
    }


  }


