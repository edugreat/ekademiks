import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SearchService } from 'src/app/services/search.service';
import { NameAndId } from 'src/app/util/name-and-id';

@Component({
  selector: 'app-search-output',
  templateUrl: './search-processing.component.html',
  styleUrls: ['./search-processing.component.css']
})
export class SearchProcessingComponent implements OnInit{
  //hides the view when the custom 'page-not-found' is shown
  found = true;
  keySearch ='';
categoryNameAndId:NameAndId[] =[];
  constructor(private searchService:SearchService, 
    private route:ActivatedRoute, private router:Router){}

  ngOnInit(): void {
    this.route.paramMap.subscribe(()=>{
      this.search();
      this.searchService.found.subscribe(result => this.found = result);
    })
    
  }


  /**
   * 
   * @param keyword the keyword (subject) the student wishes to lookup for
   */
  search() {
   const exists =  this.route.snapshot.paramMap.has('keyword');
   
  const keyword = this.route.snapshot.paramMap.get('keyword');
  if(exists && keyword){
    //get the searched keyword
    this.keySearch = this.route.snapshot.paramMap.get('keyword')!;

    this.searchService.search(keyword);
    //waits for asynchronous response
    this.asyncResponse();
  }else{this.searchService.found.next(false)}
   
        
    }
    asyncResponse(){
      this.searchService.searchResponse.subscribe(response =>{
        this.categoryNameAndId = response;
         //if nothing was found, observable emits 'false' flag to display custom 'page-not-found'
         (this.categoryNameAndId.length !== 0 
          ? this.searchService.found.next(true)
          : this.searchService.found.next(false))
      });
      }
  processSelection(event:Event){
    let routeUrl = `search/category`;
   const target  = event.target as HTMLOptionElement;
   const selectedCategoryName = target.value;
   //get the selected category
   const selectedCategory = this.categoryNameAndId.find(category => category.name === selectedCategoryName) 
   if(selectedCategory)  {
 //get the id of the selected category
 const selectedId = selectedCategory.id;
 //route to this component
 this.router.navigateByUrl(
  `${routeUrl}/${selectedCategoryName}/id/${selectedId}/subj/${this.keySearch}`);
   }
  
  }
}
