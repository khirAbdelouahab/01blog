import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PostDataResponse } from '../post/post-service';
import { ReportData } from '../post/report-dialog/report-dialog';
import { ReportDataRequest, ReportUserData } from '../post/report-dialog/report-dialog.service';


export interface UserProfileDataResponse {
  userDataResponse: UserDataResponse;
  postDataResponse: PostDataResponse[];
  profileFollowStats: ProfileFollowStats;
  isConnectedUser: boolean;
  isSubscribedByMe: boolean;
}

export interface ProfileFollowStats {
  followers: number;
  following: number;
}

export interface UserDataResponse {
  id: number;
  username: string;
  fullname: string;
  email: string;
  avatar: string;
  role: string;
  about: string;
  state: string;
}

export interface SuggestionUserCard {
  id: number;
  username: string;
  fullname: string;
  isSubscribedByMe: boolean;
}

export interface OtherUserData {
  id: number;
  username: string;
  fullname: string;
  isFollowedByMe: boolean;
  followStats: ProfileFollowStats;
}
@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = 'http://localhost:8080/api'

  constructor(private http: HttpClient) { }

  getOthers(token: string): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<any>(`${this.apiUrl}/others`, options);
  }

  reportUser(token: string, reportData: ReportDataRequest) {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.post<any>(`${this.apiUrl}/profile/report`, reportData , options);
  }


  getAllUsersReported(token: string):  Observable<ReportUserData[]>{
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<ReportUserData[]>(`${this.apiUrl}/profile/reports/find/all`, options);
  }
  
  follow(token: string, username: string): Observable<any> {

    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    const costumizeUrl = `${this.apiUrl}/follow/${username}`;
    return this.http.post<any>(costumizeUrl, {}, options);
  }

  getUserProfile(token: string, username: string): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    const costumizeUrl = `${this.apiUrl}/profile/info/${username}`;
    return this.http.get<any>(costumizeUrl, options);
  }

  getConnectedUserProfile(token: string): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    const costumizeUrl = `${this.apiUrl}/profile/info/me`;
    return this.http.get<any>(costumizeUrl, options);
  }

  saveUserAboutToDataBase(token: string, user: UserDataResponse): Observable<UserDataResponse> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.post<UserDataResponse>(`${this.apiUrl}/profile/info/about`, user, options);
  }

  updateProfileImage(token: string, file: File) {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
      }
    };
    const dataForm = new FormData();
    dataForm.append('file', file, file.name);
    return this.http.post<UserDataResponse>(`${this.apiUrl}/profile/info/image`, dataForm, options);
  }

  getUsers(token: string): Observable<UserDataResponse[]> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<UserDataResponse[]>(`${this.apiUrl}/admin/users`, options);
  }

  updateUserState(token: string, user: UserDataResponse): Observable<any> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };

    return this.http.post<any>(`${this.apiUrl}/admin/profile/info/state`, user, options);
  }
}
