import { Component, Inject, Input, output, PLATFORM_ID } from '@angular/core';
import { NotificationData, NotificationService } from './notification.service';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastService } from '../toast-component/toast.service';
import { AuthService } from '../auth';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.html',
  styleUrl: './notification.css'
})

export class NotificationComponent {
  @Input() set notificationInfo(value: NotificationData | undefined) {
    this._notificationInfo = value;
  }
  onClickEventHandler = output<void>();
  constructor(private authService: AuthService, private toastService: ToastService, private router: Router, private notificationService: NotificationService, @Inject(PLATFORM_ID) private platformId: Object) { }
  get notificationInfo() { return this._notificationInfo; }
  private _notificationInfo: NotificationData | undefined;
  markAsRead(id: any) {
    if (isPlatformBrowser(this.platformId)) {
      const token = sessionStorage.getItem('authToken');
      if (!token) {
        return;
      }
      this.notificationService.markNotificationAsRead(token, id).subscribe({
        error: (err: HttpErrorResponse) => {
            switch (err.status) {
              case 401:
                this.toastService.error(err.error.message);
                this.authService.logout();
                break;
              case 400:
                this.toastService.error(err.error.message);
                break;
              case 403:
                this.toastService.error(err.error.message);
                break;
              case 404:
                this.toastService.error(err.error.message);
                break; 
              default:
                break;
            }  
        }
      })
    }
  }

  onNotificationClick(id: any) {
    this.onClickEventHandler.emit();
    this.router.navigate(['post/view', id]);
  }
}
