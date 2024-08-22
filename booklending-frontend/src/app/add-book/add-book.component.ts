import {Component, EventEmitter, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Book, BooksService} from '../services/books.service';
import {NotificationService} from '../services/notification.service';

@Component({
  selector: 'app-add-book',
  standalone: true,
  templateUrl: './add-book.component.html',
  styleUrls: ['./add-book.component.scss'],
  imports: [ReactiveFormsModule]
})
export class AddBookComponent {
  addBookForm: FormGroup;

  @Output() bookAdded = new EventEmitter<void>();

  constructor(private fb: FormBuilder, private booksService: BooksService, private notificationService: NotificationService) {
    this.addBookForm = this.fb.group({
      title: ['', Validators.required],
      author: ['', Validators.required],
      coverUrl: ['', Validators.required],
      isbn: ['', Validators.required],
      status: ['available', Validators.required]
    });
  }

  onSubmit() {
    if (this.addBookForm.valid) {
      const newBook: Omit<Book, 'id'> = this.addBookForm.value; // Exclude `id` from new book submission
      this.booksService.addBook(newBook).subscribe({
        next: (book) => {
          this.notificationService.openDialog('Book added successfully!', false);
          this.addBookForm.reset({
            title: '',
            author: '',
            coverUrl: '',
            isbn: '',
            status: 'available'
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
}
