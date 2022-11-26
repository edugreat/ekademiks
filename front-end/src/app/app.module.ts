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

const routes: Routes=[
  {path: 'category/:id', component: DateComponent}
]

@NgModule({
  declarations: [
    AppComponent,
    SubjectCategoryComponent,
    SubjectComponent,
    SolutionComponent,
    DateComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    HttpClientModule
  ],
  providers: [MultiService],
  bootstrap: [AppComponent]
})
export class AppModule { }
