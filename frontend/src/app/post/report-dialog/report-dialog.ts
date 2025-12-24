import { Component, Inject, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule, MatDialog } from '@angular/material/dialog';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatRadioModule } from '@angular/material/radio';
import { MatButtonModule } from '@angular/material/button';
import { ConfirmationDialogComponent } from '../../confirmation-dialog/confirmation-dialog';

export interface ReportDialogData {
  postId: string;
}

export interface ReportData {
  reason: string;
  details: string;
}

@Component({
  selector: 'app-report-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatRadioModule,
    MatButtonModule
  ],
  templateUrl: './report-dialog.html',
  styleUrls: ['./report-dialog.css']
})
export class ReportDialogComponent implements AfterViewInit {
  @ViewChild('firstFocusable') firstFocusable?: ElementRef;

  selectedReason = new FormControl('', Validators.required);
  additionalDetails = new FormControl('');

  reportReasons = [
    { value: 'spam', label: 'Spam or misleading' },
    { value: 'harassment', label: 'Harassment or bullying' },
    { value: 'hate_speech', label: 'Hate speech or symbols' },
    { value: 'violence', label: 'Violence or dangerous content' },
    { value: 'adult_content', label: 'Adult or sexual content' },
    { value: 'copyright', label: 'Copyright violation' },
    { value: 'other', label: 'Other' }
  ];

  constructor(private dialog: MatDialog,
    public dialogRef: MatDialogRef<ReportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ReportDialogData
  ) { }

  isSubmitting: boolean = false;

  ngAfterViewInit(): void {
    // Manually focus the dialog title
    setTimeout(() => {
      this.firstFocusable?.nativeElement?.focus();
    }, 100);
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.selectedReason.invalid) {
      this.selectedReason.markAsTouched();
      return;
    }

    if (this.isSubmitting) return;
    this.isSubmitting = true;
    if (this.selectedReason.valid) {
      const reportData: ReportData = {
        reason: this.selectedReason.value!,
        details: this.additionalDetails.value || ''
      };
      const ConfirmationDialog = this.dialog.open(ConfirmationDialogComponent, {
        width: '400px',
        data: {
          title: 'Report Post',
          message: 'Are you sure you want to Submit this Report?'
        },
        disableClose: true,  // ← Prevents ESC key and backdrop click
        hasBackdrop: true,
        backdropClass: 'dark-backdrop'  // ← Makes backdrop darker
      });
      ConfirmationDialog.afterClosed().subscribe({
        next: (result: boolean) => {
           this.isSubmitting = false;
          if (result) {
            this.dialogRef.close(reportData);
          } else {
            this.dialogRef.close(null);
          }
        }
      })
    }
  }
}