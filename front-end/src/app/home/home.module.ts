import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { MaterialModule } from '../material/material.module';
import { CommonModule } from '@angular/common';




@NgModule({
  declarations: [
    HomeComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
   
  ],
  exports:[HomeComponent]
})
export class HomeModule { }
