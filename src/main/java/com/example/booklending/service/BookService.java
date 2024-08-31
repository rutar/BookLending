package com.example.booklending.service;

import com.example.booklending.dto.BookDto;
import com.example.booklending.exception.BookAlreadyExistsException;
import com.example.booklending.exception.ConflictException;
import com.example.booklending.model.*;
import com.example.booklending.repository.ActionRepository;
import com.example.booklending.repository.BookRepository;
import com.example.booklending.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    @Value("${spring.application.defaults.page-size}")
    private String defaultPageSize;


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

    public PagedResponse<BookDto> getBooks(String searchQuery, String page, String size, String sortBy, String order, String statuses) {
        log.info("Fetching books with search query: {}, sorting by: {}, order: {}", searchQuery, sortBy, order);

        // Create a sorting object based on the sortBy and order parameters
        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        // Create a specification for filtering
        Specification<Book> spec = Specification.where(null);
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String lowerCaseSearchQuery = "%" + searchQuery.toLowerCase() + "%";

            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), lowerCaseSearchQuery),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("isbn")), lowerCaseSearchQuery),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), lowerCaseSearchQuery)
                    )
            );
        }

        // Add status filtering
        if (statuses != null && !statuses.isEmpty()) {
            // Split the statuses string into an array
            String[] statusArray = statuses.split(",");
            List<BookStatus> statusList = Arrays.stream(statusArray)
                    .map(String::trim)
                    .map(BookStatus::valueOf)
                    .collect(Collectors.toList());

            spec = spec.and((root, query, criteriaBuilder) ->
                    root.get("status").in(statusList)
            );
        }

        // Parsing and handling the size parameter
        int pageSize;
        try {
            pageSize = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            log.error("Invalid size parameter: {}. Defaulting to {} results.", size, defaultPageSize);
            pageSize = Integer.parseInt(size); // Default size if parsing fails
        }

        // Parsing and handling the page parameter
        int pageNumber;
        try {
            pageNumber = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            log.error("Invalid page parameter: {}. Defaulting to page 0.", page);
            pageNumber = 0; // Default page if parsing fails
        }

        // Limiting the number of results
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Fetch the filtered and sorted list of books
        Page<BookDto> bookPage = bookRepository.findAll(spec, pageable)
                .map(book -> {
                    log.debug("Mapping book entity to DTO for book ID: {}", book.getId());
                    return modelMapper.map(book, BookDto.class);
                });

        return new PagedResponse<>(
                bookPage.getContent(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast(),
                bookPage.getSize(),
                bookPage.getNumber()
        );
    }
}
