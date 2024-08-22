package com.example.booklending.controller;

import com.example.booklending.dto.BookDto;
import com.example.booklending.service.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("unit")
class BookControllerTest {

    private MockMvc mockMvc;

    private AutoCloseable closeable;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createBook_ShouldReturnCreatedStatus() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");

        when(bookService.createBook(any(BookDto.class))).thenReturn(Optional.of(bookDto));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Book\",\"isbn\":\"1234567890\",\"status\":\"available\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/books/1"))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void getBookById_ShouldReturnBook() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");

        when(bookService.getBookById(anyLong())).thenReturn(Optional.of(bookDto));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void getBookById_ShouldReturnNotFoundStatus_WhenBookNotFound() throws Exception {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookByIsbn_ShouldReturnBook() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setIsbn("1234567890");
        bookDto.setTitle("Test Book");

        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.of(bookDto));

        mockMvc.perform(get("/api/books/isbn/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("1234567890"));
    }

    @Test
    void getBookByIsbn_ShouldReturnNotFoundStatus_WhenBookNotFound() throws Exception {
        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/isbn/0000000000"))
                .andExpect(status().isNotFound());
    }


    @Test
    void getAllBooks_ShouldReturnBooks() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setTitle("Test Book");

        List<BookDto> books = Collections.singletonList(bookDto);

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook() throws Exception {
        BookDto currentBookDto = new BookDto();
        currentBookDto.setId(1L);
        currentBookDto.setTitle("Current Title");

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(1L);
        updatedBookDto.setTitle("Updated Title");

        when(bookService.getBookById(anyLong())).thenReturn(Optional.of(currentBookDto));
        when(bookService.updateBook(anyLong(), any(BookDto.class))).thenReturn(Optional.of(updatedBookDto));

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\",\"isbn\":\"1234567890\",\"status\":\"checked out\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void updateBook_ShouldReturnNotFoundStatus_WhenBookNotFound() throws Exception {
        // Arrange: Mock the service to return an empty Optional for the given ID
        when(bookService.getBookById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert: Perform a PUT request and expect a 404 Not Found status
        mockMvc.perform(put("/api/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nonexistent Title\",\"isbn\":\"0000000000\",\"status\":\"available\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_ShouldReturnNoContentStatus() throws Exception {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.of(new BookDto()));
        doNothing().when(bookService).deleteBook(anyLong());

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_ShouldReturnNotFoundStatus_WhenBookNotFound() throws Exception {
        // Arrange: Mock the service to return an empty Optional for the given ID
        when(bookService.getBookById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert: Perform a DELETE request and expect a 404 Not Found status
        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound());
    }
}
