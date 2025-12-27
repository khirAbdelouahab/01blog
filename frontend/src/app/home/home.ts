import { Component, Input } from '@angular/core';
import { PostsContainerComponent } from '../posts-container/posts-container';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ProfileComponent } from '../profile/profile';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth';
import { HeaderComponent } from '../header/header';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink,
    RouterLinkActive, PostsContainerComponent, ProfileComponent, HeaderComponent],
  templateUrl: './home.html',
  styleUrl: './home.css'
})

export class HomeComponent {
  username: string = '';
  sidebarVisible: boolean = false;
  constructor(private router: Router, private authService: AuthService) { }

  goToAdminPanel() {
    this.router.navigate(['/admin']);
  }

  toggleSidebar(event: any) {
    this.sidebarVisible = event;
  }
  isAdmin(): boolean {
    return this.authService.hasRole('admin');
  }

  logout() {
    console.log('logout');
    this.authService.logout();
  }

}
