import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserDataResponse } from '../profile/profile.service';
import { Observable } from 'rxjs';
import { Response } from '../auth';
import { PostDataResponse } from '../post/post-service';


export enum PostState {
    NEW,
    HIDDEN,
    VISIBLE
}
@Injectable({
  providedIn: 'root'
})
export class AdminPanelService {
  private apiUrl = 'http://localhost:8080/api'
  constructor(private http: HttpClient) { }
  searchUsers(token: string, content: string): Observable<UserDataResponse[]> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
    };
    return this.http.get<UserDataResponse[]>(`${this.apiUrl}/admin/search/users/${content}`, options);
  }

  deleteUser(token: string, username: string): Observable<Response> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.delete<Response>(`${this.apiUrl}/admin/users/delete/${username}`, options);
  }

  deletePost(token: string, postId: number): Observable<Response> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.delete<Response>(`${this.apiUrl}/admin/posts/delete/${postId}`, options);
  }

  updatePostState(token : string, PostID: any, state: PostState) : Observable<PostDataResponse> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json' 
      }
    };
    return this.http.post<any>(`${this.apiUrl}/admin/post/${PostID}/update/state`, state, options);
  }
}
