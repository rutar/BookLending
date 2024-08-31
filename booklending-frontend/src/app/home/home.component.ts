import {CommonModule, NgForOf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {Component} from '@angular/core';
import {NotificationService} from '../services/notification.service';
import {BookDto, BookService, BookStatus} from "../services/book.service";
import {AuthService} from '../services/auth.service';

@Component({
  selector: 'app-book-borrowing',
  templateUrl: './home.component.html',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule,
    CommonModule
  ],
  styleUrls: ['./home.component.scss']
})

export class HomeComponent {
  searchQuery: string = '';
  books: BookDto[] = [];
  filteredBooks: BookDto[] = [];

  constructor(private authService: AuthService, private bookService: BookService, private notificationService: NotificationService) {
    this.fetchBooks();
  }

  ngOnInit(): void {
    this.fetchBooks();
  }

  // Fetch books with server-side filtering and sorting
  fetchBooks(): void {
    this.bookService.getBooks(this.searchQuery, 'title', 'asc').subscribe(
      (books) => {
        this.books = books;
      },
      (error) => {
        console.error('Error fetching books:', error);
      }
    );
  }


  // This method will be called whenever the search input changes
  onSearch(): void {
    this.fetchBooks();
  }

  reserveBook(bookId: number) {
    this.bookService.reserveBook(this.authService.getUsername(), bookId).subscribe({
      next: () => {
      this.notificationService.openDialog("Book reserved successfully!", false);
      this.fetchBooks();
    },
      error: (err) => {
        if (err.status === 404) {
          this.notificationService.openDialog("Book not found.", true);
        } else {
          this.notificationService.openDialog("An error occurred. Please try again later.", true);
        }
      }
    });
  }

  cancelReservation(bookId: number) {
    this.bookService.cancelReservation(this.authService.getUsername(), bookId).subscribe({
      next: () => {
        this.notificationService.openDialog("Reservation cancelled successfully!", false);
        this.fetchBooks();
      },
      error: (err) => {
        if (err.status === 404) {
          this.notificationService.openDialog("Reservation not found. It may have already been cancelled.", true);
        } else {
          this.notificationService.openDialog("An error occurred. Please try again later.", true);
        }
      }
    });
  }


  markAsReceived(bookId: number) {
    this.bookService.markAsReceived(this.authService.getUsername(), bookId).subscribe({
      next: () => {
      this.notificationService.openDialog("Book marked as received!", false);
      this.fetchBooks();
      },
      error: (err) => {
        if (err.status === 404) {
          this.notificationService.openDialog("This book is not lent out.", true);
        } else {
          this.notificationService.openDialog("An error occurred. Please try again later.", true);
        }
      }
    });
  }


  markAsReturned(bookId: number) {
    this.bookService.markAsReturned(this.authService.getUsername(), bookId).subscribe({
      next: () => {
        this.notificationService.openDialog("Book returned successfully!", false);
        this.fetchBooks();
      },
      error: (err) => {
        if (err.status === 404) {
          this.notificationService.openDialog("This book currently is not borrowed or returned by user", true);
        } else {
          this.notificationService.openDialog("An error occurred. Please try again later.", true);
        }
      }
    });
  }

  protected readonly BookStatus = BookStatus;
}
