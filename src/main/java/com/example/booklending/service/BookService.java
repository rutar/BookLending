package com.example.booklending.service;

import com.example.booklending.dto.BookDto;
import com.example.booklending.exceptions.BookAlreadyExistsException;
import com.example.booklending.exceptions.ConflictException;
import com.example.booklending.model.Book;
import com.example.booklending.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;

    @Transactional
    public Optional<BookDto> createBook(BookDto bookDto) {
        log.info("Attempting to create a new book with ISBN: {}", bookDto.getIsbn());
        try {
            // Check if the book already exists by ISBN
            if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
                log.warn("Book with ISBN already exists: {}", bookDto.getIsbn());
                throw new BookAlreadyExistsException("Book with this ISBN already exists.");
            }

            Book book = modelMapper.map(bookDto, Book.class);
            Book savedBook = bookRepository.save(book);
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
    public void deleteBook(Long id) {
        log.info("Attempting to delete book with ID: {}", id);
        Optional<Book> bookToDelete = bookRepository.findById(id);
        if (bookToDelete.isPresent()) {
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
}
