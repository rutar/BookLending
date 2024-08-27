import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AddBookComponent} from '../add-book/add-book.component';
import {NotificationService} from '../services/notification.service';
import {BookDto, BookService} from "../services/book.service";
import {HttpClientModule} from '@angular/common/http';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  imports: [HttpClientModule, CommonModule, AddBookComponent]
})
export class DashboardComponent implements OnInit {
  isAddBookModalVisible = false;
  books: BookDto[] = [];
  currentUserId = 1; // Example user ID, replace with actual user ID in your app

  constructor(private notificationService: NotificationService, private booksService: BookService) {
  }

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
    });
  }

  // Reserve a book
  onReserveBook(book: BookDto): void {
    this.booksService.reserveBook(this.currentUserId, book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Book reserved successfully.", false);
    });
  }

  // Cancel a reservation
  onCancelReservation(book: BookDto): void {
    this.booksService.cancelReservation(this.currentUserId, book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Reservation canceled successfully.", false);
    });
  }

  // Mark a book as received
  onMarkAsReceived(book: BookDto): void {
    this.booksService.markAsReceived(this.currentUserId, book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Book marked as received.", false);
    });
  }

  // Mark a book as returned
  onMarkAsReturned(book: BookDto): void {
    this.booksService.markAsReturned(this.currentUserId, book.id).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
      this.notificationService.openDialog("Book marked as returned.", false);
    });
  }

  // Remove a book from the list and the backend
  onRemoveBook(book: BookDto): void {
    this.booksService.deleteBook(book.id).subscribe(() => {
      this.books = this.books.filter(b => b.id !== book.id);
      this.notificationService.openDialog("Book removed successfully.", false);
    });
  }

  // Helper method to update a book in the local list after an update
  private updateBookInList(updatedBook: BookDto): void {
    const index = this.books.findIndex(b => b.id === updatedBook.id);
    if (index !== -1) {
      this.books[index] = updatedBook;
    }
  }
}
