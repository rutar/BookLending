import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Mock Router
class MockRouter {
  navigate() {
    // Mock implementation of navigate
  }
}

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let httpMock: HttpTestingController;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,  // Import for HttpClientTesting
        CommonModule,             // Import for CommonModule
        FormsModule,              // Import for FormsModule
        LoginComponent            // Import the standalone component here
      ],
      providers: [
        AuthService,
        { provide: Router, useClass: MockRouter }  // Provide the mocked router
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should navigate to dashboard for role ID 1', () => {
    const spy = spyOn(router, 'navigate');
    const mockToken = 'mock.jwt.token';
    const mockRoleName = 'ADMIN'; // Role name

    spyOn(authService, 'login').and.returnValue(of(mockToken));
    spyOn(authService, 'saveToken').and.callThrough();
    spyOn(authService, 'getRoleNameFromToken').and.returnValue(mockRoleName);

    component.username = 'admin';
    component.password = 'password';
    component.login();

    // Ensure the HTTP request is made
    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockToken);

    expect(authService.saveToken).toHaveBeenCalledWith(mockToken);
    expect(spy).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should navigate to home for role ID 2', () => {
    const spy = spyOn(router, 'navigate');
    const mockToken = 'mock.jwt.token';
    const mockRoleName = 'USER'; // Role ID as a number

    spyOn(authService, 'login').and.returnValue(of(mockToken));
    spyOn(authService, 'saveToken').and.callThrough();
    spyOn(authService, 'getRoleNameFromToken').and.returnValue(mockRoleName);

    component.username = 'user';
    component.password = 'password';
    component.login();

    // Ensure the HTTP request is made
    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockToken);

    expect(authService.saveToken).toHaveBeenCalledWith(mockToken);
    expect(spy).toHaveBeenCalledWith(['/home']);
  });

  it('should navigate to login if role ID is null', () => {
    const spy = spyOn(router, 'navigate');
    const mockToken = 'mock.jwt.token';

    spyOn(authService, 'login').and.returnValue(of(mockToken));
    spyOn(authService, 'saveToken').and.callThrough();
    spyOn(authService, 'getRoleNameFromToken').and.returnValue(null);

    component.username = 'user';
    component.password = 'password';
    component.login();

    // Ensure the HTTP request is made
    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockToken);

    expect(authService.saveToken).toHaveBeenCalledWith(mockToken);
    expect(spy).toHaveBeenCalledWith(['/login']);
  });

  it('should navigate to login if role ID is unknown', () => {
    const spy = spyOn(router, 'navigate');
    const mockToken = 'mock.jwt.token';
    const mockRoleName = "SUPERUSER"; // Unknown role ID

    spyOn(authService, 'login').and.returnValue(of(mockToken));
    spyOn(authService, 'saveToken').and.callThrough();
    spyOn(authService, 'getRoleNameFromToken').and.returnValue(mockRoleName);

    component.username = 'user';
    component.password = 'password';
    component.login();

    // Ensure the HTTP request is made
    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockToken);

    expect(authService.saveToken).toHaveBeenCalledWith(mockToken);
    expect(spy).toHaveBeenCalledWith(['/login']);
  });

  it('should handle login error', () => {
    spyOn(authService, 'login').and.returnValue(throwError(() => new Error('Login failed')));
    spyOn(window, 'alert').and.callThrough();

    component.username = 'user';
    component.password = 'password';
    component.login();

    // Ensure the HTTP request is made
    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush('Login failed', { status: 401, statusText: 'Unauthorized' });

    expect(window.alert).toHaveBeenCalledWith('Invalid username or password');
  });
});
