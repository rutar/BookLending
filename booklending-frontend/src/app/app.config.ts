import { enableProdMode, importProvidersFrom } from '@angular/core';
import { AppComponent } from './app.component';
import { provideRouter } from '@angular/router';
import { bootstrapApplication } from '@angular/platform-browser';
import { routes } from './app.routes';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms'; // Import FormsModule here
import { AuthService } from './services/auth.service'; // Import AuthService
import { environment } from '../environments/environment';
import {BooksService} from "./services/books.service";

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    importProvidersFrom(
      HttpClientModule,
      FormsModule // Include FormsModule here
    ),
    AuthService, // Provide AuthService here
    BooksService
  ]
})
  .catch(err => console.error(err));
