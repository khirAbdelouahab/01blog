import { CommonModule } from '@angular/common';
import { Component, computed, OnInit, signal } from '@angular/core';
import { ProfileService, UserDataResponse } from '../../profile/profile.service';
import { AdminPanelService } from '../admin-panel.service';
import { Response } from '../../auth';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog';
import { MatDialog } from '@angular/material/dialog';
import { ReportUserData } from '../../post/report-dialog/report-dialog.service';

interface UserLineData {
  userDataResponse: UserDataResponse,
  showMenu: boolean
}

interface UserReportLineData {
  report: ReportUserData,
  showMenu: boolean
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './userreports.html',
  styleUrl: './userreports.css'
})

export class UserReportsComponent implements OnInit {
  users_array = signal<UserReportLineData[]>([]);
  usersList = computed(() => this.users_array());
  loading = signal(true);
  private isDeleting = false;

  constructor(private profileService: ProfileService,private router: Router, private adminService: AdminPanelService, private dialog: MatDialog) {
  }
  ngOnInit(): void {
    this.getAllUsersReports();
  }

  getAllUsersReports() {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.profileService.getAllUsersReported(token).subscribe({
      next: (response: ReportUserData[]) => {
        if (response) {
          console.log('ReportUserData[]: ', response); 
          let arrayOfUsers: UserReportLineData[] = [];
          response.forEach((userData) => {
            console.log('report = ' , userData);
            
            const userLineData: UserReportLineData = {
              report: userData,
              showMenu: false
            }
            arrayOfUsers.push(userLineData);
          })
          this.users_array.set(arrayOfUsers);
          this.loading.set(false);
        } else {
          this.users_array.set([]);
        }
      },
      error: (err) => {
      }
    })
  }


  openConfirmationDialog() : Observable<boolean> {
    
        const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
          width: '400px',
          data: {
            title: 'Delete Post',
            message: 'Are you sure you want to delete this post?'
          }
        });
        return dialogRef.afterClosed();
      }
  removeUser(username: string) {
    if (this.isDeleting) {
      return;
    }
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.isDeleting = true;

    this.openConfirmationDialog().subscribe(result => {
    this.isDeleting = false;

      if (result) {
        this.adminService.deleteUser(token, username).subscribe({
        next: (response: Response) => {
      
        const arrayOfUsers: UserReportLineData[] = this.users_array().filter(report => {
          return report.report.reportedUser.username != username;
        })
        this.users_array.set(arrayOfUsers);
      },
      error: (err) => {
      }
    });
      }
    })
    
  }

  toggleDropdown(id: any) {
    
    if (this.isDeleting) {
      return;
    }



    this.usersList = computed(() => this.users_array().map((user) => {
      if (user.report.id == id) {
        user.showMenu = !user.showMenu;
      } else {
        user.showMenu = false;
      }
      return user;
    }));
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }
  banUser(user: any) {
    if (this.isDeleting) {
      return;
    }
    console.log('user: ', user);
    if (user.state == "banned") {
      user.state = "active";
    } else {
      user.state = "banned";
    }
    this.updateUserState(user);
  }
  updateUserState(userData: UserDataResponse) {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.profileService.updateUserState(token, userData).subscribe({
      next: (res) => {
        console.log('res: ', res);
      },
      error: (err) => {
      }
    })
  }

  viewProfile(username: string) {
    this.router.navigate(["/home/profile", username]);
  }
}