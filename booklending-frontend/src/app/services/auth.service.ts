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

  getRoleNameFromToken(): string | null {
    const token = this.getToken();
    if (token) {
      const decodedPayload = this.decodeJwtPayload(token);
      return decodedPayload?.roleName || null;
    }
    return null;
  }

  private decodeJwtPayload(token: string): any {
    try {
      const base64Url = token.split('.')[1];  // Extract the payload part of the JWT
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');  // Replace URL-safe characters
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Error decoding JWT:', error);
      return null;
    }
  }
}
