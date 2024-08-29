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
    this.loadBooks();
  }

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks() {
    this.bookService.getBooks().subscribe(data => {
      this.books = data;
      this.filteredBooks = this.books;
    });
  }

  onSearch() {
    this.filteredBooks = this.books.filter(book =>
      book.title.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  reserveBook(bookId: number) {
    this.bookService.reserveBook(this.authService.getUsername(), bookId).subscribe(() => {
      this.notificationService.openDialog("Book reserved successfully!", false);
      this.loadBooks();
    });
  }

  cancelReservation(bookId: number) {
    this.bookService.cancelReservation(this.authService.getUsername(), bookId).subscribe({
      next: () => {
        this.notificationService.openDialog("Reservation cancelled successfully!", false);
        this.loadBooks();
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
    this.bookService.markAsReceived(this.authService.getUsername(), bookId).subscribe(() => {
      this.notificationService.openDialog("Book marked as received!", false);
      this.loadBooks();
    });
  }

  markAsReturned(bookId: number) {
    this.bookService.markAsReturned(this.authService.getUsername(), bookId).subscribe(() => {
      this.notificationService.openDialog("Book returned successfully!", false);
      this.loadBooks();
    });
  }

  protected readonly BookStatus = BookStatus;
}
