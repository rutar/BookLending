package com.example.booklending.service;

import com.example.booklending.dto.BookDto;
import com.example.booklending.exception.BookAlreadyExistsException;
import com.example.booklending.exception.ConflictException;
import com.example.booklending.model.Action;
import com.example.booklending.model.ActionType;
import com.example.booklending.model.Book;
import com.example.booklending.model.User;
import com.example.booklending.repository.ActionRepository;
import com.example.booklending.repository.BookRepository;
import com.example.booklending.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ActionRepository actionRepository;

    @Transactional
    public Optional<BookDto> createBook(BookDto bookDto, String userName) {
        log.info("Attempting to create a new book with ISBN: {}", bookDto.getIsbn());
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found"));
        try {
            // Check if the book already exists by ISBN
            if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
                log.warn("Book with ISBN already exists: {}", bookDto.getIsbn());
                throw new BookAlreadyExistsException("Book with this ISBN already exists.");
            }

            Book book = modelMapper.map(bookDto, Book.class);
            Book savedBook = bookRepository.save(book);

            Action action = new Action();
            action.setBook(book);
            action.setUser(user);
            action.setAction(ActionType.ADD_BOOK);
            action.setActionDate(LocalDateTime.now());
            actionRepository.save(action);


            log.info("Book created successfully with ID: {}", savedBook.getId());

            BookDto savedBookDto = modelMapper.map(savedBook, BookDto.class);
            return Optional.of(savedBookDto);

        } catch (BookAlreadyExistsException e) {
            log.error("Conflict occurred during book creation: {}", e.getMessage());
            throw new ConflictException(e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred while creating the book: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<BookDto> getBookById(Long id) {
        log.info("Fetching book with ID: {}", id);
        return bookRepository.findById(id)
                .map(book -> {
                    log.info("Book found with ID: {}", id);
                    return modelMapper.map(book, BookDto.class);
                });
    }

    public Optional<BookDto> getBookByIsbn(String isbn) {
        log.info("Fetching book with ISBN: {}", isbn);
        return bookRepository.findByIsbn(isbn)
                .map(book -> {
                    log.info("Book found with ISBN: {}", isbn);
                    return modelMapper.map(book, BookDto.class);
                });
    }

    @Transactional
    public Optional<BookDto> updateBook(Long id, BookDto bookDtoToUpdate) {
        log.info("Updating book with ID: {}", id);

        try {
            Book bookToUpdate = modelMapper.map(bookDtoToUpdate, Book.class);
            bookToUpdate.setId(id);  // Ensure the ID remains the same
            Book updatedBook = bookRepository.save(bookToUpdate);
            log.info("Book updated successfully with ID: {}", id);

            BookDto updatedBookDto = modelMapper.map(updatedBook, BookDto.class);
            return Optional.of(updatedBookDto);

        } catch (Exception e) {
            log.error("An error occurred while updating the book: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Transactional
    public void deleteBook(Long id, String userName) {

        log.info("Attempting to delete book with ID: {}", id);

        User user = userRepository.findByUsername(userName).orElseThrow(() -> new EntityNotFoundException("User " + userName + " not found"));
        Optional<Book> bookToDelete = bookRepository.findById(id);

        if (bookToDelete.isPresent()) {


            Action action = new Action();
            action.setBook(bookToDelete.get());
            action.setUser(user);
            action.setAction(ActionType.DELETE_BOOK);
            action.setActionDate(LocalDateTime.now());
            actionRepository.save(action);

            bookRepository.delete(bookToDelete.get());
            log.info("Book deleted successfully with ID: {}", id);
        } else {
            log.error("Book not found with ID: {}", id);
            throw new EntityNotFoundException("Book not found with ID: " + id);
        }
    }

    public List<BookDto> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll().stream()
                .map(book -> {
                    log.debug("Mapping book entity to DTO for book ID: {}", book.getId());
                    return modelMapper.map(book, BookDto.class);
                })
                .collect(Collectors.toList());
    }

    /**
     * Searches for books by title, author, or ISBN.
     * <p>
     * This method delegates to the {@link BookRepository#searchByTitleOrAuthorOrIsbn(String)}
     * method to find books that match the search query in the title, author, or ISBN fields.
     * The query is case-insensitive.
     * </p>
     *
     * @param query the search query to match against the title, author, and ISBN fields.
     *              The search is case-insensitive and matches any part of the fields.
     * @return a list of {@link BookDto} objects that match the search criteria.
     */
    public List<BookDto> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        String cleanedQuery = query.trim().toLowerCase();
        List<Book> books = bookRepository.searchByTitleOrAuthorOrIsbn(cleanedQuery);
        return books.stream()
                .map(book -> modelMapper.map(book, BookDto.class))
                .collect(Collectors.toList());
    }
}
