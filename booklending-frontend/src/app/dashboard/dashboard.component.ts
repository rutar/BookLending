import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AddBookComponent} from '../add-book/add-book.component';
import {NotificationService} from '../services/notification.service';
import {BookDto, BookService, BookStatus} from "../services/book.service";
import {HttpClientModule} from '@angular/common/http';
import {AuthService} from '../services/auth.service';
import {ConfirmationModalComponent} from '../confirmation-modal/confirmation-modal.component';
import {Subject, takeUntil} from "rxjs";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  imports: [HttpClientModule, CommonModule, FormsModule, AddBookComponent, ConfirmationModalComponent]
})
export class DashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  isAddBookModalVisible = false;
  isConfirmModalVisible = false;
  selectedBook: BookDto | null = null;
  books: BookDto[] = [];
  statusFilters: { [key in BookStatus]: boolean } = {
    [BookStatus.AVAILABLE]: true,
    [BookStatus.RESERVED]: false,
    [BookStatus.LENT_OUT]: false,
    [BookStatus.BORROWED]: false,
    [BookStatus.RETURNED]: false
  };

  // Define the ordered array of statuses
  orderedStatuses: BookStatus[] = [
    BookStatus.AVAILABLE,
    BookStatus.RESERVED,
    BookStatus.LENT_OUT,
    BookStatus.BORROWED,
    BookStatus.RETURNED
  ];

  currentPage = 0;
  isLoading = false;
  hasMoreBooks = true;
  totalPages: number = 0;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private booksService: BookService
  ) {
  }

  ngOnInit(): void {
    this.loadBooks();
  }


  @HostListener('window:scroll', ['$event'])
  onScroll(event: Event): void {
    const scrollTop = window.scrollY || document.documentElement.scrollTop;
    const scrollHeight = document.documentElement.scrollHeight;
    const clientHeight = document.documentElement.clientHeight;

    // Trigger fetch when 75% of the page is scrolled
    if (scrollTop + clientHeight >= scrollHeight * 0.75 && !this.isLoading && this.hasMoreBooks) {
      this.loadBooks();
    }
  }

  onAddBook() {
    this.isAddBookModalVisible = true;
  }

  onCloseModal() {
    this.isAddBookModalVisible = false;
  }

  onBookAdded() {
    this.isAddBookModalVisible = false;
  }

  loadBooks(): void {
    if (this.isLoading) return;

    this.isLoading = true;
    const activeStatuses = Object.entries(this.statusFilters)
      .filter(([_, isActive]) => isActive)
      .map(([status, _]) => status as BookStatus);

    this.booksService.getBooks(this.currentPage, '', 'title', 'asc', activeStatuses)
      .subscribe({
        next: (response) => {  // Correctly type response
          this.books = [...this.books, ...response.content];  // Append new books to existing list
          this.totalPages = response.totalPages;  // Update totalPages from response
          this.hasMoreBooks = this.currentPage < this.totalPages - 1;  // Determine if more books are available
          this.currentPage++;  // Increment current page for next request
          this.isLoading = false;  // Stop loading indicator
        },
        error: (error) => {
          console.error('Error fetching books:', error);
          this.notificationService.openDialog('Failed to load books. Please try again later.', true);
          this.isLoading = false;  // Ensure loading is stopped even on error
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onFilterChange(): void {
    this.books = [];
    this.currentPage = 0;
    this.hasMoreBooks = true;
    this.loadBooks();
  }

  onConfirmRemoveBook(book: BookDto): void {
    this.selectedBook = book;
    this.isConfirmModalVisible = true;
  }

  onRemoveBookConfirmed(): void {
    if (this.selectedBook) {
      this.onRemoveBook(this.selectedBook);
      this.onCloseConfirmModal();
    }
  }

  onCloseConfirmModal(): void {
    this.isConfirmModalVisible = false;
    this.selectedBook = null;
  }


  onReserveBook(book: BookDto): void {
    this.booksService.reserveBook(this.authService.getUsername(), book.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedBook) => {
          this.notificationService.openDialog('Book reserved successfully.', false);
          this.updateBookInList(updatedBook);
          this.books = [];
          this.currentPage = 0;
          this.loadBooks();
        },
        error: (error) => {
          this.notificationService.openDialog('Failed to reserve book. Please try again later.', true);
          // Handle error
        }
      });
  }


  // Cancel a reservation
  onCancelReservation(book: BookDto): void {
    this.booksService.cancelReservation(this.authService.getUsername(), book.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(updatedBook => {
        this.updateBookInList(updatedBook);
        this.notificationService.openDialog("Reservation canceled successfully.", false);
        this.books = [];
        this.currentPage = 0;
        this.loadBooks();
      });
  }

  // Mark a book as received by user
  onMarkAsReceived(book: BookDto): void {
    this.booksService.markAsReceived(this.authService.getUsername(), book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Book received.", false);
      this.books = [];
      this.currentPage = 0;
      this.loadBooks();
    });
  }

  // Mark a book as returned back to library
  onMarkAsLentout(book: BookDto): void {
    this.booksService.markAsLentout(this.authService.getUsername(), book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Book lent out.", false);
      this.books = [];
      this.currentPage = 0;
      this.loadBooks();
    });
  }

  // Mark a book as returned back to library
  onMarkAsReturned(book: BookDto): void {
    this.booksService.markAsReturned(this.authService.getUsername(), book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Book is returned to library.", false);
      this.books = [];
      this.currentPage = 0;
      this.loadBooks();
    });
  }

  onRemoveBook(book: BookDto): void {
    this.booksService.deleteBook(book.id).subscribe(() => {
      this.books = this.books.filter(b => b.id !== book.id);
      this.notificationService.openDialog("Book removed successfully.", false);
      this.books = [];
      this.currentPage = 0;
      this.loadBooks();
    });
  }

  private updateBookInList(updatedBook: BookDto): void {
    const index = this.books.findIndex(b => b.id === updatedBook.id);
    if (index !== -1) {
      this.books[index] = updatedBook;
    }
  }

  public BookStatus = BookStatus;
}
