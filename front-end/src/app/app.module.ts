import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';

import { HttpClientModule} from '@angular/common/http';
import { SubjectCategoryComponent } from './components/subject-category/subject-category.component';
import { SubjectComponent } from './components/subject/subject.component';
import { SolutionComponent } from './components/solution/solution.component'
import { MultiService } from './services/multi.service';
import { DateComponent } from './components/date/date.component';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SubjectNameComponent } from './components/subject-name/subject-name.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { SearchComponent } from './components/search/search.component';
import { SearchProcessingComponent } from './components/search-processing/search-processing.component';
import { SearchOutputComponent } from './components/search-output/search-output.component';

const routes: Routes=[
  
  {path: 'category/:categoryName/:id', component: DateComponent},
  {path: 'exam/:categoryName/:examYear/:categoryId', component: SubjectNameComponent},
  {path: "question/:categoryId/:subjectName/:examYear", component: SubjectComponent},
  {path: 'search/:keyword', component: SearchProcessingComponent},
  {path:'search/category/:cate_name/id/:cate_id/subj/:sub', component: SearchOutputComponent}
  
  
]

@NgModule({
  declarations: [
    AppComponent,
    SubjectCategoryComponent,
    SubjectComponent,
    SolutionComponent,
    DateComponent,
    SubjectNameComponent,
    PageNotFoundComponent,
    SearchComponent,
    SearchProcessingComponent,
    SearchOutputComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    HttpClientModule,
    FormsModule
  ],
 // exports:[RouterModule],
  providers: [MultiService],
  bootstrap: [AppComponent]
})
export class AppModule { }
