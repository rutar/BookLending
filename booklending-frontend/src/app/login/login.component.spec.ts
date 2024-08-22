import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {
    authServiceMock = {
      login: jasmine.createSpy('login').and.returnValue(of('mock-token')),
      saveToken: jasmine.createSpy('saveToken')
    };

    routerMock = {
      navigate: jasmine.createSpy('navigate')
    };

    await TestBed.configureTestingModule({
      imports: [FormsModule, HttpClientModule, LoginComponent], // Add HttpClientModule to imports
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should call authService.login and navigate to /home on successful login', () => {
    component.username = 'testuser';
    component.password = 'password';

    component.login();

    expect(authServiceMock.login).toHaveBeenCalledWith('testuser', 'password');
    expect(authServiceMock.saveToken).toHaveBeenCalledWith('mock-token');
    expect(routerMock.navigate).toHaveBeenCalledWith(['/home']);
  });

  it('should show an error message on failed login', () => {
    authServiceMock.login.and.returnValue(throwError({ status: 401 }));

    component.login();

    expect(authServiceMock.login).toHaveBeenCalledWith(component.username, component.password);
    expect(authServiceMock.saveToken).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should initialize with default values', () => {
    expect(component.username).toBe('');
    expect(component.password).toBe('');
    expect(component.errorMessage).toBe('');
  });
});
