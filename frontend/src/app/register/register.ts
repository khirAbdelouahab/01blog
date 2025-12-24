import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService, RegisterRequest } from '../auth';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone:true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  username: string = '';
  email: string = '';
  password: string = '';
  confirmPassword:string = '';
  fullName:string = '';
  message: string = '';
  isLoading: boolean = true;
  isEmailValid: boolean = true;
  isPasswordStrong: boolean = true;
  isPasswordsAreEquals: boolean = true;
  isUserNameValid: boolean = true;
  isFullNameValid: boolean = true;

  constructor(private router: Router,private authService: AuthService) {}
   goToLogin() {
    this.router.navigate(['/login']);
  }

  isStrongPassword() : boolean {
    return true;
  }

  arePasswordsEquals() : boolean {
    return this.password === this.confirmPassword;
  }

  isAllInputsSucced() : boolean {
    if (!this.isStrongPassword()) {
      this.isPasswordStrong = false;
      this.message = 'password should contains : numbers , letters and symbols';
      return false;
    }
    if (!this.arePasswordsEquals()) {
      this.isPasswordsAreEquals = false;
      this.message = 'passwords are not equals';
      return false;
    }
    return true;
  }
  inputsValid() : boolean {
    return (this.username && this.password && this.confirmPassword && this.fullName && this.email)? true:false;
  }
    onSubmit() {
      if (!this.username || !this.password) {
        this.message = 'Please enter both username and password';
        return;
      }
      if (!this.isAllInputsSucced()) {
        return;
      }
      this.isLoading = true;
      const userDetails: RegisterRequest = {
        username: this.username,
        email:this.email,
        fullname:this.fullName,
        password: this.password
      };
  
      this.authService.register(userDetails).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.message = "Register successful!";
          if (response.success) {
            this.goToLogin();
          } else {
            console.log('Register failed!');
          }
        },
        error: (error:HttpErrorResponse) => {
          this.isLoading = false;
          alert(error.error.message);
        }
      });
    }

  togglePassword(feiled : string) {
    if (feiled == "password") {
      console.log(`password is ${this.password}`);
    } else {
      console.log(`password is ${this.confirmPassword}`);
    }
  }
}
