export class StudentAttempt {

    /**
     * Class that models the examination attempts
     * @param subjectName the subject currently being attempted
     * @param examYear the year such question was asked
     * @param categoryName the type of exam being attempted
     * @param questionNumber the question number answered
     * @param selectedOption the option selected
     */
    constructor(public subjectName:string,
                public examYear:string,
                public categoryId:number,
                public questionNumber?:number,
                public selectedOption?:string){}
}
