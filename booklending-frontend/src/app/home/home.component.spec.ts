import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {HomeComponent} from './home.component';
import {AuthService} from '../services/auth.service';
import {BookDto, BookService, BookStatus, PagedResponse} from '../services/book.service';
import {NotificationService} from '../services/notification.service';
import {of, throwError} from 'rxjs';
import {FormsModule} from '@angular/forms';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let bookServiceSpy: jasmine.SpyObj<BookService>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['getUsername']);
    const bookSpy = jasmine.createSpyObj('BookService', ['getBooks', 'reserveBook', 'cancelReservation', 'markAsReceived', 'markAsReturned']);
    const notificationSpy = jasmine.createSpyObj('NotificationService', ['openDialog']);

    await TestBed.configureTestingModule({
      imports: [HomeComponent, FormsModule],  // HomeComponent is now in imports
      providers: [
        {provide: AuthService, useValue: authSpy},
        {provide: BookService, useValue: bookSpy},
        {provide: NotificationService, useValue: notificationSpy}
      ]
    }).compileComponents();

    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    bookServiceSpy = TestBed.inject(BookService) as jasmine.SpyObj<BookService>;
    notificationServiceSpy = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch books on init', fakeAsync(() => {
    const mockResponse: PagedResponse<BookDto> = {
      content: [{
        id: 1,
        title: 'Book 1',
        author: 'Author 1',
        isbn: '1234',
        status: BookStatus.AVAILABLE,
        coverUrl: 'url1'
      }],
      totalPages: 1,
      totalElements: 1,
      last: true,
      size: 1,
      number: 0
    };
    bookServiceSpy.getBooks.and.returnValue(of(mockResponse));

    fixture.detectChanges(); // Triggers ngOnInit
    tick();

    expect(bookServiceSpy.getBooks).toHaveBeenCalledWith(0, '', 'title', 'asc', undefined);
    expect(component.books.length).toBe(1);
    expect(component.totalPages).toBe(1);
    expect(component.hasMoreBooks).toBeFalse();
  }));

  it('should handle error when fetching books', fakeAsync(() => {
    bookServiceSpy.getBooks.and.returnValue(throwError(() => new Error('Network error')));

    fixture.detectChanges();
    tick();

    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Failed to load books. Please try again later.', true);
  }));

  it('should trigger search and reset pagination', fakeAsync(() => {
    component.searchQuery = 'test';
    const mockResponse: PagedResponse<BookDto> = {
      content: [{
        id: 1,
        title: 'Test Book',
        author: 'Test Author',
        isbn: '1234',
        status: BookStatus.AVAILABLE,
        coverUrl: 'url1'
      }],
      totalPages: 1,
      totalElements: 1,
      last: true,
      size: 1,
      number: 0
    };
    bookServiceSpy.getBooks.and.returnValue(of(mockResponse));

    component.onSearch();
    tick();

    expect(bookServiceSpy.getBooks).toHaveBeenCalledWith(0, 'test', 'title', 'asc', undefined);
    expect(component.books.length).toBe(1);
    expect(component.currentPage).toBe(1);
  }));

  it('should reserve a book successfully', fakeAsync(() => {
    const bookId = 1;
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.reserveBook.and.returnValue(of({} as BookDto));
    spyOn(component, 'onSearch');

    component.reserveBook(bookId);
    tick();

    expect(bookServiceSpy.reserveBook).toHaveBeenCalledWith('testUser', bookId);
    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Book reserved successfully!', false);
    expect(component.onSearch).toHaveBeenCalled();
  }));

  it('should handle error when reserving a book', fakeAsync(() => {
    const bookId = 1;
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.reserveBook.and.returnValue(throwError(() => ({status: 404})));

    component.reserveBook(bookId);
    tick();

    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Book not found.', true);
  }));

  it('should cancel reservation successfully', fakeAsync(() => {
    const bookId = 1;
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.cancelReservation.and.returnValue(of({} as BookDto));
    spyOn(component, 'onSearch');

    component.cancelReservation(bookId);
    tick();

    expect(bookServiceSpy.cancelReservation).toHaveBeenCalledWith('testUser', bookId);
    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Reservation cancelled successfully!', false);
    expect(component.onSearch).toHaveBeenCalled();
  }));

  it('should mark book as received successfully', fakeAsync(() => {
    const bookId = 1;
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.markAsReceived.and.returnValue(of({} as BookDto));
    spyOn(component, 'onSearch');

    component.markAsReceived(bookId);
    tick();

    expect(bookServiceSpy.markAsReceived).toHaveBeenCalledWith('testUser', bookId);
    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Book marked as received!', false);
    expect(component.onSearch).toHaveBeenCalled();
  }));

  it('should mark book as returned successfully', fakeAsync(() => {
    const bookId = 1;
    authServiceSpy.getUsername.and.returnValue('testUser');
    bookServiceSpy.markAsReturned.and.returnValue(of({} as BookDto));
    spyOn(component, 'onSearch');

    component.markAsReturned(bookId);
    tick();

    expect(bookServiceSpy.markAsReturned).toHaveBeenCalledWith('testUser', bookId);
    expect(notificationServiceSpy.openDialog).toHaveBeenCalledWith('Book returned successfully!', false);
    expect(component.onSearch).toHaveBeenCalled();
  }));

  it('should load more books on scroll', fakeAsync(() => {
    const mockResponse: PagedResponse<BookDto> = {
      content: [{
        id: 2,
        title: 'Book 2',
        author: 'Author 2',
        isbn: '5678',
        status: BookStatus.AVAILABLE,
        coverUrl: 'url2'
      }],
      totalPages: 2,
      totalElements: 2,
      last: true,
      size: 1,
      number: 1
    };
    bookServiceSpy.getBooks.and.returnValue(of(mockResponse));
    component.books = [{
      id: 1,
      title: 'Book 1',
      author: 'Author 1',
      isbn: '1234',
      status: BookStatus.AVAILABLE,
      coverUrl: 'url1'
    }];
    component.currentPage = 1;
    component.hasMoreBooks = true;

    component.onScroll(new Event('scroll'));
    tick();

    expect(bookServiceSpy.getBooks).toHaveBeenCalledWith(1, '', 'title', 'asc', undefined);
    expect(component.books.length).toBe(2);
    expect(component.currentPage).toBe(2);
  }));
});
