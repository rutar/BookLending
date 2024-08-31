package com.example.booklending.controller;

import com.example.booklending.dto.BookDto;
import com.example.booklending.exception.ConflictException;
import com.example.booklending.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Endpoints for managing books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Create a new book", description = "Creates a new book with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody BookDto bookDto, @RequestParam String userName) {
        try {
            Optional<BookDto> savedBookDto = bookService.createBook(bookDto, userName);

            return savedBookDto
                    .map(book -> ResponseEntity
                            .created(URI.create("/api/books/" + book.getId()))
                            .body(book))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        } catch (ConflictException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get book by ID", description = "Fetches a book based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@Parameter(description = "ID of the book to fetch") @PathVariable Long id) {
        Optional<BookDto> bookDto = bookService.getBookById(id);
        return bookDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get book by ISBN", description = "Fetches a book based on the provided ISBN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDto> getBookByIsbn(@Parameter(description = "ISBN of the book to fetch") @PathVariable String isbn) {
        Optional<BookDto> bookDto = bookService.getBookByIsbn(isbn);
        return bookDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get all books", description = "Fetches a list of all books.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of books retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        List<BookDto> books = bookService.getBooks(searchQuery, sortBy, order);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @Operation(summary = "Update a book", description = "Updates the details of an existing book.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(
            @Parameter(description = "ID of the book to update")
            @PathVariable Long id, @RequestBody BookDto bookDtoNew) {
        Optional<BookDto> bookDtoCurrent = bookService.getBookById(id);
        if (bookDtoCurrent.isPresent()) {
            Optional<BookDto> bookDtoUpdated = bookService.updateBook(id, bookDtoNew);
            return bookDtoUpdated.map(bookDto -> new ResponseEntity<>(bookDto, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete a book", description = "Deletes a book based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@Parameter(description = "ID of the book to delete") @PathVariable Long id,  @RequestParam String userName) {

        try {
            bookService.deleteBook(id, userName);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
