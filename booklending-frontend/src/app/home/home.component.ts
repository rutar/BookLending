import { Component, OnInit } from '@angular/core';
import { BookService } from '../services/book.service';
import { BookDto } from '../services/book.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  standalone: true,
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  books: BookDto[] = [];
  selectedBook: BookDto | null = null;
  private errorMessage: string | undefined;

  constructor(private bookService: BookService) { }

  ngOnInit(): void {
    // Load books on component initialization
    this.loadBooks();
  }

  loadBooks(): void {
    this.bookService.getBooks().subscribe({
      next: (books: BookDto[]) => {
        this.books = books;
        this.errorMessage = ''; // Clear any previous error messages
      },
      error: (err: any) => {
        console.error('Error loading books', err);
        this.errorMessage = 'Failed to load books. Please try again later.';
        this.books = []; // Clear books on error
      }
    });
  }

  onSearch(query: string): void {
    // Check if the query is not empty or whitespace
    if (!query.trim()) {
      this.books = []; // Clear books if the query is invalid
      this.errorMessage = 'Please enter a valid search query.';
      return;
    }

    this.bookService.searchBooks(query).subscribe({
      next: (books: BookDto[]) => {
        this.books = books;
        this.errorMessage = ''; // Clear any previous error messages
      },
      error: (err: any) => {
        console.error('Error searching books', err);
        this.errorMessage = 'There was an error retrieving search results. Please try again.';
        this.books = []; // Clear books on error
      }
    });
  }
/*  onReserve(book: BookDto): void {
    this.bookService.reserveBook(book.id).subscribe(
      () => this.loadBooks(),
        (error: any) => console.error('Error reserving book', error)
    );
  }

  onCancel(book: BookDto): void {
    this.bookService.cancelReservation(book.id).subscribe(
      () => this.loadBooks(),
        (error: any) => console.error('Error cancelling reservation', error)
    );
  }

  onMarkReceived(book: BookDto): void {
    this.bookService.markAsReceived(book.id).subscribe(
      () => this.loadBooks(),
        (error: any) => console.error('Error marking book as received', error)
    );
  }

  onMarkReturned(book: BookDto): void {
    this.bookService.markAsReturned(book.id).subscribe(
      () => this.loadBooks(),
        (error: any) => console.error('Error marking book as returned', error)
    );
  }*/
}
