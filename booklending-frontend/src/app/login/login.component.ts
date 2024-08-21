import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service'; // Adjust the import path as necessary
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'] // Assuming SCSS is used
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (this.username && this.password) {
      this.authService.login(this.username, this.password).subscribe({
        next: (response) => {
          const token = response.token; // Adjust based on your backend response
          this.authService.saveToken(token);
          this.router.navigate(['/']); // Redirect to home or another route
        },
        error: (err) => {
          console.error('Login failed', err);
          // Handle login error, show a message to the user, etc.
        }
      });
    } else {
      console.error('Username or password is missing.');
      // Optionally, show an error message to the user
    }
  }
}
