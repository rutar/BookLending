import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {BookDto, BookService, BookStatus, CreateBook} from './book.service';

describe('BooksService', () => {
  let service: BookService;
  let httpMock: HttpTestingController;
  const baseUrl = 'http://localhost:8080/api/books';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BookService]
    });

    service = TestBed.inject(BookService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('#getBooks', () => {
    it('should return an Observable<Book[]>', () => {
      const dummyBooks: BookDto[] = [
        {
          id: 1,
          title: 'Book 1',
          author: 'Author 1',
          isbn: '12345',
          status: BookStatus.AVAILABLE,
          coverUrl: 'http://example.com/cover1.jpg'
        },
        {
          id: 2,
          title: 'Book 2',
          author: 'Author 2',
          isbn: '67890',
          status: BookStatus.BORROWED,
          coverUrl: 'http://example.com/cover2.jpg'
        }
      ];

      service.getBooks().subscribe(books => {
        expect(books.length).toBe(2);
        expect(books).toEqual(dummyBooks);
      });

      const req = httpMock.expectOne(`${baseUrl}`);
      expect(req.request.method).toBe('GET');
      req.flush(dummyBooks);
    });
  });

  describe('#getBookById', () => {
    it('should return an Observable<Book>', () => {
      const dummyBook: BookDto = {
        id: 1,
        title: 'Book 1',
        author: 'Author 1',
        isbn: '12345',
        status: BookStatus.AVAILABLE,
        coverUrl: 'http://example.com/cover1.jpg'
      };

      service.getBookById(1).subscribe(book => {
        expect(book).toEqual(dummyBook);
      });

      const req = httpMock.expectOne(`${baseUrl}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(dummyBook);
    });
  });

  describe('#addBook', () => {
    it('should add a book and return the added book', () => {
      const newBook: CreateBook = {
        title: 'New Book',
        author: 'New Author',
        coverUrl: 'http://example.com/newcover.jpg',
        isbn: '112233',
        status: BookStatus.AVAILABLE
      };
      const dummyBook: BookDto = {id: 1, ...newBook};

      service.addBook(newBook).subscribe(book => {
        expect(book).toEqual(dummyBook);
      });

      const req = httpMock.expectOne(`${baseUrl}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newBook);
      req.flush(dummyBook);
    });
  });

  describe('#updateBook', () => {
    it('should update a book and return the updated book', () => {
      const updatedBook: BookDto = {
        id: 1,
        title: 'Updated Book',
        author: 'Updated Author',
        isbn: '12345',
        status: BookStatus.AVAILABLE,
        coverUrl: 'http://example.com/updatedcover.jpg'
      };

      service.updateBook(updatedBook).subscribe(book => {
        expect(book).toEqual(updatedBook);
      });

      const req = httpMock.expectOne(`${baseUrl}/1`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedBook);
      req.flush(updatedBook);
    });
  });

  describe('#deleteBook', () => {
    it('should delete a book by ID', () => {
      service.deleteBook(1).subscribe({

        next: (response) => {
          // Since `deleteBook` returns void, assert that no data is returned
          expect(response).toEqual(Object({}));
        },
        error: (err) => fail('Expected no error but got one: ' + err)
      });

      const req = httpMock.expectOne(`${baseUrl}/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush({}); // Empty object to simulate successful delete
    });
  });
});
