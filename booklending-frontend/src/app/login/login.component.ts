import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../services/auth.service';
import {NotificationService} from '../services/notification.service';

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

  constructor(private authService: AuthService, private http: HttpClient, private router: Router, private notificationService: NotificationService) {
  }

  login() {
    this.authService.login(this.username, this.password).subscribe(
      (token: string) => {
        // Save the JWT token
        this.authService.saveToken(token);

        // Decode the role from the token
        const roleName = this.authService.getRoleNameFromToken();



        // Navigate based on the user role
        if (roleName === null) {
          console.error('Role name is null. Redirecting to login.');
          this.router.navigate(['/login']); // Redirect to login or error page if role is null
        } else if (roleName === 'ADMIN') {  // Assuming role name represents admin
          this.router.navigate(['/dashboard']);
        } else if (roleName === "USER") {  // Assuming role name represents user
          this.router.navigate(['/home']);
        } else {
          console.error('Unknown role:', roleName);
          console.error('Redirecting to login.');
          this.router.navigate(['/login']);
        }
      },
      (error) => {
        console.error('Login failed', error);
        this.notificationService.openDialog('Invalid username or password.', true);
      }
    );
  }

}
