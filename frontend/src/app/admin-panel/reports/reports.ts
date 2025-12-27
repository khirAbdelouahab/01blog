import { Component, computed, HostListener, OnInit, signal } from '@angular/core';
import { ReportDialogService, ReportLineData, ReportPostData } from '../../post/report-dialog/report-dialog.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AdminPanelService, PostState } from '../admin-panel.service';
import { Response } from '../../auth';
import { ProfileService, UserDataResponse } from '../../profile/profile.service';
import { PostDataResponse } from '../../post/post-service';
import { ToastService } from '../../toast-component/toast.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-reports',
  imports: [CommonModule],
  templateUrl: './reports.html',
  styleUrl: './reports.css'
})
export class ReportsComponent implements OnInit {
  PostState = PostState;
  
  // Or expose as readonly
  readonly postState = PostState;
  //users_array = signal<UserLineData[]>([]);
  reports_List = signal<ReportLineData[]>([]);
  reportsList = computed(() => this.reports_List());
  loading = signal(true);

  constructor(private dialog: MatDialog, private toastService: ToastService, private reportService: ReportDialogService, private adminService: AdminPanelService, private profileService: ProfileService, private router: Router) { }
  ngOnInit(): void {
    this.getAllReports();
  }
  

  getAllReports() {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.reportService.getReports(token).subscribe({
      next: (response: ReportPostData[]) => {
        console.log('reports: ', response);
        const reports: ReportLineData[] = [];
        response.forEach(report => {
          reports.push(
            {
              report: report,
              showMenu: false
            }
          )
        })
        this.reports_List.set(reports);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('error: ', err);
      }
    })
  }
  toggleDropdown(id: any) {
    this.reportsList = computed(() => this.reports_List().map((report) => {
      if (report.report.id == id) {
        report.showMenu = !report.showMenu;
      } else {
        report.showMenu = false;
      }
      return report;
    }));
  }

  onViewPost(postID: any) {
    this.router.navigate(['post/view', postID]);
  }
  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  // Close dropdown when clicking outside
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.actions-cell')) {
      this.reportsList = computed(() => this.reports_List().map((report) => {
        report.showMenu = false;
        return report;
      }));
    }
  }


  openConfirmationDialog() : Observable<boolean> {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete Post',
        message: 'Are you sure you want to delete this Post?'
          }
      });
      return dialogRef.afterClosed();
  }
  onDeletePost(postID: any) {
  const token = sessionStorage.getItem('authToken');
  if (!token) {
    return;
  }

  this.openConfirmationDialog().subscribe({
    next: (result) => {
      if (result) {
        this.adminService.deletePost(token, postID).subscribe({
          next: (response: Response) => {
            const updatedReports = this.reports_List().filter(
              reportLine => reportLine.report.post.id !== postID
            );
            this.reports_List.set(updatedReports);
            this.toastService.success("Post Deleted Successfully");
             
          },
          error: (err: HttpErrorResponse) => {
            switch (err.status) {
              case 404:
                this.toastService.error(err.error.message);
                break;
              default:
                this.toastService.error("Failed to delete post");
                break;
            }
          }
        });
      }
    },
    error: (err) => {
      this.toastService.error("Failed to open confirmation dialog");
    }
  });
}

  banUser(user: any) {
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
      next: (res: Response) => {
        if (res.success) {
          this.toastService.success("User State updated succesfuly");
          this.getAllReports();
        }
          
      },
      error: (err: HttpErrorResponse) => {
        switch (err.status) {
          case 403:
            this.toastService.error(err.error.message)
            break;
        
          default:
            if (err.error.message) {
              this.toastService.error(err.error.message);
            } else {
              this.toastService.error("something happen wrong");

            }
            break;
        }
      }
    })
  }

  updatePostState(postID: any) {
    console.log("post id = ", postID);
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.adminService.updatePostState(token, postID).subscribe({
      next: (updatedPost: PostDataResponse) => {
        this.toastService.success(`post ${updatedPost.title} is ${updatedPost.state} NOW`);
        this.getAllReports();
      },
      error: (err:HttpErrorResponse) => {
        switch (err.status) {
          case 403:
            if (err.error?.message) {
              this.toastService.error(err.error.message);
            } else {
              this.toastService.error("you can't do this operation");
            }
            break;
          default:
            if (err.error.message) {
              this.toastService.error(err.error.message);
            } else {
              this.toastService.error("something happen wrong");
            }
            break;
        }
      }
    });

    
  } 
}
