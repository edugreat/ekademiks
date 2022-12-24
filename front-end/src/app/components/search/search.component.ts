import { Component, OnInit } from '@angular/core';
import { SearchService } from 'src/app/services/search.service';
import { NameAndId } from 'src/app/util/name-and-id';


@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit{
  categoryNameAndId:NameAndId[] =[];
  searchKeyword = '';
  //tracks if the questions are populated to clear off the search box
   constructor(private searchService:SearchService){ }

  ngOnInit(): void {
    this.searchService.questionDisplay.subscribe(displayed =>{
      if(displayed)
      this.searchKeyword = '';
    })
  }

    
  }


