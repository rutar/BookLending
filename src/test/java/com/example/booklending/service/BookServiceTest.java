package com.example.booklending.service;

import com.example.booklending.dto.BookDto;
import com.example.booklending.exceptions.ConflictException;
import com.example.booklending.model.Book;
import com.example.booklending.model.BookStatus;
import com.example.booklending.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setStatus(BookStatus.AVAILABLE);

        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");
        bookDto.setIsbn("1234567890");
        bookDto.setStatus(BookStatus.AVAILABLE);
    }

    @Test
    void createBook_shouldReturnCreatedBookDto() {
        when(modelMapper.map(bookDto, Book.class)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        Optional<BookDto> result = bookService.createBook(bookDto);

        assertEquals(Optional.of(bookDto), result);
        verify(bookRepository).save(book);
    }

    @Test
    void createBook_shouldThrowConflictExceptionWhenIsbnExists() {
        when(bookRepository.findByIsbn(bookDto.getIsbn())).thenReturn(Optional.of(book));

        assertThrows(ConflictException.class, () -> bookService.createBook(bookDto));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void createBook_shouldReturnEmptyOptionalOnGeneralException() {
        when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Unexpected error"));
        when(modelMapper.map(bookDto, Book.class)).thenReturn(book);

        Optional<BookDto> result = bookService.createBook(bookDto);

        assertTrue(result.isEmpty());
    }

    @Test
    void getBookById_shouldReturnBookDtoWhenBookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        Optional<BookDto> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals(bookDto, result.get());
    }

    @Test
    void getBookById_shouldReturnEmptyOptionalWhenBookDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<BookDto> result = bookService.getBookById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateBook_shouldReturnUpdatedBookDto() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(modelMapper.map(bookDto, Book.class)).thenReturn(book);
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);


        Optional<BookDto> result = bookService.updateBook(1L, bookDto);

        assertEquals(Optional.of(bookDto), result);
        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_shouldReturnEmptyOptionalWhenUpdateFails() {
        //   when(modelMapper.map(bookDto, Book.class)).thenReturn(book);
        //   when(bookRepository.save(any(Book.class))).thenReturn(null);  // Simulating a failure in save

        Optional<BookDto> result = bookService.updateBook(1L, bookDto);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteBook_shouldCallDeleteWhenBookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_shouldThrowEntityNotFoundExceptionWhenBookDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    void getAllBooks_shouldReturnListOfBookDtos() {
        List<Book> books = new ArrayList<>();
        books.add(book);
        when(bookRepository.findAll()).thenReturn(books);
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        Iterable<BookDto> result = bookService.getAllBooks();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
        assertEquals(bookDto, result.iterator().next());
    }

    @Test
    void getBookByIsbn_shouldReturnBookDtoWhenBookExists() {
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(book));
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        Optional<BookDto> result = bookService.getBookByIsbn("1234567890");

        assertTrue(result.isPresent());
        assertEquals(bookDto, result.get());
    }

    @Test
    void getBookByIsbn_shouldReturnEmptyOptionalWhenBookDoesNotExist() {
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());

        Optional<BookDto> result = bookService.getBookByIsbn("1234567890");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchBooksByTitle() {
        String query = "Effective";
        Book book = new Book(1L, "Effective Java", "Joshua Bloch", "978-0134685991", BookStatus.AVAILABLE, "cover_url");
        BookDto bookDto = new BookDto(1L, "Effective Java", "Joshua Bloch", "978-0134685991", BookStatus.AVAILABLE, "cover_url");

        when(bookRepository.searchByTitleOrAuthorOrIsbn(query.trim().toLowerCase())).thenReturn(List.of(book));
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        List<BookDto> result = bookService.searchBooks(query);

        assertEquals(1, result.size());
        assertEquals(bookDto, result.get(0));
    }

    @Test
    void testSearchBooksByAuthor() {
        String query = "Robert";
        Book book = new Book(2L, "Clean Code", "Robert C. Martin", "978-0132350884", BookStatus.AVAILABLE, "cover_url");
        BookDto bookDto = new BookDto(2L, "Clean Code", "Robert C. Martin", "978-0132350884", BookStatus.AVAILABLE, "cover_url");

        when(bookRepository.searchByTitleOrAuthorOrIsbn(query.trim().toLowerCase())).thenReturn(List.of(book));
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        List<BookDto> result = bookService.searchBooks(query);

        assertEquals(1, result.size());
        assertEquals(bookDto, result.get(0));
    }

    @Test
    void testSearchBooksByIsbn() {
        String query = "978-0134685991";
        Book book = new Book(1L, "Effective Java", "Joshua Bloch", "978-0134685991", BookStatus.AVAILABLE, "cover_url");
        BookDto bookDto = new BookDto(1L, "Effective Java", "Joshua Bloch", "978-0134685991", BookStatus.AVAILABLE, "cover_url");

        when(bookRepository.searchByTitleOrAuthorOrIsbn(query)).thenReturn(List.of(book));
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        List<BookDto> result = bookService.searchBooks(query);

        assertEquals(1, result.size());
        assertEquals(bookDto, result.get(0));
    }

    @Test
    void testSearchBooksEmptyQuery() {
        List<BookDto> result = bookService.searchBooks("");

        assertEquals(0, result.size());
    }

    @Test
    void testSearchBooksNoResults() {
        String query = "Nonexistent";
        when(bookRepository.searchByTitleOrAuthorOrIsbn(query.trim().toLowerCase())).thenReturn(Collections.emptyList());

        List<BookDto> result = bookService.searchBooks(query);

        assertEquals(0, result.size());
    }
}
