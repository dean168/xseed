import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';

import { translates } from './app.translates';
import { routing } from './app.routing';
import { declarations } from './app.declarations';
import { providers } from './app.providers';

@NgModule({
  declarations: [...declarations],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    CommonModule,
    RouterModule,
    FormsModule,
    HttpClientModule,
    translates,
    routing
  ],
  providers: [...providers],
  bootstrap: [AppComponent]
})
export class AppModule { }
