import { Options } from "./options";

export class Subject {
    private id: number;
    private name:string;
    private questionNumber:string;
    private number:number;
    private date:Date;
    private options:Options;

    constructor(
        id:number,
        name:string,
        questionNumber:string,
        number: number,
        date: Date,
        options:Options 
        ){

        this.id = id;
        this.name = name;
        this.questionNumber= questionNumber;
        this.number = number;
        this.date = date;
        this.options = options;
    }
}
