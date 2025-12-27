import { Component, OnInit } from '@angular/core';
import { PostDataResponse, PostService } from '../post/post-service';
import { PostComponent } from '../post/post';
import { CommonModule } from '@angular/common';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-posts-container',
  imports: [PostComponent,CommonModule],
  templateUrl: './posts-container.html',
  styleUrl: './posts-container.css'
})

export class PostsContainerComponent implements OnInit {
  posts$ = new BehaviorSubject<PostDataResponse[]>([]);
  constructor(private postService: PostService, private router:Router){
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }
  ngOnInit(): void {
    this.getConnectedUserPosts();
  }

  getConnectedUserPosts() {
    const token: null | string = sessionStorage.getItem("authToken");
    if (token == null) {
      return;
    }
    this.postService.getConnectedUserPosts(token).subscribe({
      next:(response : PostDataResponse[]) => {
        console.log(response);
        
       this.posts$.next(response || []);
      },
      error : (err) => {
        console.error("error : ", err); 
      }
    });
  }
}
