import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Category } from '../model/category';
import { SubjectDate } from '../util/subject-date';
import { SubjectName } from '../util/subject-name';

@Injectable({
  providedIn: 'root'
})
export class MultiService {

  private dateEndpoint = "http://localhost:8080/subject";
 
  private baseUrl:string = "http://localhost:8080/akad/categories";

  private subjectNameEndpoint = this.dateEndpoint+"/date";

  constructor(private httpClient: HttpClient) { }

  /**
   * A service method that fetches course categories
   */
  public getCourseCategories():Observable<Category[]>{

    return this.httpClient.get<fetchResponse>(this.baseUrl).pipe(

    map(response =>response._embedded.categories)

    );
  }
   
   //This method returns a dates array from the backend server
  fetchAvailableExamDates(id:number):Observable<SubjectDate[]> {
    return this.httpClient.get<SubjectDate[]>(`${this.dateEndpoint}?id=${id}`);
  }

  //Method returns Subject array with name attributes
  fetchSubjectNames(examYear:string, categoryName:string):Observable<SubjectName[]>{
    
    //routes to the controller class's GET method in the database
    
    return this.httpClient.get<SubjectName[]>(`${this.subjectNameEndpoint}?date=${examYear} &categoryName=${categoryName}`);

  }
   
}
//declare an interface to fetch the course categories
interface fetchResponse{
 _embedded:{
  categories:Category[]
 }
}