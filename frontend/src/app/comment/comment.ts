import { Component, Input, output } from '@angular/core';
import { CommentDataResponse, CommentService } from './comment.service';
import { CommonModule } from '@angular/common';
import { PostService } from '../post/post-service';
import { Response } from '../auth';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastService } from '../toast-component/toast.service';

@Component({
  selector: 'app-comment',
  standalone:true,
  imports: [CommonModule],
  templateUrl: './comment.html',
  styleUrl: './comment.css'
})
export class CommentComponent {
  private _comment: CommentDataResponse | undefined;
  dateToString:string = '';
  @Input() set commentInfo(value: CommentDataResponse | undefined) {
    this._comment = value;
    if (this._comment) {
      this.dateToString = this.postService.getTimeAgo(this._comment?.creationDate.toString());
    }
  }
  onDeleteCommentEventHandler = output<number>();

  get commentInfo() { return this._comment; }
  constructor(private toastService: ToastService, private postService:PostService, private commentService: CommentService) {}

  onDelete(id: any) {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.commentService.deleteComment(token, id).subscribe({
      next: (response:Response) => {
        if (response.success) {
          this.toastService.success("comment deleted succesfuly");
          this.onDeleteCommentEventHandler.emit(id as number);
        }
      }, 
      error: (err: HttpErrorResponse) => {
        switch(err.status) {
          case 401 : 
            alert(err.error.message);
            break;
          case 404 : 
            alert("not found");
            break;
          default:
            break; 
        }
      }
    })
    
  }


}
