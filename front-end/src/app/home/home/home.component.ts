import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

   welcomeMsg = "Welcome to e-Kademiks. Embark on your academic journey with us and unlock a world of knowledge and growth. Whether you're a student striving for excellence or an educator dedicated to nurturing minds, our platform is here to empower you every step of the way. Explore our comprehensive range of assessments tailored to junior and senior high school levels, designed to challenge and inspire. Dive into subjects that spark your curiosity, from mathematics to literature, and beyond. With personalized profiles, real-time feedback, and insightful analytics, your learning experience is as unique as you are. Join a community of learners committed to success and discover your full potential with e-Kademiks.Start your adventure now. The path to greatness awaits";

   myBreakPoint :number =0;
   constructor(){}

  ngOnInit(): void {
    
    this.myBreakPoint = (window.innerWidth <= 600) ? 1: 4;
  }

  handleEvent(event:any){

    this.myBreakPoint =(event.target.innerWidth <= 600) ? 1:4
  }
}


