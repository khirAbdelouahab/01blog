import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService, RegisterRequest } from '../auth';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastService } from '../toast-component/toast.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  username: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  fullName: string = '';
  isLoading: boolean = false;
  
  // Validation flags
  errors = {
    username: '',
    fullName: '',
    email: '',
    password: '',
    confirmPassword: ''
  };

  // Touch flags to show errors only after user interacts
  touched = {
    username: false,
    fullName: false,
    email: false,
    password: false,
    confirmPassword: false
  };

  constructor(
    private toastService: ToastService,
    private router: Router,
    private authService: AuthService
  ) {}

  goToLogin() {
    this.router.navigate(['/login']);
  }

  // Mark field as touched
  markAsTouched(field: keyof typeof this.touched) {
    this.touched[field] = true;
    this.validateField(field);
  }

  // Validate individual field
  validateField(field: keyof typeof this.errors) {
    switch (field) {
      case 'username':
        this.validateUsername();
        break;
      case 'fullName':
        this.validateFullName();
        break;
      case 'email':
        this.validateEmail();
        break;
      case 'password':
        this.validatePassword();
        break;
      case 'confirmPassword':
        this.validateConfirmPassword();
        break;
    }
  }

  // Username validation: 3-20 characters, alphanumeric and underscore only
  validateUsername(): boolean {
    if (!this.username) {
      this.errors.username = 'Username is required';
      return false;
    }
    if (this.username.length < 3) {
      this.errors.username = 'Username must be at least 3 characters';
      return false;
    }
    if (this.username.length > 20) {
      this.errors.username = 'Username cannot exceed 20 characters';
      return false;
    }
    if (!/^[a-zA-Z0-9_]+$/.test(this.username)) {
      this.errors.username = 'Username can only contain letters, numbers, and underscores';
      return false;
    }
    this.errors.username = '';
    return true;
  }

  // Full name validation: 2-30 characters, letters and spaces only
  validateFullName(): boolean {
    if (!this.fullName) {
      this.errors.fullName = 'Full name is required';
      return false;
    }
    if (this.fullName.length < 2) {
      this.errors.fullName = 'Full name must be at least 2 characters';
      return false;
    }
    if (this.fullName.length > 30) {
      this.errors.fullName = 'Full name cannot exceed 30 characters';
      return false;
    }
    if (!/^[a-zA-Z\s]+$/.test(this.fullName)) {
      this.errors.fullName = 'Full name can only contain letters and spaces';
      return false;
    }
    this.errors.fullName = '';
    return true;
  }

  // Email validation
  validateEmail(): boolean {
    if (!this.email) {
      this.errors.email = 'Email is required';
      return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.errors.email = 'Please enter a valid email address';
      return false;
    }
    if (this.email.length > 100) {
      this.errors.email = 'Email cannot exceed 100 characters';
      return false;
    }
    this.errors.email = '';
    return true;
  }

  // Password validation: min 8 chars, must contain uppercase, lowercase, and number
  validatePassword(): boolean {
    if (!this.password) {
      this.errors.password = 'Password is required';
      return false;
    }
    if (this.password.length < 8) {
      this.errors.password = 'Password must be at least 8 characters';
      return false;
    }
    if (!/(?=.*[a-z])/.test(this.password)) {
      this.errors.password = 'Password must contain at least one lowercase letter';
      return false;
    }
    if (!/(?=.*[A-Z])/.test(this.password)) {
      this.errors.password = 'Password must contain at least one uppercase letter';
      return false;
    }
    if (!/(?=.*\d)/.test(this.password)) {
      this.errors.password = 'Password must contain at least one number';
      return false;
    }
    this.errors.password = '';
    
    // Re-validate confirm password if it's been touched
    if (this.touched.confirmPassword) {
      this.validateConfirmPassword();
    }
    return true;
  }

  // Confirm password validation
  validateConfirmPassword(): boolean {
    if (!this.confirmPassword) {
      this.errors.confirmPassword = 'Please confirm your password';
      return false;
    }
    if (this.password !== this.confirmPassword) {
      this.errors.confirmPassword = 'Passwords do not match';
      return false;
    }
    this.errors.confirmPassword = '';
    return true;
  }

  // Check if all validations pass
  isFormValid(): boolean {
    return (
      this.validateUsername() &&
      this.validateFullName() &&
      this.validateEmail() &&
      this.validatePassword() &&
      this.validateConfirmPassword()
    );
  }

  // Check if form has any errors to display
  hasErrors(): boolean {
    return Object.values(this.errors).some(error => error !== '');
  }

  onSubmit() {
    // Mark all fields as touched
    Object.keys(this.touched).forEach(key => {
      this.touched[key as keyof typeof this.touched] = true;
    });

    // Validate all fields
    if (!this.isFormValid()) {
      this.toastService.warning('Please fix all validation errors');
      return;
    }

    this.isLoading = true;
    const userDetails: RegisterRequest = {
      username: this.username.trim(),
      email: this.email.trim().toLowerCase(),
      fullname: this.fullName.trim(),
      password: this.password
    };

    this.authService.register(userDetails).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.toastService.success('Registration successful! Please login.');
          this.goToLogin();
        } else {
          this.toastService.error('Registration failed. Please try again.');
        }
      },
      error: (error: HttpErrorResponse) => {
        this.isLoading = false;
        
        if (error.status === 400) {
          // Handle validation errors from backend
          if (error.error.errors) {
            // Multiple validation errors
            Object.keys(error.error.errors).forEach(key => {
              const errorKey = key as keyof typeof this.errors;
              if (errorKey in this.errors) {
                this.errors[errorKey] = error.error.errors[key];
              }
            });
            this.toastService.error('Please fix the validation errors');
          } else {
            // Single error message
            this.toastService.error(error.error.message || 'Invalid data provided');
          }
        } else if (error.status === 409) {
          // Conflict - username or email already exists
          this.toastService.error(error.error.message || 'Username or email already exists');
        } else if (error.status === 500) {
          this.toastService.error('Server error. Please try again later.');
        } else {
          this.toastService.error('Registration failed. Please try again.');
        }
      }
    });
  }

  // Toggle password visibility
  showPassword = false;
  showConfirmPassword = false;

  togglePasswordVisibility(field: 'password' | 'confirmPassword') {
    if (field === 'password') {
      this.showPassword = !this.showPassword;
    } else {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }

  // Get password strength for visual feedback
  getPasswordStrength(): 'weak' | 'medium' | 'strong' | '' {
    if (!this.password) return '';
    
    let strength = 0;
    if (this.password.length >= 8) strength++;
    if (this.password.length >= 12) strength++;
    if (/[a-z]/.test(this.password)) strength++;
    if (/[A-Z]/.test(this.password)) strength++;
    if (/\d/.test(this.password)) strength++;
    if (/[^a-zA-Z0-9]/.test(this.password)) strength++;

    if (strength <= 2) return 'weak';
    if (strength <= 4) return 'medium';
    return 'strong';
  }
}