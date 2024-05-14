import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './home/home.component';
import {MatGridListModule} from '@angular/material/grid-list';
import { FlexLayoutModule } from '@angular/flex-layout';




@NgModule({
  declarations: [
    HomeComponent
  ],
  imports: [
    CommonModule,
    MatGridListModule,
    FlexLayoutModule
   
  ]
})
export class HomeModule { }
