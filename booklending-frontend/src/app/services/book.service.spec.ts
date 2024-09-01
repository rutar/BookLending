import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BookService, BookDto, BookStatus, PagedResponse, CreateBook } from './book.service';
import { AuthService } from './auth.service';

describe('BookService', () => {
  let service: BookService;
  let httpMock: HttpTestingController;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['getUsername']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        BookService,
        { provide: AuthService, useValue: authSpy }
      ]
    });

    service = TestBed.inject(BookService);
    httpMock = TestBed.inject(HttpTestingController);
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch books', () => {
    const mockResponse: PagedResponse<BookDto> = {
      content: [
        { id: 1, title: 'Test Book', author: 'Author', isbn: '123456789', status: BookStatus.AVAILABLE, coverUrl: 'https://example.com/cover.jpg' }
      ],
      totalElements: 1,
      totalPages: 1,
      last: true,
      size: 1,
      number: 0
    };

    service.getBooks(0, '', 'title', 'asc', [BookStatus.AVAILABLE]).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(request => request.url === 'http://localhost:8080/api/books' && request.method === 'GET');
    expect(req.request.params.has('page')).toBeTrue();
    expect(req.request.params.get('page')).toEqual('0');
    req.flush(mockResponse);
  });

  it('should fetch a single book by ID', () => {
    const mockBook: BookDto = { id: 1, title: 'Test Book', author: 'Author', isbn: '123456789', status: BookStatus.AVAILABLE, coverUrl: 'https://example.com/cover.jpg' };

    service.getBookById(1).subscribe(book => {
      expect(book).toEqual(mockBook);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/books/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockBook);
  });

  it('should add a new book', () => {
    const newBook: CreateBook = { title: 'New Book', author: 'Author', coverUrl: 'https://example.com/cover.jpg', isbn: '987654321', status: BookStatus.AVAILABLE };
    const addedBook: BookDto = { id: 2, ...newBook };

    authServiceSpy.getUsername.and.returnValue('testUser');

    service.addBook(newBook).subscribe(book => {
      expect(book).toEqual(addedBook);
    });

    const req = httpMock.expectOne((request) => {
      return request.url === 'http://localhost:8080/api/books' &&
        request.method === 'POST' &&
        request.params.has('userName') &&
        request.params.get('userName') === 'testUser';
    });

    // Check if the body of the request matches the expected new book
    expect(req.request.body).toEqual(newBook);

    // Respond with the mocked book data
    req.flush(addedBook);
  });

  it('should update an existing book', () => {
    const updatedBook: BookDto = { id: 1, title: 'Updated Book', author: 'Updated Author', isbn: '123456789', status: BookStatus.RESERVED, coverUrl: 'https://example.com/cover.jpg' };

    service.updateBook(updatedBook).subscribe(book => {
      expect(book).toEqual(updatedBook);
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/books/${updatedBook.id}`);
    expect(req.request.method).toBe('PUT');
    req.flush(updatedBook);
  });

  it('should delete a book', () => {
    const bookId = 1;
    authServiceSpy.getUsername.and.returnValue('testUser');

    service.deleteBook(bookId).subscribe(() => {
      expect(true).toBeTrue(); // Expect no errors for successful deletion
    });

    const req = httpMock.expectOne((request) => {
      return request.url === `http://localhost:8080/api/books/${bookId}` &&
        request.method === 'DELETE' &&
        request.params.has('userName') &&
        request.params.get('userName') === 'testUser';
    });

    // Verify that the request method is DELETE and params are correct
    expect(req.request.method).toBe('DELETE');
    expect(req.request.params.has('userName')).toBeTrue();
    expect(req.request.params.get('userName')).toEqual('testUser');

    // Respond with no content
    req.flush({});
  });

  it('should search books', () => {
    const searchQuery = 'Test';
    const mockBooks: BookDto[] = [
      { id: 1, title: 'Test Book', author: 'Author', isbn: '123456789', status: BookStatus.AVAILABLE, coverUrl: 'https://example.com/cover.jpg' }
    ];

    service.searchBooks(searchQuery).subscribe(books => {
      expect(books).toEqual(mockBooks);
    });

    const req = httpMock.expectOne(request => request.url === 'http://localhost:8080/api/books/search' && request.method === 'GET');
    expect(req.request.params.has('query')).toBeTrue();
    expect(req.request.params.get('query')).toEqual(searchQuery.toLowerCase().trim());
    req.flush(mockBooks);
  });

  it('should reserve a book', () => {
    const userName = 'testUser';
    const bookId = 1;
    const mockBook: BookDto = {
      id: 1,
      title: 'Reserved Book',
      author: 'Author',
      isbn: '123456789',
      status: BookStatus.RESERVED,
      coverUrl: 'https://example.com/cover.jpg'
    };

    authServiceSpy.getUsername.and.returnValue(userName);

    service.reserveBook(userName, bookId).subscribe(book => {
      expect(book).toEqual(mockBook);
    });

    const req = httpMock.expectOne(request =>
      request.url === `http://localhost:8080/api/actions/reserve` &&
      request.params.has('userName') &&
      request.params.get('userName') === userName &&
      request.params.has('bookId') &&
      request.params.get('bookId') === bookId.toString()
    );
    expect(req.request.method).toBe('POST');
    req.flush(mockBook);
  });

  it('should cancel reservation', () => {
    const userName = 'testUser';
    const bookId = 1;
    const mockBook: BookDto = {
      id: 1,
      title: 'Cancelled Book',
      author: 'Author',
      isbn: '123456789',
      status: BookStatus.AVAILABLE,
      coverUrl: 'https://example.com/cover.jpg'
    };

    authServiceSpy.getUsername.and.returnValue(userName);

    service.cancelReservation(userName, bookId).subscribe(book => {
      expect(book).toEqual(mockBook);
    });

    const req = httpMock.expectOne(request =>
      request.url === `http://localhost:8080/api/actions/cancel_reservation` &&
      request.params.has('userName') &&
      request.params.get('userName') === userName &&
      request.params.has('bookId') &&
      request.params.get('bookId') === bookId.toString()
    );
    expect(req.request.method).toBe('POST');
    req.flush(mockBook);
  });

  it('should mark a book as lent out', () => {
    const userName = 'testUser';
    const bookId = 1;
    const mockBook: BookDto = {
      id: 1,
      title: 'Lent Book',
      author: 'Author',
      isbn: '123456789',
      status: BookStatus.LENT_OUT,
      coverUrl: 'https://example.com/cover.jpg'
    };

    authServiceSpy.getUsername.and.returnValue(userName);

    service.markAsLentout(userName, bookId).subscribe(book => {
      expect(book).toEqual(mockBook);
    });

    const req = httpMock.expectOne(request =>
      request.url === `http://localhost:8080/api/actions/lent_out` &&
      request.params.has('userName') &&
      request.params.get('userName') === userName &&
      request.params.has('bookId') &&
      request.params.get('bookId') === bookId.toString()
    );
    expect(req.request.method).toBe('POST');
    req.flush(mockBook);
  });


  it('should mark a book as received', () => {
    const userName = 'testUser';
    const bookId = 1;
    const mockBook: BookDto = { id: 1, title: 'Received Book', author: 'Author', isbn: '123456789', status: BookStatus.RETURNED, coverUrl: 'https://example.com/cover.jpg' };

    authServiceSpy.getUsername.and.returnValue(userName);

    service.markAsReceived(userName, bookId).subscribe(book => {
      expect(book).toEqual(mockBook);
    });

    const req = httpMock.expectOne(request =>
      request.url === `http://localhost:8080/api/actions/received` &&
      request.params.has('userName') &&
      request.params.get('userName') === userName &&
      request.params.has('bookId') &&
      request.params.get('bookId') === bookId.toString()
    );
    expect(req.request.method).toBe('POST');
    req.flush(mockBook);
  });

  it('should mark a book as returned', () => {
    const userName = 'testUser';
    const bookId = 1;
    const mockBook: BookDto = { id: 1, title: 'Returned Book', author: 'Author', isbn: '123456789', status: BookStatus.RETURNED, coverUrl: 'https://example.com/cover.jpg' };

    authServiceSpy.getUsername.and.returnValue(userName);

    service.markAsReturned(userName, bookId).subscribe(book => {
      expect(book).toEqual(mockBook);
    });

    const req = httpMock.expectOne(request =>
      request.url === `http://localhost:8080/api/actions/returned` &&
      request.params.has('userName') &&
      request.params.get('userName') === userName &&
      request.params.has('bookId') &&
      request.params.get('bookId') === bookId.toString()
    );
    expect(req.request.method).toBe('POST');
    req.flush(mockBook);
  });
});
