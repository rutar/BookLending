import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthService} from "./auth.service";

export interface BookDto {
  id: number;
  title: string;
  author: string;
  isbn: string;
  status: BookStatus;
  coverUrl: string;
}

export enum BookStatus {
  AVAILABLE = 'AVAILABLE',
  RESERVED = 'RESERVED',
  LENT_OUT = 'LENT_OUT',
  BORROWED = 'BORROWED',
  RETURNED = 'RETURNED'
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

  constructor(private http: HttpClient, private authService: AuthService) {
  }


  // Method to fetch books from the server with search and sorting
  getBooks(searchQuery: string = '', sortBy: string = 'title', order: string = 'asc'): Observable<any[]> {
    let params = new HttpParams()
      .set('searchQuery', searchQuery)
      .set('sortBy', sortBy)
      .set('order', order);

    return this.http.get<BookDto[]>(this.bookServiceBaseUrl, { params });
  }

  // Fetch a single book by ID
  getBookById(id: number): Observable<BookDto> {
    return this.http.get<BookDto>(`${this.bookServiceBaseUrl}/${id}`);
  }

  // Add a new book
  addBook(book: CreateBook): Observable<BookDto> {
    const params = new HttpParams().set('userName', this.authService.getUsername());
    return this.http.post<BookDto>(this.bookServiceBaseUrl, book, {params});
  }

  // Update an existing book
  updateBook(book: BookDto): Observable<BookDto> {
    return this.http.put<BookDto>(`${this.bookServiceBaseUrl}/${book.id}`, book);
  }

  // Delete a book by ID
  deleteBook(id: number): Observable<void> {
    const params = new HttpParams().set('userName', this.authService.getUsername());
    return this.http.delete<void>(`${this.bookServiceBaseUrl}/${id}`, {params});
  }

  // Search books by title, author, or ISBN
  searchBooks(query: string): Observable<BookDto[]> {
    const params = new HttpParams().set('query', query.trim().toLowerCase());
    return this.http.get<BookDto[]>(`${this.bookServiceBaseUrl}/search`, {params});
  }

  // Reserve a book
  reserveBook(userName: string , bookId: number): Observable<BookDto> {
    const params = new HttpParams()
      .set('userName', userName)
      .set('bookId', bookId);

    return this.http.post<BookDto>(`${this.actionServiceBaseUrl}/reserve`, {}, {params});
  }

  // Cancel a reservation
  cancelReservation(userName: string, bookId: number): Observable<BookDto> {
    const params = new HttpParams()
      .set('userName', userName)
      .set('bookId', bookId);

    return this.http.post<BookDto>(`${this.actionServiceBaseUrl}/cancel_reservation`, {}, {params});
  }

  // Mark a book as lent out by librarian
  markAsLentout(userName: string , bookId: number): Observable<BookDto> {
    const params = new HttpParams()
      .set('userName', userName)
      .set('bookId', bookId);

    return this.http.post<BookDto>(`${this.actionServiceBaseUrl}/lent_out`, {}, {params});
  }

  // Mark a book as received
  markAsReceived(userName: string , bookId: number): Observable<BookDto> {
    const params = new HttpParams()
      .set('userName', userName)
      .set('bookId', bookId);

    return this.http.post<BookDto>(`${this.actionServiceBaseUrl}/received`, {}, {params});
  }

  // Mark a book as returned
  markAsReturned(userName: string, bookId: number): Observable<BookDto> {
    const params = new HttpParams()
      .set('userName', userName)
      .set('bookId', bookId);

    return this.http.post<BookDto>(`${this.actionServiceBaseUrl}/returned`, {}, {params});
  }
}
