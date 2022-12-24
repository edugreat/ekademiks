import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Category } from '../model/category';
import { Subject } from '../model/subject';
import { SubjectDate } from '../util/subject-date';
import { SubjectName } from '../util/subject-name';

@Injectable({
  providedIn: 'root'
})
export class MultiService {

  private dateEndpoint = "http://localhost:8080/subject";
 
  private baseUrl:string = "http://localhost:8080/akad";

  private subjectNameEndpoint = this.dateEndpoint+"/date";
  private searchUrl = "http://localhost:8080/subject/search";

  constructor(private httpClient: HttpClient) { }

  /**
   * A service method that fetches course categories
   */
  public getCourseCategories():Observable<Category[]>{

    return this.httpClient.get<fetchResponse>(`${this.baseUrl}/categories`).pipe(

    map(response =>response._embedded.categories)

    );
  }
   
   //This method returns a dates array from the backend server
  fetchAvailableExamDates(id:number, categoryName:string):Observable<SubjectDate[]> {
    return this.httpClient.get<SubjectDate[]>(`${this.dateEndpoint}?id=${id}&name=${categoryName}`);
  }

  //Method returns Subject array with name attributes
  fetchSubjectNames(examYear:string, categoryName:string):Observable<SubjectName[]>{
    
    //routes to the controller class's GET method in the database
    
    return this.httpClient.get<SubjectName[]>(`${this.subjectNameEndpoint}?date=${examYear}&categoryName=${categoryName}`);

  }
/**
 * Implements the fetchSubjects abstraction
 * @param examYear the year record to query against
 * @param examName the examination name (eg Mathematics)
 * @param categoryId the category id (eg JAMB's category id) to further streamline the search
 */
  fetchSubjects(examYear:string, examName:string, categoryId:number):Observable<Subject[]>{
   
    return this.httpClient.get<fetchSubjects>
    (`${this.baseUrl}/subjects/search/findByNameAndExamDate?name=${examName}&categoryId=${categoryId}&examYear=${examYear}`).pipe(
      map(response=>response._embedded.subjects
        
      ))

  }
  /**
   * returns dates to select when user types a particular subject name in the search box
   * @param category the category the subject searched belongs
   * @param subject the subject the user intends to take exercise on
   * @param return Observable of SubjectDate array
   */
  searchDates(category:string, subject:string):Observable<SubjectDate[]>{
  
    return this.httpClient.get<SubjectDate[]>(`${this.searchUrl}?category=${category}&subject=${subject}`);
  }
   
}
//declare an interface to fetch the course categories
interface fetchResponse{
 _embedded:{
  categories:Category[]
 }
}

//declares an abstraction to fetch Subjects when implemented
interface fetchSubjects{
  _embedded:{
    subjects:Subject[]
  }
}