import { CommonModule } from '@angular/common';
import { Component, computed, OnInit, signal } from '@angular/core';
import { ProfileService, UserDataResponse } from '../../profile/profile.service';
import { AdminPanelService } from '../admin-panel.service';
import { Response } from '../../auth';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog';
import { MatDialog } from '@angular/material/dialog';

interface UserLineData {
  userDataResponse: UserDataResponse,
  showMenu: boolean
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users.html',
  styleUrl: './users.css'
})

export class UsersComponent implements OnInit {
  users_array = signal<UserLineData[]>([]);
  usersList = computed(() => this.users_array());
  loading = signal(true);
  private isDeleting = false;

  constructor(private profileService: ProfileService,private router: Router, private adminService: AdminPanelService, private dialog: MatDialog) {
  }
  ngOnInit(): void {
    this.getAllUsers();
  }

  getAllUsers() {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.profileService.getUsers(token).subscribe({
      next: (response: UserDataResponse[]) => {
        if (response) {
          const arrayOfUsers: UserLineData[] = [];
          response.forEach((userData) => {
            const userLineData: UserLineData = {
              userDataResponse: userData,
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
      
        const arrayOfUsers: UserLineData[] = this.users_array().filter(user => {
          return user.userDataResponse.username != username;
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
      if (user.userDataResponse.id == id) {
        user.showMenu = !user.showMenu;
      } else {
        user.showMenu = false;
      }
      return user;
    }));
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

  onSearchDataChange(content: any) {
    if (this.isDeleting) {
      return;
    }
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.adminService.searchUsers(token, content).subscribe({
      next: (response: UserDataResponse[]) => {
        const arrayOfUsers: UserLineData[] = [];
        response.forEach((userData) => {
          const userLineData: UserLineData = {
            userDataResponse: userData,
            showMenu: false
          }
          arrayOfUsers.push(userLineData);
        })
        this.users_array.set(arrayOfUsers);

      },
      error: (err) => {
      }
    })
  }
}