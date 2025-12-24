import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

import {  RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [CommonModule, RouterOutlet,   
    RouterLink,       
    RouterLinkActive],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.css'
})
export class AdminPanel {
  
}
