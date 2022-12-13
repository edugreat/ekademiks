import { Injectable } from '@angular/core';
import { StudentAttempt } from '../model/student-attempt';

@Injectable({
  providedIn: 'root'
})
export class StudentAttemptService {
  //inject student attempt here
  attempts: StudentAttempt[] = [];
  //session storage to store the students attempt
  storage: Storage = sessionStorage;

  constructor() {

  }
  //method that persists the students attempts to the session storage
  persistAttempts(target: HTMLInputElement, examYear: string, subjectName: string, categoryId: number) {

    //get the question number being attempted
    const questionNumber = Number(target.getAttribute("name"));
    //get the option selected by the student
    const optionSelected = target.value;

    //get the local storage item if exists
    let items: StudentAttempt[] = this.getStorageItem();


    switch (items) {
      case null:

        //initialize the first student's attempt
        let initialAttempt: StudentAttempt = new StudentAttempt(
          subjectName, examYear, categoryId, questionNumber, optionSelected
        );
        //populate the attempts array
        this.attempts.push(initialAttempt);
        //populate the session storage
        this.storage.setItem("my-username", JSON.stringify(this.attempts));
        break;

      default:
        //something exists in the session storage
        //get the items in the session storage


        //get the attempt for the given criteria if exists
        let currentAttempt = items.find(item => {

          return this.findAttemptFor(item, categoryId, subjectName, examYear, questionNumber);
        });

        if (currentAttempt !== undefined) {
          //get the index of the attempt from array of attempt
          const currentIndex = items.findIndex(item => item == currentAttempt);

          //update the selected option
          currentAttempt.selectedOption = optionSelected;

          //update items to be peristed to the session storage
          items.splice(currentIndex, 1, currentAttempt);

          //update the sesssion storage
          this.storage.setItem('my-username', JSON.stringify(items));

        } else {
          // currentAttempt is undefined meaning, no attempt for given quetion number

          //create new attempt object
          let tempAttempt = new StudentAttempt(subjectName, examYear, categoryId, questionNumber, optionSelected);
          //save to the items array
          items.push(tempAttempt);
          //persist to session storage
          this.storage.setItem("my-username", JSON.stringify(items));
        }

        break;
    }

  }

  //helper method to check and retrieve the attempt for the given criteria from the sesion storage
  private findAttemptFor(item: StudentAttempt, categoryId: number, subjectName: string, examYear: string, questionNumber: number): unknown {
    return item.categoryId == categoryId
      && item.subjectName == subjectName
      && item.examYear == examYear
      && item.questionNumber == questionNumber;
  }

  //declares a helper method to get localStorage item
  private getStorageItem(): StudentAttempt[] {

    return JSON.parse(this.storage.getItem("my-username")!);
  }


}
