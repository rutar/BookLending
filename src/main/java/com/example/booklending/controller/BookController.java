package com.example.booklending.controller;

import com.example.booklending.dto.BookDto;
import com.example.booklending.exception.ConflictException;
import com.example.booklending.model.PagedResponse;
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
            @ApiResponse(responseCode = "409", description = "Conflict - Book already exists",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> createBook(
            @Parameter(description = "Details of the book to create", required = true)
            @RequestBody BookDto bookDto,
            @Parameter(description = "Username of the person creating the book", required = true)
            @RequestParam String userName) {
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
    public ResponseEntity<BookDto> getBookById(
            @Parameter(description = "ID of the book to fetch", required = true)
            @PathVariable Long id) {
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
    public ResponseEntity<BookDto> getBookByIsbn(
            @Parameter(description = "ISBN of the book to fetch", required = true)
            @PathVariable String isbn) {
        Optional<BookDto> bookDto = bookService.getBookByIsbn(isbn);
        return bookDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get all books", description = "Fetches a list of all books.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of books retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PagedResponse<BookDto>> getAllBooks(
            @Parameter(description = "Search query for filtering books")
            @RequestParam(required = false) String searchQuery,
            @Parameter(description = "Page number for pagination", schema = @Schema(defaultValue = "0"))
            @RequestParam(defaultValue = "0") String page,
            @Parameter(description = "Number of items per page", schema = @Schema(defaultValue = "200"))
            @RequestParam(defaultValue = "200") String size,
            @Parameter(description = "Field to sort by", schema = @Schema(defaultValue = "title"))
            @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Order of sorting: asc or desc", schema = @Schema(defaultValue = "asc"))
            @RequestParam(defaultValue = "asc") String order,
            @Parameter(description = "Filter books by status")
            @RequestParam(defaultValue = "") String statuses) {
        return new ResponseEntity<>(bookService.getBooks(searchQuery, page, size, sortBy, order, statuses), HttpStatus.OK);
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
            @Parameter(description = "ID of the book to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated details of the book", required = true)
            @RequestBody BookDto bookDtoNew) {
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
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID of the book to delete", required = true)
            @PathVariable Long id,
            @Parameter(description = "Username of the person deleting the book", required = true)
            @RequestParam String userName) {

        try {
            bookService.deleteBook(id, userName);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
