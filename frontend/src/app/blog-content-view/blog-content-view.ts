import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostDataResponse, PostDataResponseView, PostService } from '../post/post-service';
import { BehaviorSubject, Observable } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommentData, CommentDataResponse, CommentService } from '../comment/comment.service';
import { ReportData, ReportDialogComponent } from '../post/report-dialog/report-dialog';
import { MatDialog } from '@angular/material/dialog';
import { ReportDataRequest, ReportDialogService, ReportPostData, ReportReason } from '../post/report-dialog/report-dialog.service';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog';
import { AuthService, Response } from '../auth';
import { ProfileService, UserDataResponse } from '../profile/profile.service';
import { AdminPanelService, PostState } from '../admin-panel/admin-panel.service';
import { CommentComponent } from '../comment/comment';
import { HttpErrorResponse } from '@angular/common/http';
import { HeaderComponent } from '../header/header';
import { ToastService } from '../toast-component/toast.service';

enum State {
  update,
  save
}

@Component({
  selector: 'app-blog-content-view',
  imports: [CommonModule, FormsModule, CommentComponent, HeaderComponent],
  templateUrl: './blog-content-view.html',
  styleUrl: './blog-content-view.css'
})

export class BlogContentView implements OnInit {
  isLoading = true;
  postId: string | null = null;
  post$ = new BehaviorSubject<PostDataResponse | undefined>(undefined);
  commentContent: string = '';
  postState: State = State.save;
  postLiked: boolean = false;
  isAdmin: boolean = false;
  isMenuOpened: boolean = false;
  constructor(private toastService: ToastService, private authService: AuthService, private postService: PostService, private router: Router, private commentService: CommentService, private route: ActivatedRoute, private dialog: MatDialog, private reportService: ReportDialogService, private profileService: ProfileService, private adminService: AdminPanelService, @Inject(PLATFORM_ID) private platformId: Object) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.postId = params['id'];
      this.getPostById();
      this.isAdmin = this.authService.hasRole('admin');

    });
  }

  onDeleteComment(event : any) {
     console.log('Received delete event for ID:', event);
    
    if (!this.post$.value) {
      return;
    }
    const newData: CommentDataResponse[]  = this.post$.value?.comments.filter(comment => {
      return comment.id != event;
    })

    let newPost = this.post$.value;
    newPost.comments = newData;

    this.post$.next(newPost);

  }
  changePostStateAction() {
    if (this.postState === State.save) {
      this.postState = State.update;
    } else {
      this.postState = State.save;
    }
  }

  toggleMenu() {
    this.isMenuOpened = !this.isMenuOpened;
  }

  ActivateUpdatePostState() {
    this.postState = State.update;
  }

  ActivateSavePostState() {
    this.postState = State.save;
  }

  onReport(id: any): void {
    if (document.activeElement instanceof HTMLElement) {
      document.activeElement.blur();
    }
    setTimeout(() => {
      const dialogRef = this.dialog.open(ReportDialogComponent, {
        width: '500px',
        maxWidth: '90vw',
        data: { postId: id },
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
          this.submitReport(id, result);
        }
      });
    }, 50);
  }

  private submitReport(id: any, reportData: ReportData): void {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      return;
    }
    const data: ReportDataRequest = {
      content: reportData.details,
      reportedId: id,
      reason: this.convertReportReasonToEnum(reportData.reason)
    }
    this.reportService.createPostReport(token, data).subscribe({
      next: (response: Response) => {
        this.toastService.success("report submitted succesfuly");
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

  goToProfile(username: any) {
    this.router.navigate(['/home/profile', username]);
  }

  onLike(id: any) {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.postLiked = !this.postLiked;
    this.postService.likePost(token, id).subscribe({
      next: (data) => {

        console.log('data: ', data);
      },
      error: (err) => {
        console.error('error: ', err);
      }
    })
  }
  onComment(id: any) {
    const token = sessionStorage.getItem("authToken");
    if (!token) {
      return;
    }
    const commentData: CommentData = {
      content: this.commentContent,
      postId: id
    }
    if (commentData.content.length >= 1000) {
      this.toastService.error("Comment 'Content' is too long");
      return;
    }
    this.commentService.saveComment(token, commentData).subscribe({
      next: (response: any) => {
        if (response.message) {
          return;
        }
        const comment: CommentDataResponse = response;
        if (!this.post$.value) {
          return;
        }
        let comments: CommentDataResponse[] = this.post$.value?.comments;
        comments.push(comment);
        const NewPostData: PostDataResponse = {...this.post$.value, comments : comments} 
        this.post$.next(NewPostData);
        this.commentContent = '';
        this.toastService.success("comment created succesfuly");
      },
      error: (err: HttpErrorResponse) => {
        switch (err.status) {
          case 404:
            this.toastService.error("not found");
            this.goToHome();
            break;
          case 400:
            this.toastService.error(err.error.message);
            this.goToHome();
            break;
          default:
            this.toastService.error("something wrong");
            this.goToHome();
            break;
        }
      }
    })
  }

  onCancel() {
    this.commentContent = '';
  }
  getPostById() {
    if (isPlatformBrowser(this.platformId)) {
      const token = sessionStorage.getItem('authToken');
      if (!token || !this.postId) {
        return;
      }
      const id = Number.parseInt(this.postId);
      this.postService.findPostViewById(token, id).subscribe({
        next: (response: PostDataResponseView) => {
          this.postLiked = response.isLikedByMe;
          this.post$.next(response.post);
          this.isLoading = false;
        },
        error: (err: HttpErrorResponse) => {
          switch (err.status) {
            case 403:
              alert('post is forbidden');
              this.goToHome();
              break;
            case 404:
              alert('post not found');
              this.goToHome();
              break;
            default:
              alert('Internal server');
              break;
          }
          console.error('error: ', err);
        }
      });
    }

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

  goToHome() {
    this.router.navigate(['home/feeds']);
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

  deletePost(postID: any) {
    console.log('deletePost triggered: ', postID);
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.openConfirmationDialog().subscribe(result => {
      if (result) {
        if (this.isAdmin) {
      this.deletePostByAdmin(token, postID);
    } else {
      this.postService.deletePost(postID, token).subscribe({
        next: (res) => {
          this.router.navigate(['/home/feeds']); 
        },
        error: (err) => {
        }
      });
    }
      }
    })
    
  }

  deletePostByAdmin(token: string, postID: any) {
    this.adminService.deletePost(token, postID).subscribe({
      next: (res) => {
        this.router.navigate(['/home/feeds']); 
      },
      error: (err) => {
      }
    });
  }

  hidePost(postID: any) {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.adminService.updatePostState(token, postID).subscribe({
      next: (updatedPost: PostDataResponse) => {
        alert(`post ${updatedPost.title} is HIDDEN NOW`);
      },
      error: (err) => {
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

  editPost(postID: any) {
    this.router.navigate(['/home/post/create', postID]);
  }

}
