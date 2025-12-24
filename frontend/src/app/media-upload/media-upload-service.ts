import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PostData } from '../post/post-service';
import { Observable } from 'rxjs';



export interface MediaUploadData {
  id: number;
  url: string;
  type: string;
  content:string;
  file:File | null,
  isExisting: boolean;
}

export enum MediaType {
  Image,
  Video
}

export interface MediaUpload {
  id: number;
  content: string;
  type: MediaType
}

export interface MediaUploadDataTransfer {
  id: number;
  content: string;
}

@Injectable({
  providedIn: 'root'
})

export class MediaUploadService {
  private apiUrl = 'http://localhost:8080/api';


  constructor(private http: HttpClient) { }

  createPost(postData: PostData, mediaContents: MediaUploadData[], token: string): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('title', postData.title);
    formData.append('content', postData.content);
    formData.append('category', postData.category);
const mediaContentsArray: string[] = [];
    mediaContents.forEach((m) => {
      
      if (m.file != null) {
        formData.append('files', m.file, m.file.name);
      }
      mediaContentsArray.push(m.content);
    })
    formData.append('mediaContents', JSON.stringify(mediaContentsArray));
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      observe: 'events' as const,
      reportProgress: true
    };
    return this.http.post<any>(`${this.apiUrl}/posts/new`, formData, options);
  }

  updatePost(postID: string, postData: PostData, mediaContents: MediaUploadData[], token: string): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('title', postData.title);
    formData.append('content', postData.content);
    formData.append('category', postData.category);
    formData.append('id', postID);
    mediaContents.forEach((m) => {
      if (m.file != null) {
        formData.append('files', m.file, m.file.name);
      }
    })
    const mediaUpload = this.convertArray(mediaContents);
    formData.append('mediaJson', JSON.stringify(mediaUpload));
    const options = {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      observe: 'events' as const,
      reportProgress: true
    };
    return this.http.post<any>(`${this.apiUrl}/posts/update`, formData, options);
  }


  convert(mediaUploadData: MediaUploadData) : MediaUpload {
    return {
      id : mediaUploadData.id,
      content:mediaUploadData.content,
      type:mediaUploadData.type == 'Image' ? MediaType.Image:MediaType.Video
    }
  }

  convertArray(mediaUpload: MediaUploadData[]) : MediaUpload[] {
    let list: MediaUpload[] = []
    mediaUpload.map((media) => {
      list.push(this.convert(media));
    })
    return list;
  }
}
