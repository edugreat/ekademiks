export class Solution {
    private id:number;
    private date:Date;
    private subject:string;
    private link:string;

    constructor(
        id: number, 
        date:Date,
        subject:string,
        link:string){
     this.id = id;
     this.date = date;
     this.link = link;
     this.subject = subject;
        }
}
