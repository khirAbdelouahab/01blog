import { Component, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { PostData, PostDataResponse, PostService } from '../post/post-service';
import { MediaType, MediaUpload, MediaUploadData, MediaUploadDataTransfer, MediaUploadService } from '../media-upload/media-upload-service';
import { MediaUploadComponent } from '../media-upload/media-upload';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

export interface DialogData {
  title: string;
  content: string;
}


@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, FormsModule, MediaUploadComponent, CommonModule],
  providers: [PostService, MediaUploadService],  // ← Add this
  templateUrl: './create-post.html',
  styleUrl: './create-post.css'
})
export class CreatePost implements OnInit {
  isMenuOpen = false;
  isUpdateMode: boolean = false;
  mediaUpload: Array<File> = [];
  postData: PostData = {
    title: '',
    content: '',
    category: ''
  };
  postId: string = '';
  loading = signal(true);
  mediaUploaded: MediaUploadData[] = [];
  medias: MediaUpload[] = [];
  mediaContents: MediaUploadDataTransfer[] = [];
  selectedFiles: File[] = [];
  previewUrl: any;
  constructor(private postService: PostService, private router: Router, private route: ActivatedRoute) {
  }
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const pId = params['id'];
      if (pId) {
        this.isUpdateMode = true;
        this.getPostById(pId);
      } else {
        this.loading.set(false);
      }
    });
  }

  //http://localhost:4200/1ad9a375-bea8-46eb-a8f7-dad44c602932
  //
  onFileChange(event: any): void {  
    const files = event.target.files;
    this.selectedFiles = Array.from(files);
    const file = this.selectedFiles[0];
    this.mediaUpload.push(file);
    this.previewUrl = URL.createObjectURL(file);
    const dataUploaded: MediaUploadDataTransfer = {
      id: this.previewUrl,
      content: ''
    }
    this.mediaContents.push(dataUploaded);
    const fileType = file.type.startsWith('image/') ? 'Image' : 'Video';
    file.type.startsWith('video/') ? 'video' : 'unknown';
    const mediaId : any = Date.now(); 
    this.mediaUploaded.push(
      {
        id: mediaId,
        url: this.previewUrl,
        type: fileType,
        isExisting: false,
        file:file,
        content: ''
      }
    );
    this.medias.push(
      {
        id: mediaId,
        type: fileType == 'Image' ? MediaType.Image : MediaType.Video,
        content: ''
      }
    )
    event.target.value = '';
    this.updateFilePreview();
  }

  loadMediaUploaded() {

  }

  onRemoveItem(media: MediaUploadData) {
    console.log('media removed:', media);
    console.log('media list:', this.mediaUploaded);
    this.mediaUploaded = this.mediaUploaded.filter((m) => {
      if (media.isExisting) {
        return media.id !== m.id;
      }else {
        return media.file !== m.file;
      }
    })
  }

  updateFilePreview(): void {
    const fileList = document.getElementById('fileList');
    if (fileList) {
      fileList.innerHTML = '';
      this.selectedFiles.forEach(file => {
        const fileItem = document.createElement('div');
        fileItem.textContent = file.name;
        fileItem.className = 'file-item';
        fileList.appendChild(fileItem);
      });
    }
  }
  onCancel(): void {
    this.router.navigate(['/home']);
  }

  onPublish(): void {
    if (!this.postData.title || !this.postData.content || !this.postData.category) {
      alert('Please fill in all required fields');
      return;
    }
   
    this.mediaUploaded = this.mediaUploaded.map((media) => {
      if(!media.isExisting) {
        media.id = -1;
      } 
      return media;
    })
    
    if (this.isUpdateMode) {
      this.postService.updatePost(this.postId, this.postData, this.mediaUploaded);
    } else {
      this.postService.createPost(this.postData, this.mediaUploaded);
    }
    this.goToHome();
  }

  goToHome() {
    this.router.navigate(['/home/feeds']);
  }

  onMediaChangeContent(media: MediaUploadDataTransfer) {
    this.mediaUploaded.forEach((m) => {
      if (m.id === media.id) {
        m.content = media.content;
      }
    })
  }

  getPostById(id: any) {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      return;
    }
    this.postService.findById(token, id as number).subscribe({
      next: (post: PostDataResponse) => {
        this.postId = post.id.toString();
        this.postData.title = post.title;
        this.postData.content = post.content;
        this.postData.category = post.category;
        this.medias = post.mediaUploads.map(media => ({
          id: media.id,
          content: media.content,
          type: media.type
        }))
        this.mediaUploaded = post.mediaUploads.map(media => ({
          id: media.id,
          url: `http://localhost:8080/api/media/file/${media.id}`,
          type: media.type.toString(),
          content: media.content,
          file:null,
          isExisting: true
        }))
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(true);
      }
    });
  }
}
