import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AddBookComponent } from '../add-book/add-book.component'; // Adjust the path if necessary
import { NotificationService } from '../services/notification.service';
import {Book, BooksService} from "../services/books.service"; // Adjust the path if necessary
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  imports: [HttpClientModule, CommonModule, AddBookComponent] // Import CommonModule here
})
export class DashboardComponent {
  isAddBookModalVisible = false;

  constructor(private notificationService: NotificationService, private booksService: BooksService) {}

  onAddBook() {
    this.isAddBookModalVisible = true;
  }

  onCloseModal() {
    this.isAddBookModalVisible = false;
  }

  onBookAdded() {
    this.isAddBookModalVisible = false; // Close the modal when a book is added
  }



  books: Book[] = [];



  ngOnInit(): void {
    this.loadBooks();
  }

  // Load all books from the backend
  loadBooks(): void {
    this.booksService.getBooks().subscribe(data => {
      this.books = data;
    });
  }


  // Cancel a reservation and update the book's status to available
  onCancelReservation(book: Book): void {
    book.status = 'available';
    this.booksService.updateBook(book).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
    });
  }

  // Mark a book as returned by updating its status to available
  onMarkReturned(book: Book): void {
    book.status = 'available';
    this.booksService.updateBook(book).subscribe(updatedBook => {
      this.updateBookInList(updatedBook);
    });
  }

  // Remove a book from the list and the backend
  onRemoveBook(book: Book): void {
    this.booksService.deleteBook(book.id).subscribe(() => {
      this.books = this.books.filter(b => b.id !== book.id);
    });
  }

  // Helper method to update a book in the local list after an update
  private updateBookInList(updatedBook: Book): void {
    const index = this.books.findIndex(b => b.id === updatedBook.id);
    if (index !== -1) {
      this.books[index] = updatedBook;
    }
  }
}
