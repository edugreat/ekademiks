export class Subject {
    private id: number;
    private name:string;
    private question:string;
    private number:number;
    private date:Date;

    constructor(
        id:number,
        name:string,
        question:string,
        number: number,
        date: Date 
        ){

        this.id = id;
        this.name = name;
        this.question= question;
        this.number = number;
        this.date = date;
    }
}
