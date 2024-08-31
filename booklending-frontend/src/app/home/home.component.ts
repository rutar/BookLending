import {CommonModule, NgForOf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {Component, HostListener, OnInit} from '@angular/core';
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

export class HomeComponent implements OnInit {
  searchQuery: string = '';
  books: BookDto[] = [];
  currentPage: number = 0;
  isLoading: boolean = false;
  hasMoreBooks: boolean = true;

  constructor(private authService: AuthService, private bookService: BookService, private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.fetchBooks();
  }

  // Infinite scrolling event listener
  @HostListener('window:scroll', ['$event'])
  onScroll(): void {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 100 && !this.isLoading && this.hasMoreBooks) {
      this.fetchBooks();
    }
  }

  // Fetch books with server-side filtering and sorting
  fetchBooks(): void {
    if (this.isLoading) return;

    this.isLoading = true;
    this.bookService.getBooks(this.currentPage, this.searchQuery, 'title', 'asc').subscribe({
      next: (response) => {
        this.books = [...this.books, ...response.content];
        this.hasMoreBooks = !response.last;
        this.currentPage++;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching books:', error);
        this.notificationService.openDialog('Failed to load books. Please try again later.', true);
        this.isLoading = false;
      }
    });
  }

  // Trigger book search and reset infinite scroll
  onSearch(): void {
    this.books = [];
    this.currentPage = 0;
    this.hasMoreBooks = true;
    this.fetchBooks();
  }

  // Book actions (reserve, cancel reservation, mark as received, mark as returned)
  reserveBook(bookId: number) {
    this.bookService.reserveBook(this.authService.getUsername(), bookId).subscribe({
      next: () => {
        this.notificationService.openDialog("Book reserved successfully!", false);
        this.onSearch();  // Reset and refetch books
      },
      error: (err) => {
        this.handleError(err, "Book not found.");
      }
    });
  }

  cancelReservation(bookId: number) {
    this.bookService.cancelReservation(this.authService.getUsername(), bookId).subscribe({
      next: () => {
        this.notificationService.openDialog("Reservation cancelled successfully!", false);
        this.onSearch();  // Reset and refetch books
      },
      error: (err) => {
        this.handleError(err, "Reservation not found. It may have already been cancelled.");
      }
    });
  }

  markAsReceived(bookId: number) {
    this.bookService.markAsReceived(this.authService.getUsername(), bookId).subscribe({
      next: () => {
        this.notificationService.openDialog("Book marked as received!", false);
        this.onSearch();  // Reset and refetch books
      },
      error: (err) => {
        this.handleError(err, "This book is not lent out.");
      }
    });
  }

  markAsReturned(bookId: number) {
    this.bookService.markAsReturned(this.authService.getUsername(), bookId).subscribe({
      next: () => {
        this.notificationService.openDialog("Book returned successfully!", false);
        this.onSearch();  // Reset and refetch books
      },
      error: (err) => {
        this.handleError(err, "This book is not borrowed or returned by the user.");
      }
    });
  }

  private handleError(error: any, notFoundMessage: string): void {
    if (error.status === 404) {
      this.notificationService.openDialog(notFoundMessage, true);
    } else {
      this.notificationService.openDialog("An error occurred. Please try again later.", true);
    }
  }

  protected readonly BookStatus = BookStatus;
}
