import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PostDataResponse } from '../post/post-service';
import { UserDataResponse } from '../profile/profile.service';
import { Response } from '../auth';

export interface NotificationData {
  id:number;
  content:string;
  read:boolean;
  post:PostDataResponse;
  reciever:UserDataResponse;
}
@Injectable({
  providedIn: 'root'
})

export class NotificationService {
  private apiUrl = 'http://localhost:8080/api';
  constructor(private http: HttpClient) { }

  getAllNotifications(token:string) : Observable<NotificationData[]> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<NotificationData[]>(`${this.apiUrl}/notification/me/all`,options);
  }
  

  getUnReadNotifCount(token:string) : Observable<number> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<number>(`${this.apiUrl}/notification/me/unread/count`, options);
  }

  markNotificationAsRead(token:string, id : any) : Observable<Response> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'

      }
    };
    return this.http.post<any>(`${this.apiUrl}/notification/asread/${id}`, {} ,options);
  }

  markAllNotificationsAsRead(token:string) : Observable<Response> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'

      }
    };
    return this.http.post<any>(`${this.apiUrl}/notification/asread/all`, {} , options);
  }
}
