import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { NotificationComponent } from './notification/notification';
import { NotificationData, NotificationService } from './notification/notification.service';
import { BehaviorSubject } from 'rxjs';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MatButtonModule, NotificationComponent, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit, OnDestroy {
  title = 'auth-client';
  visibleNotifications: boolean = false;
  currentUser: any = null;
  notificationCount: number = 0;
  notifications$ = new BehaviorSubject<NotificationData[]>([]);
  constructor(private http: HttpClient, private cdr: ChangeDetectorRef, private router: Router, private authService: AuthService, private notificationService: NotificationService) {
  }
  ngOnDestroy(): void {
    console.log('appComponent is destroyed');
  }
  ngOnInit(): void {
    console.log('appComponent is loaded...');

    const User = this.authService.getConnectedUser();
    if (User) {
      console.log('USER = ', User);
      this.currentUser = User;
      this.notificationCount = User.notificationsCount;

    } else {
      this.authService.currentUserDetails.subscribe(data => {
        if (data) {
          this.currentUser = { ...data };
          this.notificationCount = data.notificationsCount;
        } else {
          this.currentUser = null;
          this.notificationCount = 0;
        }
      });
    }
    this.cdr.detectChanges();
  }
  openDialog_post() {
    console.log('navigate');
    this.router.navigate(['/home/post/create']);
  }
  goToHome() {
    this.router.navigate(['/home']);
  }
  goToProfile() {
    const username = this.authService.getCurrentUser()?.username;
    if (!username) {
      return;
    }
    this.router.navigate(['/home/profile', username]);
  }
  toggleNotifications() {
    this.visibleNotifications = !this.visibleNotifications;
    if (this.visibleNotifications) {
      this.getAllNotifications();
    }
  }
  getAllNotifications() {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.notificationService.getAllNotifications(token).subscribe({
      next: (res: NotificationData[]) => {
        console.log('notifications: ', res);
        this.notifications$.next(res);
      },
      error: (err) => {
        console.error('error: ', err);
      }
    });
  }
  logout(): void {
    sessionStorage.removeItem('authToken');
    this.router.navigate(['/login']);
  }

  markAllRead() {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.notificationService.markAllNotificationsAsRead(token).subscribe({
      next: (res) => {
        if (res.success) {
          this.authService.updateConnectedUser(0);
        }
      },
      error: (err) => {
        console.error('error: ', err);
      }
    })
  }
}
