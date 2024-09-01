import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { BookService, BookDto, BookStatus, PagedResponse } from '../services/book.service';
import { NotificationService } from '../services/notification.service';
import { AuthService } from '../services/auth.service';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let bookServiceSpy: jasmine.SpyObj<BookService>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const bookServiceSpyObj = jasmine.createSpyObj('BookService', ['getBooks', 'reserveBook', 'cancelReservation', 'markAsReceived', 'markAsLentout', 'markAsReturned', 'deleteBook']);
    const notificationServiceSpyObj = jasmine.createSpyObj('NotificationService', ['openDialog']);
    const authServiceSpyObj = jasmine.createSpyObj('AuthService', ['getUsername']);

    await TestBed.configureTestingModule({
      imports: [DashboardComponent, HttpClientTestingModule],
      providers: [
        { provide: BookService, useValue: bookServiceSpyObj },
        { provide: NotificationService, useValue: notificationServiceSpyObj },
        { provide: AuthService, useValue: authServiceSpyObj }
      ]
    }).compileComponents();

    bookServiceSpy = TestBed.inject(BookService) as jasmine.SpyObj<BookService>;
    notificationServiceSpy = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load books on initialization', fakeAsync(() => {
    const mockBooks: BookDto[] = [
      { id: 1, title: 'Book 1', author: 'Author 1', isbn: '1234567890', status: BookStatus.AVAILABLE, coverUrl: 'url1' },
      { id: 2, title: 'Book 2', author: 'Author 2', isbn: '0987654321', status: BookStatus.RESERVED, coverUrl: 'url2' }
    ];
    const mockResponse: PagedResponse<BookDto> = {
      content: mockBooks,
      totalElements: 2,
      totalPages: 1,
      last: true,
      size: 2,
      number: 0
    };
    bookServiceSpy.getBooks.and.returnValue(of(mockResponse));

    fixture.detectChanges();
    tick();

    expect(component.books).toEqual(mockBooks);
    expect(component.totalPages).toBe(1);
    expect(bookServiceSpy.getBooks).toHaveBeenCalledWith(0, '', 'title', 'asc', [BookStatus.AVAILABLE]);
  }));

  it('should handle error when loading books', fakeAsync(() => {
    bookServiceSpy.getBooks.and.returnValue(throwError(() => new Error('Network error')));

    fixture.detectChanges();
    tick();

    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Failed to load books. Please try again later.', true);
  }));

  it('should reserve a book', fakeAsync(() => {
    const mockBook: BookDto = { id: 1, title: 'Book 1', author: 'Author 1', isbn: '1234567890', status: BookStatus.AVAILABLE, coverUrl: 'url1' };
    const updatedBook: BookDto = { ...mockBook, status: BookStatus.RESERVED };
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.reserveBook.and.returnValue(of(updatedBook));
    bookServiceSpy.getBooks.and.returnValue(of({ content: [updatedBook], totalElements: 1, totalPages: 1, last: true, size: 1, number: 0 }));

    component.onReserveBook(mockBook);
    tick();

    expect(bookServiceSpy.reserveBook).toHaveBeenCalledWith('testUser', 1);
    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Book reserved successfully.', false);
    expect(component.books).toEqual([updatedBook]);
  }));

  it('should cancel a reservation', fakeAsync(() => {
    const mockBook: BookDto = { id: 1, title: 'Book 1', author: 'Author 1', isbn: '1234567890', status: BookStatus.RESERVED, coverUrl: 'url1' };
    const updatedBook: BookDto = { ...mockBook, status: BookStatus.AVAILABLE };
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.cancelReservation.and.returnValue(of(updatedBook));
    bookServiceSpy.getBooks.and.returnValue(of({ content: [updatedBook], totalElements: 1, totalPages: 1, last: true, size: 1, number: 0 }));

    component.onCancelReservation(mockBook);
    tick();

    expect(bookServiceSpy.cancelReservation).toHaveBeenCalledWith('testUser', 1);
    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Reservation canceled successfully.', false);
    expect(component.books).toEqual([updatedBook]);
  }));

  it('should mark a book as received', fakeAsync(() => {
    const mockBook: BookDto = { id: 1, title: 'Book 1', author: 'Author 1', isbn: '1234567890', status: BookStatus.RESERVED, coverUrl: 'url1' };
    const updatedBook: BookDto = { ...mockBook, status: BookStatus.BORROWED };
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.markAsReceived.and.returnValue(of(updatedBook));
    bookServiceSpy.getBooks.and.returnValue(of({ content: [updatedBook], totalElements: 1, totalPages: 1, last: true, size: 1, number: 0 }));

    component.onMarkAsReceived(mockBook);
    tick();

    expect(bookServiceSpy.markAsReceived).toHaveBeenCalledWith('testUser', 1);
    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Book received.', false);
    expect(component.books).toEqual([updatedBook]);
  }));

  it('should mark a book as lent out', fakeAsync(() => {
    const mockBook: BookDto = { id: 1, title: 'Book 1', author: 'Author 1', isbn: '1234567890', status: BookStatus.RESERVED, coverUrl: 'url1' };
    const updatedBook: BookDto = { ...mockBook, status: BookStatus.LENT_OUT };
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.markAsLentout.and.returnValue(of(updatedBook));
    bookServiceSpy.getBooks.and.returnValue(of({ content: [updatedBook], totalElements: 1, totalPages: 1, last: true, size: 1, number: 0 }));

    component.onMarkAsLentout(mockBook);
    tick();

    expect(bookServiceSpy.markAsLentout).toHaveBeenCalledWith('testUser', 1);
    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Book lent out.', false);
    expect(component.books).toEqual([updatedBook]);
  }));

  it('should mark a book as returned', fakeAsync(() => {
    const mockBook: BookDto = { id: 1, title: 'Book 1', author: 'Author 1', isbn: '1234567890', status: BookStatus.BORROWED, coverUrl: 'url1' };
    const updatedBook: BookDto = { ...mockBook, status: BookStatus.RETURNED };
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.markAsReturned.and.returnValue(of(updatedBook));
    bookServiceSpy.getBooks.and.returnValue(of({ content: [updatedBook], totalElements: 1, totalPages: 1, last: true, size: 1, number: 0 }));

    component.onMarkAsReturned(mockBook);
    tick();

    expect(bookServiceSpy.markAsReturned).toHaveBeenCalledWith('testUser', 1);
    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Book is returned to library.', false);
    expect(component.books).toEqual([updatedBook]);
  }));

  it('should remove a book', fakeAsync(() => {
    const mockBook: BookDto = { id: 1, title: 'Book 1', author: 'Author 1', isbn: '1234567890', status: BookStatus.AVAILABLE, coverUrl: 'url1' };
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.deleteBook.and.returnValue(of(void 0));
    bookServiceSpy.getBooks.and.returnValue(of({ content: [], totalElements: 0, totalPages: 0, last: true, size: 0, number: 0 }));

    component.onRemoveBook(mockBook);
    tick();

    expect(bookServiceSpy.deleteBook).toHaveBeenCalledWith(1);
    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Book removed successfully.', false);
    expect(component.books).toEqual([]);
  }));

  it('should update filters and reload books', fakeAsync(() => {
    component.statusFilters[BookStatus.RESERVED] = true;
    bookServiceSpy.getBooks.and.returnValue(of({ content: [], totalElements: 0, totalPages: 0, last: true, size: 0, number: 0 }));

    component.onFilterChange();
    tick();

    expect(bookServiceSpy.getBooks).toHaveBeenCalledWith(0, '', 'title', 'asc', [BookStatus.AVAILABLE, BookStatus.RESERVED]);
    expect(component.currentPage).toBe(1);
    expect(component.hasMoreBooks).toBeFalse();
  }));

  it('should load more books on scroll', fakeAsync(() => {
    const mockBooks: BookDto[] = [
      { id: 1, title: 'Book 1', author: 'Author 1', isbn: '1234567890', status: BookStatus.AVAILABLE, coverUrl: 'url1' },
      { id: 2, title: 'Book 2', author: 'Author 2', isbn: '0987654321', status: BookStatus.AVAILABLE, coverUrl: 'url2' }
    ];
    bookServiceSpy.getBooks.and.returnValue(of({ content: mockBooks, totalElements: 4, totalPages: 2, last: false, size: 2, number: 1 }));
    component.books = [...mockBooks];
    component.currentPage = 1;
    component.hasMoreBooks = true;

    component.onScroll(new Event('scroll'));
    tick();

    expect(bookServiceSpy.getBooks).toHaveBeenCalledWith(1, '', 'title', 'asc', [BookStatus.AVAILABLE]);
    expect(component.books.length).toBe(4);
    expect(component.currentPage).toBe(2);
    expect(component.hasMoreBooks).toBeFalse();
  }));
});
