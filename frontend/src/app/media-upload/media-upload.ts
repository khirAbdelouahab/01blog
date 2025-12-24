import { Component, Input, output } from '@angular/core';
import { MediaUploadData, MediaUploadDataTransfer } from './media-upload-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-media-upload',
  standalone: true, // ← Add this!
  imports: [CommonModule, FormsModule],
  templateUrl: './media-upload.html',
  styleUrl: './media-upload.css'
})
export class MediaUploadComponent {
  isMenuOpen = false;
  isAddContent = false;
  content = '';
  @Input() media: MediaUploadData | undefined;
  onRemoveItemEvent = output<MediaUploadData>();
  onContentChangeEvent = output<MediaUploadDataTransfer>();
  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }
  onRemove(media: any) {
    this.isMenuOpen = false;
    this.onRemoveItemEvent.emit(media as MediaUploadData);
  }

  onAddContent() {
    if (this.media?.isExisting) {
      this.content = this.media.content;
    }
    this.isMenuOpen = false;
    this.isAddContent = true;
  }

  onContentChange(media: any) {
    const data: MediaUploadDataTransfer = {
      id: media.id,
      content: this.content
    }
    this.onContentChangeEvent.emit(data);
  }
}
