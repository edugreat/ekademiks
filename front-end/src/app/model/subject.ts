import { Options } from "./options";

export class Subject {
    public id: number;
    public name:string;
    public question: string;
    public questionNumber:string;
    public number:number;
    public date:Date;
    public options:Options;

    constructor(
        id:number,
        name:string,
        question:string,
        questionNumber:string,
        number: number,
        date: Date,
        options:Options 
        ){

        this.id = id;
        this.name = name;
        this.question= question,
        this.questionNumber= questionNumber;
        this.number = number;
        this.date = date;
        this.options = options;
    }
}
