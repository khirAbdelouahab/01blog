import { Component, computed, OnInit, signal } from '@angular/core';
import { ReportDialogService, ReportLineData, ReportPostData } from '../../post/report-dialog/report-dialog.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AdminPanelService, PostState } from '../admin-panel.service';
import { Response } from '../../auth';
import { ProfileService, UserDataResponse } from '../../profile/profile.service';
import { PostDataResponse } from '../../post/post-service';

@Component({
  selector: 'app-reports',
  imports: [CommonModule],
  templateUrl: './reports.html',
  styleUrl: './reports.css'
})
export class ReportsComponent implements OnInit {
  //users_array = signal<UserLineData[]>([]);
  reports_List = signal<ReportLineData[]>([]);
  reportsList = computed(() => this.reports_List());
  loading = signal(true);

  constructor(private reportService: ReportDialogService, private adminService: AdminPanelService, private profileService: ProfileService, private router: Router) { }
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

  onDeletePost(postID: any) {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.adminService.deletePost(token, postID).subscribe({
      next: (response: Response) => {
        console.log('response: ', response);
      },
      error: (err) => {
        console.error('error: ', err);

      }
    });
  }

  banUser(user: any) {
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
        console.error('error: ', err);
      }
    })
  }

  hidePost(postID: any) {
      const token = sessionStorage.getItem('authToken');
      if (!token) {
        return;
      }
      this.adminService.updatePostState(token, postID, PostState.HIDDEN).subscribe({
        next: (updatedPost: PostDataResponse) => {
          alert(`post ${updatedPost.title} is HIDDEN NOW`);
        },
        error: (err) => {
          console.error('error: ', err);
        }
      });
    }
  
}
