import { Injectable } from '@angular/core';
import { PostDataResponse } from '../post-service';
import { UserDataResponse } from '../../profile/profile.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';



export enum ReportReason {
  SpamOrMisleading,
  HarassmentOrBullying,
  HateSpeechOrSymbols,
  ViolenceOrDangerousContent,
  AdultOrSexualContent,
  CopyrightViolation,
  Other
}

export interface ReportPostData {
  id: number;
  content: string;
  post: PostDataResponse;
  author: UserDataResponse;
  created_at: Date;
  reason: ReportReason;
}

export interface ReportLineData {
  report:any;
  showMenu:boolean;
}

export interface ReportDataRequest {
  content:string;
  reportedId:number;
  reason:ReportReason; 
}
export interface ReportUserData {
  id: number;
  content: string;
  userReported: UserDataResponse;
  author: UserDataResponse;
  created_at: Date;
  reason: ReportReason;
}

@Injectable({
  providedIn: 'root'
})
export class ReportDialogService {
  private apiUrl = 'http://localhost:8080/api';
  constructor(private http: HttpClient) { }

  createPostReport(token: string, reportPostData: ReportDataRequest) : Observable<ReportPostData>{
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.post<ReportPostData>(`${this.apiUrl}/report/post/new`,reportPostData,options);
  }

  createUserReport(token: string, reportUserData: ReportUserData) : Observable<ReportUserData>{
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.post<ReportUserData>(`${this.apiUrl}/report/user/new`,reportUserData,options);
  }

  getReportsByPostId(token: string,postId:number) : Observable<ReportPostData[]> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<ReportPostData[]>(`${this.apiUrl}/report/find/${postId}`,options);
  }

  getReports(token: string) : Observable<ReportPostData[]> {
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    };
    return this.http.get<ReportPostData[]>(`${this.apiUrl}/report/find/all`,options);
  }

}
