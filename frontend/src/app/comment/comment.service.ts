import { Injectable } from '@angular/core';
import { UserDataResponse } from '../profile/profile.service';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';


export interface CommentDataResponse {
  id: number;
  content: string;
  creationDate: Date;
  author: UserDataResponse;
  isCreatedByConnectedUser:boolean;
}

export interface CommentData {
  content: string;
  postId: number;
}

@Injectable({
  providedIn: 'root'
})

export class CommentService {
  private apiUrl = 'http://localhost:8080/api';
  constructor(private http: HttpClient){}
  saveComment(token: string, data: CommentData): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.post<CommentData>(`${this.apiUrl}/comment/new`,data,options);
  }

  deleteComment(token: string, id: number): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.delete<any>(`${this.apiUrl}/comment/delete/${id}`, options);
  }
}
