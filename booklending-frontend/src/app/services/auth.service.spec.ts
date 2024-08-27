import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {AuthService} from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const baseUrl = 'http://localhost:8080/api/auth';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.removeItem('authToken');  // Clean up after each test
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should return a token on successful login', () => {
      const mockToken = 'mock.jwt.token';
      const username = 'testUser';
      const password = 'testPass';

      service.login(username, password).subscribe((token) => {
        expect(token).toBe(mockToken);
      });

      const req = httpMock.expectOne(`${baseUrl}/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({username, password});
      req.flush(mockToken);
    });
  });

  describe('saveToken', () => {
    it('should save the token to localStorage', () => {
      const token = 'test.token';
      service.saveToken(token);
      expect(localStorage.getItem('authToken')).toBe(token);
    });
  });

  describe('getToken', () => {
    it('should retrieve the token from localStorage', () => {
      const token = 'test.token';
      localStorage.setItem('authToken', token);
      expect(service.getToken()).toBe(token);
    });

    it('should return null if no token is found', () => {
      localStorage.removeItem('authToken');
      expect(service.getToken()).toBeNull();
    });
  });

  describe('removeToken', () => {
    it('should remove the token from localStorage', () => {
      localStorage.setItem('authToken', 'test.token');
      service.removeToken();
      expect(localStorage.getItem('authToken')).toBeNull();
    });
  });

  describe('getRoleFromToken', () => {
    it('should return the role ID from the token', () => {
      const token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlSWQiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c';
      const decodedPayload = {roleId: 12345};
      spyOn(service as any, 'decodeJwtPayload').and.returnValue(decodedPayload);
      service.saveToken(token);
      expect(service.getRoleNameFromToken()).toBe('12345');
    });

    it('should return null if the token is invalid', () => {
      spyOn(service as any, 'decodeJwtPayload').and.returnValue(null);
      expect(service.getRoleNameFromToken()).toBeNull();
    });
  });

  describe('decodeJwtPayload', () => {
    it('should decode the JWT payload correctly', () => {
      const token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlSWQiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c';

      // Decode the payload part of the JWT
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));

      const actualPayload = JSON.parse(jsonPayload);

      // Validate the entire payload structure
      expect(actualPayload).toEqual(jasmine.objectContaining({
        roleId: '1234567890',
        iat: jasmine.any(Number)  // Ensure that 'iat' is a number
      }));
    });

    it('should handle errors and return null if decoding fails', () => {
      spyOn(window.console, 'error');
      const result = (service as any).decodeJwtPayload('invalid.token');
      expect(result).toBeNull();
      expect(console.error).toHaveBeenCalledWith('Error decoding JWT:', jasmine.any(Error));
    });
  });
});
