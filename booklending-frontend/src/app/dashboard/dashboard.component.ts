import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common'; // Import CommonModule

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  imports: [CommonModule] // Add CommonModule here
})
export class DashboardComponent implements OnInit {
  books: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    // Replace with your actual API endpoint
    this.http.get<any[]>('/api/books').subscribe(data => {
      this.books = data;
    });
  }

  onAddBook(): void {
    // Logic to add a new book
  }

  onCancelReservation(book: any): void {
    // Logic to cancel reservation
    // Update book status to available
    book.status = 'available';
    this.updateBook(book);
  }

  onMarkReturned(book: any): void {
    // Logic to mark a book as returned
    // Update book status to available
    book.status = 'available';
    this.updateBook(book);
  }

  onRemoveBook(book: any): void {
    // Logic to remove a book
    this.http.delete(`/api/books/${book.id}`).subscribe(() => {
      this.books = this.books.filter(b => b.id !== book.id);
    });
  }

  private updateBook(book: any): void {
    // Replace with your actual API endpoint
    this.http.put(`/api/books/${book.id}`, book).subscribe();
  }
}
