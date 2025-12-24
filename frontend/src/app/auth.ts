import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { NotificationService } from './notification/notification.service';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  fullname: string;
  username: string;
  email: string;
  password: string;
}

export interface Response {
  success: boolean;
  message: string;
}


interface JwtPayload {
  sub: string; // user id
  username: string;
  email: string;
  role: string;
  exp: number;
  notificationsCount:number;
}

interface ConnectedUser {
  username:string;
  role:string;
  notificationsCount:number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';
  private readonly TOKEN_KEY = 'authToken';
  private isBrowser: boolean;
  private currentUserSubject: BehaviorSubject<any>;
  public currentUserDetails: Observable<any>;

  constructor(private http: HttpClient, private router: Router, @Inject(PLATFORM_ID) platformId: Object,private notificationService:NotificationService) {
    this.isBrowser = isPlatformBrowser(platformId);
    const storedUser = null;
    this.currentUserSubject = new BehaviorSubject<any>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUserDetails = this.currentUserSubject.asObservable();
  }

  public get currentUserValue() {
    return this.currentUserSubject.value;
  }

  login(credentials: LoginRequest): Observable<any> {
    return this.http.post<Response>(`${this.apiUrl}/auth/login`, credentials);
  }

  saveUserDetails(userData: any, count:any) {
    const userDetails = {
      username: userData.username,
      role: userData.role,
      notificationsCount : count
    }
    localStorage.setItem('currentUser', JSON.stringify(userDetails));
    this.currentUserSubject.next(userDetails);
  }

  getNotificationsCount() : number {
    if (!sessionStorage) {
      return 0;
    }
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return 0;
    }
    let count : any = 0;
    this.notificationService.getUnReadNotifCount(token).subscribe({
      next: (value: any) => {
      count = value;
      },
      error: (err:any) => {
        console.error('error : ',err);
        count=0;
      }
    });
    return count;
  }
  register(userDetails: RegisterRequest): Observable<any> {
    return this.http.post<Response>(`${this.apiUrl}/auth/register`, userDetails);
  }


  hasRole(role: string): boolean {
    const userRole = this.getRole();
    return userRole === role;
  }

  getToken(): string | null {
    if (!this.isBrowser) {
      return null;
    }
    return sessionStorage.getItem('authToken');
  }
  private decodeToken(): JwtPayload | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      if (decoded.exp * 1000 < Date.now()) {
        this.logout();
        return null;
      }
      return decoded;
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  getRole(): string | null {
    const payload = this.decodeToken();
    return payload?.role || null;
  }

  getUsername(): string | null {
    const payload = this.decodeToken();
    return payload?.username || null;
  }

  getEmail(): string | null {
    const payload = this.decodeToken();
    return payload?.email || null;
  }

  getCurrentUser(): JwtPayload | null {
    return this.decodeToken();
  }

  getConnectedUser() : ConnectedUser | null {
    if (!this.isBrowser) {
      return null;
    }
    const connectedUser =  localStorage.getItem('currentUser');
    if (connectedUser == null) {
      return null;
    }
    const user : ConnectedUser = JSON.parse(connectedUser);
    return user;
  }

  updateConnectedUser(notificationCount: number): void {
    if (!this.isBrowser) {
      return;
    }
    const connectedUser =  localStorage.getItem('currentUser');
    if (connectedUser == null) {
      return;
    }
    
    let user : ConnectedUser = JSON.parse(connectedUser);
    user = {...user, notificationsCount: notificationCount};
    this.saveUserDetails(user, notificationCount);
  }

  getNCount() {
    const count = sessionStorage.getItem('notificationsCount');
    if(!count) {
      return 0;
    }
    return +count;
  }
  isLoggedIn(): boolean {
    return !!this.decodeToken();
  }

  logout(): void {
    sessionStorage.clear();
    localStorage.clear();
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }
}