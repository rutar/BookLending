import { TestBed, ComponentFixture, waitForAsync } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;
  let notificationService: jasmine.SpyObj<NotificationService>;

  beforeEach(waitForAsync(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['login', 'saveToken', 'getRoleNameFromToken']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const notificationSpy = jasmine.createSpyObj('NotificationService', ['openDialog']);

    TestBed.configureTestingModule({
      imports: [
        LoginComponent, // Import the standalone component
        HttpClientTestingModule,
        FormsModule
      ],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy },
        { provide: NotificationService, useValue: notificationSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
  }));

  it('should create the login component', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to the dashboard on successful admin login', () => {
    const mockToken = 'adminToken';
    authService.login.and.returnValue(of(mockToken));
    authService.getRoleNameFromToken.and.returnValue('ADMIN');

    component.login();

    expect(authService.saveToken).toHaveBeenCalledWith(mockToken);
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should navigate to the home on successful user login', () => {
    const mockToken = 'userToken';
    authService.login.and.returnValue(of(mockToken));
    authService.getRoleNameFromToken.and.returnValue('USER');

    component.login();

    expect(authService.saveToken).toHaveBeenCalledWith(mockToken);
    expect(router.navigate).toHaveBeenCalledWith(['/home']);
  });

  it('should display error message on login failure', () => {
    const mockError = { status: 401 };
    authService.login.and.returnValue(throwError(mockError));

    component.login();

    expect(notificationService.openDialog).toHaveBeenCalledWith('Invalid username or password.', true);
  });

  it('should redirect to login if role name is null', () => {
    const mockToken = 'nullRoleToken';
    authService.login.and.returnValue(of(mockToken));
    authService.getRoleNameFromToken.and.returnValue(null);

    component.login();

    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should redirect to login on unknown role', () => {
    const mockToken = 'unknownRoleToken';
    authService.login.and.returnValue(of(mockToken));
    authService.getRoleNameFromToken.and.returnValue('UNKNOWN_ROLE');

    component.login();

    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
