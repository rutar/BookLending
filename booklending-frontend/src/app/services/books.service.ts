import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  status: string;
  coverUrl: string;
}

// Interface for creating a new book, which does not include the ID
export interface CreateBook {
  title: string;
  author: string;
  coverUrl: string;
  isbn: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class BooksService {

  private baseUrl = 'http://localhost:8080/api/books';  // Base URL for your backend API

  constructor(private http: HttpClient) { }

  // Fetch all books
  getBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(this.baseUrl);
  }

  // Fetch a single book by ID
  getBookById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.baseUrl}/${id}`);
  }

  // Add a new book
  addBook(book: CreateBook): Observable<Book> {
    return this.http.post<Book>(this.baseUrl, book);
  }

  // Update an existing book
  updateBook(book: Book): Observable<Book> {
    return this.http.put<Book>(`${this.baseUrl}/${book.id}`, book);
  }

  // Delete a book by ID
  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
