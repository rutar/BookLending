import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8080/api/auth';  // Adjust to your backend URL

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/login`, { username, password }, { responseType: 'text' });
  }

  saveToken(token: string) {
    localStorage.setItem('authToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  removeToken() {
    localStorage.removeItem('authToken');
  }
}
