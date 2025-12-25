import { Component, Input } from '@angular/core';
import { PostDataResponse, PostService } from './post-service';
import { CommonModule } from '@angular/common';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { ReportData, ReportDialogComponent } from './report-dialog/report-dialog';
import { MatDialog } from '@angular/material/dialog';
import { ReportDataRequest, ReportDialogService, ReportPostData, ReportReason } from './report-dialog/report-dialog.service';
import { Response } from '../auth';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastService } from '../toast-component/toast.service';

@Component({
  selector: 'app-post',
  imports: [CommonModule],
  templateUrl: './post.html',
  styleUrl: './post.css'
})

export class PostComponent {
  @Input() set postInfo(value: PostDataResponse | undefined) {
    this._postInfo = value;
    if (value) {
      this.likes$.next(value.likes); // Set initial value from postInfo
    }
  }
  get postInfo() { return this._postInfo; }
  private _postInfo: PostDataResponse | undefined;
  isLiked: boolean = false;
  likes$ = new BehaviorSubject<number>(0);
  constructor(private toastService:ToastService , private postService: PostService, private router: Router, private dialog: MatDialog, private reportService: ReportDialogService) {
  }
  goToPostView(id: number) {
    this.router.navigate(['post/view', id]);
  }
  onLike() {
    if (this._postInfo) {
      this.isLiked = !this.isLiked;
      const token = sessionStorage.getItem('authToken');
      if (!token) {
        return;
      }
      this.postService.likePost(token, this._postInfo.id).subscribe({
        next: (response) => {
          if (this._postInfo) {
            this.likes$.next(response?.likes);
          }
        },
        error: (err) => {
          console.error('error : ', err);
        }
      });
    }
  }

  onComment() {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.postService.getTest(token).subscribe({
      error: err => {
        console.error('error: ',err);   
      }
    })
  }

  goToProfile(username: any) {
    this.router.navigate(['/home/profile', username]);
  }

  onReport(): void {
    if (!this._postInfo?.id) {
      return;
    }
    if (document.activeElement instanceof HTMLElement) {
      document.activeElement.blur();
    }
    setTimeout(() => {
      const dialogRef = this.dialog.open(ReportDialogComponent, {
        width: '500px',
        maxWidth: '90vw',
        data: { postId: this._postInfo!.id.toString() },
        hasBackdrop: true,
        backdropClass: 'custom-backdrop', // Optional: for styling
        panelClass: 'custom-dialog-container',
        disableClose: false,
        autoFocus: true,
        restoreFocus: true
      });

      dialogRef.afterClosed().subscribe((result: ReportData) => {
        if (result) {
          if (result.details.length >= 200) {
            this.toastService.error("report reason is too long. max = 200 charachter");
            return;
          }
          this.submitReport(result);
        }
      });
    }, 50);
  }

  private submitReport(reportData: ReportData): void {
    const token = sessionStorage.getItem('authToken');
    if (!token || !this._postInfo) {
      return;
    }
    const data: ReportDataRequest = {
      content: reportData.details,
      reportedId: this._postInfo.id,
      reason: this.convertReportReasonToEnum(reportData.reason)
    }
    this.reportService.createPostReport(token, data).subscribe({
      next: (response: Response) => {
        if (response.success) {
            this.toastService.success("report submitted succesfuly");
        }
      },
     error: (err:HttpErrorResponse) => {
        switch (err.status) {
          case 400:
            this.toastService.error(err.error.message);
            break;
          case 403:
            this.toastService.error("403");
            break;
          default:
            break;
        }
      }
    });
  }

  private convertReportReasonToEnum(reason: string): ReportReason {
    switch (reason) {
      case 'hate_speech':
        return ReportReason.HateSpeechOrSymbols;
      case 'spam':
        return ReportReason.SpamOrMisleading;
      case 'adult_content':
        return ReportReason.AdultOrSexualContent;
      case 'copyright':
        return ReportReason.CopyrightViolation;
      case 'harassment':
        return ReportReason.HarassmentOrBullying;
      case 'violence':
        return ReportReason.ViolenceOrDangerousContent;
      case 'other':
        return ReportReason.Other;
      default:
        return ReportReason.Other;
    }
  }

 getTimeAgo(postDatestring: string): string {
  return this.postService.getTimeAgo(postDatestring);
 }
}
