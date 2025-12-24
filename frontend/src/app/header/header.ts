import { ChangeDetectorRef, Component, OnInit, output } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { NotificationData, NotificationService } from '../notification/notification.service';
import { AuthService, Response } from '../auth';
import { CommonModule } from '@angular/common';
import { NotificationComponent } from '../notification/notification';

@Component({
  selector: 'app-header',
  imports: [CommonModule, NotificationComponent],
  templateUrl: './header.html',
  styleUrl: './header.css'
})

export class HeaderComponent implements OnInit {
  visibleNotifications: boolean = false;
  notificationCount: number = 0;
  currentUser: any = null;
  notifications$ = new BehaviorSubject<NotificationData[]>([]);
  username: string = '';
  sidebarVisible = false;
  onToggleMenuEventHandler = output<boolean>();


  constructor(private router: Router, private authService: AuthService, private notificationService: NotificationService, private cdr: ChangeDetectorRef) { }
  ngOnInit(): void {
    const User = this.authService.getConnectedUser();

    if (User) {
      this.username = User.username;
      console.log('USER = ', User);
      this.currentUser = User;
      this.notificationCount = User.notificationsCount;

    } else {
      this.authService.currentUserDetails.subscribe(data => {
        console.log('data: ', data);
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

  toggleSidebar() {
    this.sidebarVisible = !this.sidebarVisible;
    this.onToggleMenuEventHandler.emit(this.sidebarVisible);
  }
  openDialog_post() {
    this.router.navigate(['/home/post/create']);
  }

  goToHome() {
    this.router.navigate(['/home']);
  }
  goToMyProfile() {
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
        this.notifications$.next(res);
      },
      error: (err: any) => {
      }
    });
  }

  markAllRead() {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.notificationService.markAllNotificationsAsRead(token).subscribe({
      next: (res: Response) => {
        if (res.success) {
          this.authService.updateConnectedUser(0);
        }
      },
      error: (err: any) => {
      }
    })
  }
}
