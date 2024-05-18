import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';
import { MediaChange, MediaObserver } from '@angular/flex-layout';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  deviceXs:boolean = false;
  mediaSub?:Subscription;
  colspan = 2;
  welcomeMsg = "Embark on your academic journey with us and unlock a world of knowledge and growth. Whether you're a student striving for excellence or an educator dedicated to nurturing minds, our platform is here to empower you every step of the way. Explore our comprehensive range of assessments tailored to junior and senior high school levels, designed to challenge and inspire. Dive into subjects that spark your curiosity, from mathematics to literature, and beyond. With personalized profiles, real-time feedback, and insightful analytics, your learning experience is as unique as you are. Join a community of learners committed to success and discover your full potential with e-Kademiks. Start your adventure now. The path to greatness awaits";

  weclome2 = "Embark on your academic journey with us by exploring our different range of assessments tailored for both students of junior and senior categories."
  
  
  constructor(private mediaObserver: MediaObserver){}
 

  ngOnInit(): void {
    this.mediaSub = this.mediaAlias()
   
  }

  private mediaAlias(): Subscription {
    return this.mediaObserver.asObservable().subscribe((changes: MediaChange[]) =>{
     
    
      this.deviceXs = changes.some(change => change.mqAlias === 'xs');
      this.colspan = this.deviceXs ? 4: 2;

    });
  }

  ngOnDestroy(): void {
   this.mediaSub?.unsubscribe();
  }
}
