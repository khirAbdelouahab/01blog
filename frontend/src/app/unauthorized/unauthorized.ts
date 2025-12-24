import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  imports: [],
  templateUrl: './unauthorized.html',
  styleUrl: './unauthorized.css'
})
export class UnauthorizedComponent {

  constructor(private router:Router) {}
  gologin() {
    this.router.navigate(['/']);
  }

  goHome() {
    this.router.navigate(['/home']);
  }
}
