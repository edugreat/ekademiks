import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { MaterialModule } from '../material/material.module';
import { CommonModule } from '@angular/common';
import { AppRoutingModule } from '../app-routing/app-routing.module';




@NgModule({
  declarations: [
    HomeComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    AppRoutingModule
   
  ],
  exports:[HomeComponent]
})
export class HomeModule { }
