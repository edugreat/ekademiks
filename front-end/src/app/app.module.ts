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

const routes: Routes=[
  {path: 'category/:categoryName/:id', component: DateComponent},
  {path: 'exam/:categoryName/:examYear', component: SubjectNameComponent},
  {path: "question/:categoryId/:examName:/:examYear", component: SubjectComponent}
]

@NgModule({
  declarations: [
    AppComponent,
    SubjectCategoryComponent,
    SubjectComponent,
    SolutionComponent,
    DateComponent,
    SubjectNameComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [MultiService],
  bootstrap: [AppComponent]
})
export class AppModule { }
