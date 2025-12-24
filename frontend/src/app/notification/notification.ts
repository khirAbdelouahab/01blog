import { Component, Inject, Input, output, PLATFORM_ID } from '@angular/core';
import { NotificationData, NotificationService } from './notification.service';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';

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
  constructor(private router: Router, private notificationService: NotificationService, @Inject(PLATFORM_ID) private platformId: Object) { }
  get notificationInfo() { return this._notificationInfo; }
  private _notificationInfo: NotificationData | undefined;
  markAsRead(id: any) {
    if (isPlatformBrowser(this.platformId)) {
      const token = sessionStorage.getItem('authToken');
      if (!token) {
        return;
      }
      this.notificationService.markNotificationAsRead(token, id)
    }
  }

  onNotificationClick(id: any) {
    this.onClickEventHandler.emit();
    this.router.navigate(['post/view', id]);
  }
}
