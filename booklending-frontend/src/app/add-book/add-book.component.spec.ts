import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {AddBookComponent} from './add-book.component';
import {BookDto, BookService, BookStatus} from '../services/book.service';
import {NotificationService} from '../services/notification.service';
import {of, throwError} from 'rxjs';
import {CommonModule} from '@angular/common';

describe('AddBookComponent', () => {
  let component: AddBookComponent;
  let fixture: ComponentFixture<AddBookComponent>;
  let mockBookService: jasmine.SpyObj<BookService>;
  let mockNotificationService: jasmine.SpyObj<NotificationService>;

  beforeEach(async () => {
    mockBookService = jasmine.createSpyObj('BookService', ['addBook']);
    mockNotificationService = jasmine.createSpyObj('NotificationService', ['openDialog']);

    await TestBed.configureTestingModule({
      imports: [
        AddBookComponent, // Import the standalone component here
        ReactiveFormsModule,
        CommonModule
      ],
      providers: [
        { provide: BookService, useValue: mockBookService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddBookComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with default values', () => {
    const form = component.addBookForm;
    expect(form).toBeTruthy();
    expect(form.get('title')?.value).toBe('');
    expect(form.get('author')?.value).toBe('');
    expect(form.get('coverUrl')?.value).toBe('');
    expect(form.get('isbn')?.value).toBe('');
    expect(form.get('status')?.value).toBe('AVAILABLE');
  });

  it('should invalidate the form when required fields are empty', () => {
    component.addBookForm.patchValue({
      title: '',
      author: '',
      coverUrl: '',
      isbn: '',
      status: ''
    });
    expect(component.addBookForm.valid).toBeFalsy();
  });

  it('should validate the form when fields are filled correctly', () => {
    component.addBookForm.patchValue({
      title: 'Test Book',
      author: 'Test Author',
      coverUrl: 'https://example.com/cover.jpg',
      isbn: '9781234567890',
      status: 'AVAILABLE'
    });
    expect(component.addBookForm.valid).toBeTruthy();
  });

  it('should call BookService.addBook and emit bookAdded event on successful form submission', () => {
    const bookData: BookDto = {
      id: 1,  // Include the `id` field to match the `BookDto` interface
      title: 'Test Book',
      author: 'Test Author',
      coverUrl: 'https://example.com/cover.jpg',
      isbn: '9781234567890',
      status: BookStatus.AVAILABLE
    };

    mockBookService.addBook.and.returnValue(of(bookData));
    spyOn(component.bookAdded, 'emit');

    component.addBookForm.setValue({
      title: bookData.title,
      author: bookData.author,
      coverUrl: bookData.coverUrl,
      isbn: bookData.isbn,
      status: bookData.status
    });
    component.onSubmit();

    expect(mockBookService.addBook).toHaveBeenCalledWith({
      title: bookData.title,
      author: bookData.author,
      coverUrl: bookData.coverUrl,
      isbn: bookData.isbn,
      status: bookData.status
    });
    expect(mockNotificationService.openDialog).toHaveBeenCalledWith('Book added successfully!', false);
    expect(component.bookAdded.emit).toHaveBeenCalled();
    expect(component.addBookForm.valid).toBeFalsy(); // Form should be reset after submission
  });

  it('should show error dialog if adding book fails', () => {
    const bookData = {
      title: 'Test Book',
      author: 'Test Author',
      coverUrl: 'https://example.com/cover.jpg',
      isbn: '9781234567890',
      status: 'AVAILABLE'
    };

    mockBookService.addBook.and.returnValue(throwError(() => new Error('Failed to add book')));

    component.addBookForm.setValue(bookData);
    component.onSubmit();

    expect(mockNotificationService.openDialog).toHaveBeenCalledWith('Failed to add book. Please try again.', true);
  });

  it('should show validation error dialog when form is invalid on submit', () => {
    component.addBookForm.patchValue({
      title: '',
      author: '',
      coverUrl: '',
      isbn: '',
      status: ''
    });

    component.onSubmit();
    expect(mockNotificationService.openDialog).toHaveBeenCalledWith('Please fill out all fields correctly.', true);
  });

  it('should emit closeModal when escape key is pressed', () => {
    spyOn(component.closeModal, 'emit');

    const event = new KeyboardEvent('keydown', { key: 'Escape' });
    component.onEscapePress(event);

    expect(component.closeModal.emit).toHaveBeenCalled();
  });
});
