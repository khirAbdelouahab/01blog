import { Component, OnInit } from '@angular/core';
import { OtherUserData, ProfileService } from '../profile/profile.service';
import { BehaviorSubject } from 'rxjs';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastService } from '../toast-component/toast.service';
import { AuthService } from '../auth';

@Component({
  selector: 'app-other-users',
  standalone:true,
  imports: [CommonModule],
  templateUrl: './other-users.html',
  styleUrl: './other-users.css'
})
export class OtherUsersComponent implements OnInit{
  
  otherUsers$ = new BehaviorSubject<OtherUserData[] | []>([]);
  constructor(private toastService:ToastService, private authService: AuthService, private profileService: ProfileService, private router: Router){}
  ngOnInit(): void {
    this.getOtherUsers();
  }

  getOtherUsers() {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.profileService.getOthers(token).subscribe({
      next: (res:OtherUserData[]) => {
          this.otherUsers$.next(res);
      },
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

  toggleFollow(username: any) {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.profileService.follow(token,username).subscribe({
      next: (subscribe) => {
        const newOtherUsers = this.otherUsers$.value.map((user) => {
          if (user.username == username) {
            user.isFollowedByMe = subscribe.isFollower;
            user.followStats.followers += subscribe.isFollower? 1 : -1; 
          }
          return user;
        })
        this.otherUsers$.next(newOtherUsers);
      },
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
    });
  }


  goToProfile(username: any) {
    this.router.navigate(['/home/profile', username]);
  }
}
