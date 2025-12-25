import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { PostDataResponse, PostService } from '../../post/post-service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AdminPanelService, PostState } from '../admin-panel.service';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../../toast-component/toast.service';


interface PostLineData {
  data: PostDataResponse;
  showMenu: boolean;
}
@Component({
  selector: 'app-posts',
  imports: [CommonModule],
  templateUrl: './posts.html',
  styleUrl: './posts.css'
})
export class PostsComponent implements OnInit {

  posts$ = new BehaviorSubject<PostLineData[] | []>([]);
  private isDeleting = false;
  constructor(private toastService: ToastService, private postService: PostService, private dialog: MatDialog, private adminService: AdminPanelService, private router: Router, private cdn: ChangeDetectorRef) { }
  ngOnInit(): void {
    this.getPosts();
  }
  toggleDropdown(id: any) {
    const newArray = this.posts$.value.map((p) => {
      if (p.data.id == id) {
        p.showMenu = !p.showMenu;
      }
      return p;
    })
    this.posts$.next(newArray);
  }

  getPosts() {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }

    this.postService.getAllPosts(token).subscribe({
      next: (list: PostDataResponse[]) => {
        const array: PostLineData[] = list.map((post) => {
          return { data: post, showMenu: false };
        })
        this.posts$.next(array);
      },
      error: (err: HttpErrorResponse) => {
        switch (err.status) {
          case 403:

            break;
          default:
            break;
        }
      }
    })
  }

  changePostState(post: PostDataResponse) {
    if (this.isDeleting) {
      return;
    }
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.adminService.updatePostState(token, post.id).subscribe({
      next: (updatedPost: PostDataResponse) => {
        this.toastService.success('Post Updated successfully!');
        const newArray = this.posts$.value.map((p) => {
          if (p.data.id == updatedPost.id) {
            p.data = updatedPost;
          }
          return p;
        })
        this.posts$.next(newArray);
      },
      error: (err) => {
        console.error('error: ', err);
      }
    });
  }

  openConfirmationDialog(): Observable<boolean> {
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
        this.adminService.deletePost(token, postID).subscribe({
          next: (res) => {
            this.toastService.success('Post Deleted successfully!');
            const array: PostLineData[] = this.posts$.value.filter(p => {
              return p.data.id !== postID;
            })
            this.posts$.next(array);

          },
          error: (err: HttpErrorResponse) => {
            console.log('err: ');
            switch (err.status) {
              case 403:
                this.router.navigate(['/unauthorized']);
                break;
              default:
                break;
            }
          }
        });
      }
    })

  }

  goToPostVeiw(postId: any) {
    if (this.isDeleting) {
      return;
    }
    this.router.navigate(['post/view', postId]);
  }
}
