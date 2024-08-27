import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {of, throwError} from 'rxjs';
import {AddBookComponent} from './add-book.component';
import {BookDto, BookService} from '../services/book.service';
import {NotificationService} from '../services/notification.service';

describe('AddBookComponent', () => {
  let component: AddBookComponent;
  let fixture: ComponentFixture<AddBookComponent>;
  let booksService: jasmine.SpyObj<BookService>;
  let notificationService: jasmine.SpyObj<NotificationService>;

  beforeEach(async () => {
    const booksServiceSpy = jasmine.createSpyObj('BooksService', ['addBook']);
    const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['openDialog']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, AddBookComponent],
      providers: [
        FormBuilder,
        { provide: BookService, useValue: booksServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AddBookComponent);
    component = fixture.componentInstance;
    booksService = TestBed.inject(BookService) as jasmine.SpyObj<BookService>;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;

    // Define a mock Book object
    const mockBook: BookDto = {
      id: 1,  // Mock ID
      title: 'New Book',
      author: 'Author Name',
      isbn: '1234567890',
      status: 'available',
      coverUrl: 'http://example.com/cover.jpg'
    };

    // Set up spies
    booksService.addBook.and.returnValue(of(mockBook)); // Mock successful response
    notificationService.openDialog.and.stub(); // Mock dialog method
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with default values', () => {
    expect(component.addBookForm.value).toEqual({
      title: '',
      author: '',
      coverUrl: '',
      isbn: '',
      status: 'available'
    });
  });

  it('should call addBook on BookService when form is valid and submitted', () => {
    component.addBookForm.setValue({
      title: 'New Book',
      author: 'Author Name',
      coverUrl: 'http://example.com/cover.jpg',
      isbn: '1234567890',
      status: 'available'
    });

    component.onSubmit();

    expect(booksService.addBook).toHaveBeenCalledWith({
      title: 'New Book',
      author: 'Author Name',
      coverUrl: 'http://example.com/cover.jpg',
      isbn: '1234567890',
      status: 'available'
    });

    expect(notificationService.openDialog).toHaveBeenCalledWith('Book added successfully!', false);
  });

  it('should display an error dialog if addBook fails', () => {
    booksService.addBook.and.returnValue(throwError(() => new Error('Failed to add book')));

    component.addBookForm.setValue({
      title: 'New Book',
      author: 'Author Name',
      coverUrl: 'http://example.com/cover.jpg',
      isbn: '1234567890',
      status: 'available'
    });

    component.onSubmit();

    expect(notificationService.openDialog).toHaveBeenCalledWith('Failed to add book. Please try again.', true);
  });

  it('should display a dialog if form is invalid on submit', () => {
    component.addBookForm.setValue({
      title: '',
      author: '',
      coverUrl: '',
      isbn: '',
      status: 'available'
    });

    component.onSubmit();

    expect(notificationService.openDialog).toHaveBeenCalledWith('Please fill out all fields correctly.', true);
  });

  it('should reset the form and emit bookAdded event after successful submission', () => {
    component.addBookForm.setValue({
      title: 'New Book',
      author: 'Author Name',
      coverUrl: 'http://example.com/cover.jpg',
      isbn: '1234567890',
      status: 'available'
    });

    spyOn(component.bookAdded, 'emit');

    component.onSubmit();

    // Expect the form to be reset
    expect(component.addBookForm.value).toEqual({
      title: '',
      author: '',
      coverUrl: '',
      isbn: '',
      status: 'available'
    });
    expect(component.bookAdded.emit).toHaveBeenCalled();
  });
});
