import {HttpInterceptorFn} from '@angular/common/http';
import {AuthService} from '../services/auth.service';
import {inject} from "@angular/core";


export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);  // Inject AuthService
  const token = authService.getToken();  // Retrieve token from AuthService

  if (token) {
    // Clone the request and add the Authorization header
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedReq);
  }

  return next(req);
};
