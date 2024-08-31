import { Component, EventEmitter, HostListener, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BookDto, BookService } from '../services/book.service';
import { NotificationService } from '../services/notification.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-add-book',
  standalone: true,
  templateUrl: './add-book.component.html',
  styleUrls: ['./add-book.component.scss'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class AddBookComponent {
  addBookForm: FormGroup;

  @Output() bookAdded = new EventEmitter<void>();
  @Output() closeModal = new EventEmitter<void>();  // EventEmitter for closing the modal

  constructor(
    private fb: FormBuilder,
    private booksService: BookService,
    private notificationService: NotificationService
  ) {
    // Initialize the form with validators
    this.addBookForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      author: ['', [Validators.required, Validators.minLength(3)]],
      coverUrl: ['', [Validators.required, Validators.pattern('^(https?:\\/\\/)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(\\/\\S*)?$')]],
      isbn: ['', [Validators.required, Validators.pattern('^(?:\\d{9}X|\\d{10}|(978|979)\\d{10})$')]],
      status: ['AVAILABLE', Validators.required]
    });
  }

  // Listen for 'Escape' key press and emit closeModal event
  @HostListener('document:keydown.escape', ['$event'])
  onEscapePress(event: KeyboardEvent) {
    this.closeModal.emit(); // Emit the event to close the modal
  }

  // Method called when the form is submitted
  onSubmit() {
    if (this.addBookForm.valid) {
      const newBook: Omit<BookDto, 'id'> = this.addBookForm.value; // Exclude `id` from new book submission
      this.booksService.addBook(newBook).subscribe({
        next: (book) => {
          this.notificationService.openDialog('Book added successfully!', false);
          this.addBookForm.reset({
            title: '',
            author: '',
            coverUrl: '',
            isbn: '',
            status: 'AVAILABLE'
          });
          this.bookAdded.emit();
        },
        error: (err) => {
          this.notificationService.openDialog('Failed to add book. Please try again.', true);
        }
      });
    } else {
      this.notificationService.openDialog('Please fill out all fields correctly.', true);
    }
  }

  // Getters for each form control to simplify access in the template
  get title() {
    return this.addBookForm.get('title');
  }

  get author() {
    return this.addBookForm.get('author');
  }

  get isbn() {
    return this.addBookForm.get('isbn');
  }

  get coverUrl() {
    return this.addBookForm.get('coverUrl');
  }
}
