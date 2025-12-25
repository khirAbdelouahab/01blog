import { Component, OnInit } from '@angular/core';
import { PostsContainerComponent } from '../posts-container/posts-container';
import { ProfileService, UserDataResponse, UserProfileDataResponse } from './profile.service';
import { BehaviorSubject, Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PostService } from '../post/post-service';
import { AuthService, Response } from '../auth';
import { ReportDataRequest, ReportReason } from '../post/report-dialog/report-dialog.service';
import { ReportData, ReportDialogComponent } from '../post/report-dialog/report-dialog';
import { ToastService } from '../toast-component/toast.service';
import { MatDialog } from '@angular/material/dialog';
import { HttpErrorResponse } from '@angular/common/http';

enum AboutState {
  empty,
  update,
  content
}
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [PostsContainerComponent, CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class ProfileComponent implements OnInit {
  userDataResponse: UserDataResponse = {
    id: -1,
    username: "",
    fullname: "",
    email: "",
    avatar: "",
    role: "",
    about: "",
    state: ""
  };
  profileInfo$ = new BehaviorSubject<UserProfileDataResponse | undefined>(undefined);
  private destroy$ = new Subject<void>();
  private aboutState$ = new Subject<AboutState>();
  isLiked: boolean = false;
  state: string = '';
  username: string = '';
  isAdmin: boolean = false;
  isFollowing: boolean = false;

  constructor(private toastService:ToastService, private dialog: MatDialog, private profileService: ProfileService, private authService: AuthService, private postService: PostService, private router: Router, private route: ActivatedRoute) { }
  ngOnInit(): void {
    this.route.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        const username = params['username']; // Get username from route params
        if (username) {
          this.getProfile(username);
        } else {
          this.getMyProfile();
        }
      });
    this.aboutState$.subscribe(state => {
      this.state = this.convertAboutStateToString(state);
      //this.state = "content";

    })

    this.profileInfo$.subscribe(profile => {
      if (profile?.userDataResponse) {
        this.userDataResponse = profile.userDataResponse;
        this.isFollowing = profile.isSubscribedByMe;
      }
    })

    this.isAdmin = this.authService.hasRole('admin');
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  changeAboutState(state: AboutState) {
    this.aboutState$.next(state);
  }
  convertDataToState(data: string): AboutState {
    switch (data) {
      case '':
        return AboutState.empty;
      default:
        return AboutState.content;
    }
  }
  convertAboutStateToString(aboutStateCopy: AboutState): string {
    switch (aboutStateCopy) {
      case AboutState.empty:
        return "empty";
      case AboutState.update:
        return "update";
      default:
        return "content";
    }
  }

  getProfile(username: string) {
    const token = sessionStorage.getItem("authToken");
    if (!token || !username) {
      return;
    }
    this.profileService.getUserProfile(token, username).subscribe({
      next: (response: UserProfileDataResponse) => {
        this.profileInfo$.next(response || []);
        const s = this.convertDataToState(response.userDataResponse.about || '');
        this.changeAboutState(s);
      },
      error: (err: HttpErrorResponse) => {
        switch (err.status) {
          case 404:
            this.toastService.error("this user not found");
            this.router.navigate(['/home/feeds']);
            break;
          default:
            this.toastService.error("something happen wrong");
            this.router.navigate(['/home/feeds']);
            break;
        }
      }
    });
  }

  onReportUser(): void {
    if (!this.userDataResponse?.id) {
      return;
    }
    if (document.activeElement instanceof HTMLElement) {
      document.activeElement.blur();
    }
    setTimeout(() => {
      const dialogRef = this.dialog.open(ReportDialogComponent, {
        width: '500px',
        maxWidth: '90vw',
        data: { postId: this.userDataResponse!.id.toString() },
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
    if (!token ) {
      return;
    }
    const data: ReportDataRequest = {
      content: reportData.details,
      reportedId: this.userDataResponse.id,
      reason: this.convertReportReasonToEnum(reportData.reason)
    }
    this.profileService.reportUser(token, data).subscribe({
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
            this.toastService.error(err.error.message);
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

  getMyProfile() {
    const token = sessionStorage.getItem("authToken");
    if (!token) {
      return;
    }
    this.profileService.getConnectedUserProfile(token).subscribe({
      next: (response: UserProfileDataResponse) => {
        this.profileInfo$.next(response || []);
        const s = this.convertDataToState(response.userDataResponse.about || '');
        this.changeAboutState(s);
      },
      error: (err) => {
        
      }
    })
  }
  goToPostView(postId: number) {
    console.log('postID: ', postId);
    this.router.navigate(['post/view', postId]);
  }
  saveAbout(username: any) {
    const token = sessionStorage.getItem("authToken");
    if (!token) {
      return;
    }
    console.log(username);
    if (!this.userDataResponse) {
      return;
    }
    this.changeAboutState(AboutState.content);
    this.profileService.saveUserAboutToDataBase(token, this.userDataResponse).subscribe({
      next: (response: UserDataResponse) => {
        this.userDataResponse = response;
        console.log('response: ', response);
      },
      error: (err) => {
        console.error('error: ', err);
      }
    });
  }
  cancelEdit() {
    console.log('edit');
  }
  showEditForm() {
    this.aboutState$.next(AboutState.update);
  }
  editAbout() {
    this.changeAboutState(AboutState.update);
  }
  onFollow() {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.profileService.follow(token, this.userDataResponse.username).subscribe({
      next: (res) => {
        if (this.profileInfo$.value) {
          const profileData: UserProfileDataResponse = {
            userDataResponse: this.profileInfo$.value.userDataResponse,
            postDataResponse: this.profileInfo$.value.postDataResponse,
            isConnectedUser: this.profileInfo$.value.isConnectedUser,
            isSubscribedByMe: res.isFollower,
            profileFollowStats: this.profileInfo$.value.profileFollowStats
          }
          this.profileInfo$.next(profileData);
        }

      },
      error: (err) => {
        console.error('error: ', err);
      }
    });
  }

  onFileChange(event: any): void {
    const files = event.target.files;
    const selectedFiles: File[] = Array.from(files);
    const file = selectedFiles[0];
    const token = sessionStorage.getItem('authToken');
    if (!token || !file) {
      return;
    }
    this.profileService.updateProfileImage(token, file).subscribe({
      next: (response) => {
        console.log('response: ', response);
      },
      error: (err) => {
        console.error('error: ', err);
      }
    });
  }

  onLike(id: any) {
    this.isLiked = !this.isLiked;
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.postService.likePost(token, id).subscribe({
      next: (response) => {
        console.log('response : ', response);
      },
      error: (err) => {
        console.error('error : ', err);
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
      next: (res) => {
        console.log('res: ', res);
      },
      error: (err) => {
        console.error('error: ', err);
      }
    })
  }
}
