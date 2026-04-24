import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Only navigate on browser side, not in SSR
      if (isPlatformBrowser(platformId)) {
        if (error.status === 401) {
          router.navigate(['/login']);
        }

        if (error.status === 403) {
          router.navigate(['/unauthorized']);
        }
      }

      return throwError(() => error);
    })
  );
};
