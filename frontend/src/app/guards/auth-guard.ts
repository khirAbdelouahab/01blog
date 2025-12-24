import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  console.log('🔒 Auth Guard - Checking if user is logged in');
  
  if (authService.isLoggedIn()) {
    console.log('✅ User is logged in');
    return true;
  }

  console.log('❌ User not logged in, redirecting to login');
  router.navigate(['/login']);
  return false;
};
