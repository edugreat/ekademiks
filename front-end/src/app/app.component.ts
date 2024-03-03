import { Component } from '@angular/core';
import { SearchService } from './services/search.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  found =true;
  constructor(private searchService:SearchService){
    this.searchService.found.subscribe(x => this.found = x)
  }
  title = 'front-end';
  
  logo ='assets/logo/edukademiks-logo.png';
}
