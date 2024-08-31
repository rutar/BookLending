import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AddBookComponent } from '../add-book/add-book.component';
import { NotificationService } from '../services/notification.service';
import { BookDto, BookService, BookStatus } from "../services/book.service";
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { ConfirmationModalComponent } from '../confirmation-modal/confirmation-modal.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  imports: [HttpClientModule, CommonModule, FormsModule, AddBookComponent, ConfirmationModalComponent]
})
export class DashboardComponent implements OnInit {
  isAddBookModalVisible = false;
  isConfirmModalVisible = false;
  selectedBook: BookDto | null = null;
  books: BookDto[] = [];
  filteredBooks: BookDto[] = [];
  statusFilters: { [key in BookStatus]: boolean } = {
    [BookStatus.AVAILABLE]: false,
    [BookStatus.RESERVED]: false,
    [BookStatus.LENT_OUT]: false,
    [BookStatus.BORROWED]: false,
    [BookStatus.RETURNED]: false
  };

  constructor(private authService: AuthService, private notificationService: NotificationService, private booksService: BookService) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  onAddBook() {
    this.isAddBookModalVisible = true;
  }

  onCloseModal() {
    this.isAddBookModalVisible = false;
  }

  onBookAdded() {
    this.isAddBookModalVisible = false;
    this.loadBooks();
  }

  loadBooks(): void {
    this.booksService.getBooks().subscribe(data => {
      this.books = data;
      this.applyFilters();
    });
  }

  applyFilters(): void {
    const activeFilters = Object.entries(this.statusFilters)
      .filter(([_, isActive]) => isActive)
      .map(([status, _]) => status as BookStatus);

    if (activeFilters.length === 0) {
      this.filteredBooks = this.books;
    } else {
      this.filteredBooks = this.books.filter(book => activeFilters.includes(book.status));
    }
  }

  onFilterChange(): void {
    this.applyFilters();
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

  // Reserve a book
  onReserveBook(book: BookDto): void {
    this.booksService.reserveBook(this.authService.getUsername(), book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Book reserved successfully.", false);
      this.loadBooks();
    });
  }

  // Cancel a reservation
  onCancelReservation(book: BookDto): void {
    this.booksService.cancelReservation(this.authService.getUsername(), book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Reservation canceled successfully.", false);
      this.loadBooks();
    });
  }

  // Mark a book as received by user
  onMarkAsReceived(book: BookDto): void {
    this.booksService.markAsReceived(this.authService.getUsername(), book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Book received.", false);
      this.loadBooks();
    });
  }

  // Mark a book as returned back to library
  onMarkAsLentout(book: BookDto): void {
    this.booksService.markAsLentout(this.authService.getUsername(), book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Book lent out.", false);
      this.loadBooks();
    });
  }

  // Mark a book as returned back to library
  onMarkAsReturned(book: BookDto): void {
    this.booksService.markAsReturned(this.authService.getUsername(), book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Book returned to library.", false);
      this.loadBooks();
    });
  }

  onRemoveBook(book: BookDto): void {
    this.booksService.deleteBook(book.id).subscribe(() => {
      this.books = this.books.filter(b => b.id !== book.id);
      this.notificationService.openDialog("Book removed successfully.", false);
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
