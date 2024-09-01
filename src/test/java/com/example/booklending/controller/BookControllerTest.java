package com.example.booklending.controller;

import com.example.booklending.dto.BookDto;
import com.example.booklending.exception.ConflictException;
import com.example.booklending.model.PagedResponse;
import com.example.booklending.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private BookDto bookDto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        bookDto = new BookDto();  // Initialize with proper test values
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");
    }

    @Test
    public void testCreateBookSuccess() {
        when(bookService.createBook(any(BookDto.class), anyString())).thenReturn(Optional.of(bookDto));

        ResponseEntity<?> response = bookController.createBook(bookDto, "testUser");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getHeaders().getLocation()).toString().contains("/api/books/1"));
        assertEquals(bookDto, response.getBody());
    }

    @Test
    public void testCreateBookConflict() {
        when(bookService.createBook(any(BookDto.class), anyString())).thenThrow(new ConflictException("Book already exists"));

        ResponseEntity<?> response = bookController.createBook(bookDto, "testUser");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Book already exists", response.getBody());
    }

    @Test
    public void testCreateBookInternalError() {
        when(bookService.createBook(any(BookDto.class), anyString())).thenThrow(new RuntimeException());

        ResponseEntity<?> response = bookController.createBook(bookDto, "testUser");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetBookByIdSuccess() {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.of(bookDto));

        ResponseEntity<BookDto> response = bookController.getBookById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookDto, response.getBody());
    }

    @Test
    public void testGetBookByIdNotFound() {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<BookDto> response = bookController.getBookById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetBookByIsbnSuccess() {
        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.of(bookDto));

        ResponseEntity<BookDto> response = bookController.getBookByIsbn("123-456");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookDto, response.getBody());
    }

    @Test
    public void testGetBookByIsbnNotFound() {
        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.empty());

        ResponseEntity<BookDto> response = bookController.getBookByIsbn("123-456");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllBooks() {
        PagedResponse<BookDto> pagedResponse = new PagedResponse<>();
        when(bookService.getBooks(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(pagedResponse);

        ResponseEntity<PagedResponse<BookDto>> response = bookController.getAllBooks("search", "0", "10", "title", "asc", "available");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pagedResponse, response.getBody());
    }

    @Test
    public void testUpdateBookSuccess() {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.of(bookDto));
        when(bookService.updateBook(anyLong(), any(BookDto.class))).thenReturn(Optional.of(bookDto));

        ResponseEntity<BookDto> response = bookController.updateBook(1L, bookDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookDto, response.getBody());
    }

    @Test
    public void testUpdateBookNotFound() {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<BookDto> response = bookController.updateBook(1L, bookDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteBookSuccess() {
        doNothing().when(bookService).deleteBook(anyLong(), anyString());

        ResponseEntity<Void> response = bookController.deleteBook(1L, "testUser");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteBookNotFound() {
        doThrow(new EntityNotFoundException()).when(bookService).deleteBook(anyLong(), anyString());

        ResponseEntity<Void> response = bookController.deleteBook(1L, "testUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteBookInternalError() {
        doThrow(new RuntimeException()).when(bookService).deleteBook(anyLong(), anyString());

        ResponseEntity<Void> response = bookController.deleteBook(1L, "testUser");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
