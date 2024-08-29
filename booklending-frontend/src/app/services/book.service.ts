import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';


export enum BookStatus {
  AVAILABLE = 'AVAILABLE',
  BORROWED = 'BORROWED',
  RESERVED = 'RESERVED'
}

export interface BookDto {
  id: number;
  title: string;
  author: string;
  isbn: string;
  status: BookStatus;
  coverUrl: string;
}

// Interface for creating a new book, which does not include the ID
export interface CreateBook {
  title: string;
  author: string;
  coverUrl: string;
  isbn: string;
  status: BookStatus;
}

@Injectable({
  providedIn: 'root'
})
export class BookService {

  private bookServiceBaseUrl = 'http://localhost:8080/api/books';  // URL for your backend API
  private actionServiceBaseUrl = 'http://localhost:8080/api/actions'; // URL for book actions

  constructor(private http: HttpClient) {
  }

  // Fetch all books
  getBooks(): Observable<BookDto[]> {
    return this.http.get<BookDto[]>(this.bookServiceBaseUrl);
  }

  // Fetch a single book by ID
  getBookById(id: number): Observable<BookDto> {
    return this.http.get<BookDto>(`${this.bookServiceBaseUrl}/${id}`);
  }

  // Add a new book
  addBook(book: CreateBook): Observable<BookDto> {
    return this.http.post<BookDto>(this.bookServiceBaseUrl, book);
  }

  // Update an existing book
  updateBook(book: BookDto): Observable<BookDto> {
    return this.http.put<BookDto>(`${this.bookServiceBaseUrl}/${book.id}`, book);
  }

  // Delete a book by ID
  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.bookServiceBaseUrl}/${id}`);
  }

  // Search books by title, author, or ISBN
  searchBooks(query: string): Observable<BookDto[]> {
    const params = new HttpParams().set('query', query.trim().toLowerCase());
    return this.http.get<BookDto[]>(`${this.bookServiceBaseUrl}/search`, {params});
  }

  // Reserve a book
  reserveBook(userId: number, bookId: number): Observable<BookDto> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('bookId', bookId);

    return this.http.post<BookDto>(`${this.actionServiceBaseUrl}/reserve`, {}, {params});
  }

  // Cancel a reservation
  cancelReservation(userId: number, bookId: number): Observable<BookDto> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('bookId', bookId);

    return this.http.post<BookDto>(`${this.actionServiceBaseUrl}/cancel`, {}, {params});
  }

  // Mark a book as received
  markAsReceived(userId: number, bookId: number): Observable<BookDto> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('bookId', bookId);

    return this.http.post<BookDto>(`${this.actionServiceBaseUrl}/receive`, {}, {params});
  }

  // Mark a book as returned
  markAsReturned(userId: number, bookId: number): Observable<BookDto> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('bookId', bookId);

    return this.http.post<BookDto>(`${this.actionServiceBaseUrl}/return`, {}, {params});
  }
}
