import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MediaUpload, MediaUploadData, MediaUploadService } from '../media-upload/media-upload-service';
import { UserDataResponse } from '../profile/profile.service';
import { CommentDataResponse } from '../comment/comment.service';
import { ToastService } from '../toast-component/toast.service';

export interface PostDataResponse {
  id: number;
  title: string;
  content: string;
  category: string;
  creationDate: string;
  author: UserDataResponse;
  state: string;
  mediaUploads: MediaUpload[];
  likes: number;
  comments: CommentDataResponse[];
  createdByMe: boolean;
}

export interface PostDataResponseView {
  post: PostDataResponse;
  isLikedByMe: boolean;
}

export interface PostData {
  title: string;
  content: string;
  category: string;
}

export interface PostInfo {
  postData: PostData;
  likes: number;
  comments: number;
  author: string;
  timeStamp: number;
}

export interface PostUpdateData {
  title: string;
  content: string;
  category: string;
  media: any[];
}

@Injectable({
  providedIn: 'root'
})

export class PostService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private toastService: ToastService, private http: HttpClient, private mediaUploadService: MediaUploadService) { }

  create(data: PostData, token: String): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.post<Response>(`${this.apiUrl}/posts/new`, data, options);
  }

  getConnectedUserPosts(token: string): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<any>(`${this.apiUrl}/posts`, options);
  }

  getTest(token: string): Observable<HttpEvent<any>> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      observe: 'events' as const,  // This is required for HttpEvent
      reportProgress: true
    };
    return this.http.get<any>(`${this.apiUrl}/test`, options);
  }

  createPost(postData: PostData, mediaContents: MediaUploadData[]): void {
    const token = sessionStorage.getItem('authToken');
    if (token === null) {
      return;
    }
    this.mediaUploadService.createPost(postData, mediaContents, token).subscribe({
      next: event => {
       if (event.type == HttpEventType.Response) {
         this.toastService.success("Post Created Succesfuly");
       }
      },
      error: (err: HttpErrorResponse) => {
        switch (err.status) {
          case 413:
            this.toastService.error(err.error.message);

            break;
          case 401:
            this.toastService.error(err.error.message);

            break;
          case 400:
            this.toastService.error(err.error.message);

            break;
          default:
            break;
        }
      }
    });
  }

  deletePost(PostId: any, token: string): Observable<Response> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
      }
    };
    return this.http.delete<Response>(`${this.apiUrl}/posts/delete/${PostId}`, options);
  }

  updatePost(postId: string, postData: PostData, mediaContents: MediaUploadData[]): void {
    const token = sessionStorage.getItem('authToken');
    if (token === null) {
      return;
    }
    this.mediaUploadService.updatePost(postId, postData, mediaContents, token).subscribe({
      next: event => {
        //console.log('eventType : ', event.type, '; progress : ', HttpEventType[event.type]);
      },
      error: (err: HttpErrorResponse) => {
        switch (err.status) {
          case 400:
            this.toastService.error(err.error.message);
            break;

          default:
            break;
        }
      }
    });
  }

  getTimeAgo(postDatestring: string): string {
    const postDate = new Date(postDatestring);
    const now = new Date();
    const diffInSeconds = Math.floor((now.getTime() - postDate.getTime()) / 1000);
    if (diffInSeconds < 60) {
      return `${diffInSeconds} seconds ago`;
    } else if (diffInSeconds < 3600) {
      const minutes = Math.floor(diffInSeconds / 60);
      return `${minutes} ${minutes === 1 ? 'minute' : 'minutes'} ago`;
    } else if (diffInSeconds < 86400) {
      const hours = Math.floor(diffInSeconds / 3600);
      return `${hours} ${hours === 1 ? 'hour' : 'hours'} ago`;
    } else {
      const days = Math.floor(diffInSeconds / 86400);
      return `${days} ${days === 1 ? 'day' : 'days'} ago`;
    }
  }

  likePost(token: string, postId: number): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.post<any>(`${this.apiUrl}/posts/${postId}/like`, {}, options);
  }

  findById(token: string, postId: number): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<any>(`${this.apiUrl}/posts/post/${postId}`, options);
  }

  findPostViewById(token: string, postId: number): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<any>(`${this.apiUrl}/posts/post/view/${postId}`, options);
  }

  getAllPosts(token: string): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<any>(`${this.apiUrl}/admin/posts`, options);
  }
}
