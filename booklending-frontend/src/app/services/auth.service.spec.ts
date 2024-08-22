import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Ensure that no unmatched requests are left outstanding
  });


  it('should return a token on successful login', () => {
    const mockToken = 'mocked-jwt-token';
    const username = 'testuser';
    const password = 'password';

    service.login(username, password).subscribe(token => {
      expect(token).toBe(mockToken);
    });

    const req = httpMock.expectOne(`${service['baseUrl']}/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ username, password });

    req.flush(mockToken);  // Respond with the mock token
  });

  it('should save the token to localStorage', () => {
    const token = 'mocked-jwt-token';

    spyOn(localStorage, 'setItem');
    service.saveToken(token);

    expect(localStorage.setItem).toHaveBeenCalledWith('authToken', token);
  });

  it('should retrieve the token from localStorage', () => {
    const token = 'mocked-jwt-token';

    spyOn(localStorage, 'getItem').and.returnValue(token);
    const retrievedToken = service.getToken();

    expect(localStorage.getItem).toHaveBeenCalledWith('authToken');
    expect(retrievedToken).toBe(token);
  });

  it('should retrieve the token from localStorage', () => {
    const token = 'mocked-jwt-token';

    spyOn(localStorage, 'getItem').and.returnValue(token);
    const retrievedToken = service.getToken();

    expect(localStorage.getItem).toHaveBeenCalledWith('authToken');
    expect(retrievedToken).toBe(token);
  });

  it('should remove the token from localStorage', () => {
    spyOn(localStorage, 'removeItem');
    service.removeToken();

    expect(localStorage.removeItem).toHaveBeenCalledWith('authToken');
  });

});
