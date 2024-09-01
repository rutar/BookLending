package com.example.booklending.service;

import com.example.booklending.dto.BookDto;
import com.example.booklending.exception.ConflictException;
import com.example.booklending.model.Action;
import com.example.booklending.model.Book;
import com.example.booklending.model.PagedResponse;
import com.example.booklending.model.User;
import com.example.booklending.repository.ActionRepository;
import com.example.booklending.repository.BookRepository;
import com.example.booklending.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActionRepository actionRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBook_Success() {
        BookDto bookDto = new BookDto();
        bookDto.setIsbn("1234567890");
        Book book = new Book();
        User user = new User();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());
        when(modelMapper.map(any(BookDto.class), eq(Book.class))).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(modelMapper.map(any(Book.class), eq(BookDto.class))).thenReturn(bookDto);

        Optional<BookDto> result = bookService.createBook(bookDto, "username");

        assertTrue(result.isPresent());
        verify(actionRepository, times(1)).save(any(Action.class));
    }

    @Test
    void createBook_AlreadyExists() {
        BookDto bookDto = new BookDto();
        bookDto.setIsbn("1234567890");
        User user = new User();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.of(new Book()));

        assertThrows(ConflictException.class, () -> bookService.createBook(bookDto, "username"));
    }

    @Test
    void getBookById_Success() {
        Long bookId = 1L;
        Book book = new Book();
        BookDto bookDto = new BookDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        Optional<BookDto> result = bookService.getBookById(bookId);

        assertTrue(result.isPresent());
        assertEquals(bookDto, result.get());
    }

    @Test
    void updateBook_Success() {
        Long bookId = 1L;
        BookDto bookDto = new BookDto();
        Book book = new Book();

        when(modelMapper.map(bookDto, Book.class)).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(modelMapper.map(book, BookDto.class)).thenReturn(bookDto);

        Optional<BookDto> result = bookService.updateBook(bookId, bookDto);

        assertTrue(result.isPresent());
        assertEquals(bookDto, result.get());
    }

    @Test
    void deleteBook_Success() {
        Long bookId = 1L;
        User user = new User();
        Book book = new Book();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertDoesNotThrow(() -> bookService.deleteBook(bookId, "username"));

        verify(actionRepository, times(1)).save(any(Action.class));
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void deleteBook_NotFound() {
        Long bookId = 1L;
        User user = new User();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.deleteBook(bookId, "username"));
    }

    @Test
    void getBooks_Success() {
        BookDto bookDto = new BookDto();
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(new Book()));

        when(bookRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(bookPage);
        when(modelMapper.map(any(Book.class), eq(BookDto.class))).thenReturn(bookDto);

        PagedResponse<BookDto> result = bookService.getBooks("query", "0", "10", "title", "asc", "AVAILABLE");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(bookDto, result.getContent().get(0));
    }
}