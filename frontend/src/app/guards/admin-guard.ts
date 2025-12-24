import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth';
import { isPlatformBrowser } from '@angular/common';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

 // Check if user is admin (this should work on both server and browser)
  const isAdmin = authService.hasRole('admin');
  if (!isAdmin) {
    if (isPlatformBrowser(platformId)) {
      // Browser: can use normal navigation
      
      router.navigate(['/unauthorized']);
    } else {
      // Server: return UrlTree for SSR-safe redirect
      return router.parseUrl('/unauthorized');
    }
    return false;
  }

  return true;
};
