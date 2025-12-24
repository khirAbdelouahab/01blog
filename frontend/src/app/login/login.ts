import { Component, Inject, OnDestroy, OnInit, PLATFORM_ID, signal } from '@angular/core';

import { AuthService, LoginRequest } from '../auth';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent  implements OnInit,OnDestroy {
  username: string = '';
  password: string = '';
  message: string = '';
  isLoading: boolean = false;
  isBanned = signal(false);
  isInvalidCredintials = signal(false);

  messageEroor:string = '';
  private isBrowser: boolean;
  isUsernNameFaild: boolean = false;
  isPasswordFaild: boolean = false;
  private readonly TOKEN_KEY = 'authToken';
  constructor(private router: Router, private authService: AuthService,@Inject(PLATFORM_ID) platformId: Object) {
     this.isBrowser = isPlatformBrowser(platformId);
  }
  ngOnDestroy(): void {
    console.log('LoginComponent is destroyed');
  }
  
  ngOnInit(): void {
    if (this.isBrowser) {
      sessionStorage.clear();
      localStorage.clear();
    }
  }
  
  goToRegister() {
    this.router.navigate(['/register']);
  }
  
  goToHome() {
    this.router.navigate(['/home']);
  }
  
  onSubmit() {
    if (!this.username || !this.password) {
      this.message = 'Please enter both username and password';
      return;
    }

    this.isLoading = true;
    const credentials: LoginRequest = {
      username: this.username,
      password: this.password
    };

    this.authService.login(credentials).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.message = "Login successful!";
        console.log('yeah');
        
        if (response.token) {
          this.saveToken(response.token);
          this.authService.saveUserDetails(response.userConnected,response.notificationsCount);
          this.goToHome();
        } else {
          this.isInvalidCredintials.set(true);
          this.messageEroor = response.message;
          this.isLoading = false;
        }
      },
      error: (error: HttpErrorResponse) => {

        switch (error.status) {
          case 403:
            this.isBanned.set(true);
            this.messageEroor = error.error.message;
            break;
          case 401:
            this.isInvalidCredintials.set(true);
            this.messageEroor = error.error.message;
            break;
          default:
            break;
        }
        this.isLoading = false;
        this.message = 'An error occurred during login';
      }
    });
  }

  saveToken(token: string) {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

}