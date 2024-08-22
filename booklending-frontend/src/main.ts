import {enableProdMode, importProvidersFrom} from '@angular/core';
import {AppComponent} from './app/app.component';
import {provideRouter} from '@angular/router';
import {bootstrapApplication} from '@angular/platform-browser';
import {routes} from './app/app.routes';
import {HttpClientModule} from '@angular/common/http';
import {environment} from "./environments/environment";
import { DashboardComponent } from './app/dashboard/dashboard.component';

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    importProvidersFrom(HttpClientModule) // Import HttpClientModule here
  ]
})
  .catch(err => console.error(err));

