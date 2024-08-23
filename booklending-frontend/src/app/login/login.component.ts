import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private http: HttpClient, private router: Router) {
  }

  login() {
    this.authService.login(this.username, this.password).subscribe(
      (token: string) => {
        // Save the JWT token
        this.authService.saveToken(token);

        // Decode the role from the token
        const roleId = this.authService.getRoleFromToken();



        // Navigate based on the user role
        if (roleId === null) {
          console.error('Role ID is null. Redirecting to login.');
          this.router.navigate(['/login']); // Redirect to login or error page if role is null
        } else if (roleId === 1) {  // Assuming role ID 1 represents admin
          this.router.navigate(['/dashboard']);
        } else if (roleId === 2) {  // Assuming role ID 2 represents user
          this.router.navigate(['/home']);
        } else {
          console.error('Unknown role ID:', roleId);
          console.error('Redirecting to login.');
          this.router.navigate(['/login']);
        }
      },
      (error) => {
        console.error('Login failed', error);
        alert('Invalid username or password');
      }
    );
  }

}
