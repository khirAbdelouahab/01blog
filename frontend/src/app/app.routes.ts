import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register';
import { HomeComponent } from './home/home';
import { ProfileComponent } from './profile/profile';
import { BlogContentView } from './blog-content-view/blog-content-view';
import { CreatePost } from './create-post/create-post';
import { PostsContainerComponent } from './posts-container/posts-container';
import { AdminPanel } from './admin-panel/admin-panel';
import { UsersComponent } from './admin-panel/users/users';
import { StatsComponent } from './admin-panel/stats/stats';
import { NotificationComponent } from './notification/notification';
import { adminGuard } from './guards/admin-guard';
import { ReportsComponent } from './admin-panel/reports/reports';
import { authGuard } from './guards/auth-guard';
import { OtherUsersComponent } from './other-users/other-users';
import { PostsComponent } from './admin-panel/posts/posts';
import { UnauthorizedComponent } from './unauthorized/unauthorized';
import { UserReportsComponent } from './admin-panel/userReports/userreports';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'unauthorized', component: UnauthorizedComponent },
  { path: 'login', component: LoginComponent }, // Add explicit login route
  { path: 'register', component: RegisterComponent },
  {
    path: 'home', component: HomeComponent, canActivate: [authGuard],
    children: [
      { path: '', component: PostsContainerComponent },
      { path: 'feeds', component: PostsContainerComponent, runGuardsAndResolvers: 'always'},
      { path: 'post/create', component: CreatePost },
      { path: 'post/create/:id', component: CreatePost },
      { path: 'notification', component: NotificationComponent },
      { path: 'profile/:username', component: ProfileComponent },
      { path: 'profile', component: ProfileComponent },

      { path: 'others', component: OtherUsersComponent },
      {
        path: 'admin', component: AdminPanel, canActivate: [adminGuard],
        children: [
          { path: '', component: StatsComponent },
          { path: 'users', component: UsersComponent },
          { path: 'reports', component: ReportsComponent },
          { path: 'posts', component: PostsComponent },
          { path: 'stats', component: StatsComponent },
          { path: 'userreports' , component:UserReportsComponent}
        ]
      },
    ]
  },
  { path: 'unauthorized', component: CreatePost },
  { path: 'post/view/:id', component: BlogContentView,  canActivate: [authGuard] },
  { path: '**', redirectTo: '', pathMatch: 'full' } // Redirect to login
];
