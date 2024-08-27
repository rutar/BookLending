package com.example.booklending.repository;

import com.example.booklending.AbstractIntegrationTest;
import com.example.booklending.model.Book;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.example.booklending.model.BookStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the {@link BookRepository}.
 * <p>
 * This class tests the functionality of the {@link BookRepository#searchByTitleOrAuthorOrIsbn(String)} method
 * to ensure it correctly searches books by title, author, or ISBN.
 * </p>
 */
@SpringBootTest
@Tag("integration")
@Transactional
public class BookRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void testSaveAndFindBookByIsbn() {
        // Create and save a book
        Book book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setStatus(BookStatus.AVAILABLE);

        bookRepository.save(book);

        // Find the book by ISBN
        Optional<Book> foundBook = bookRepository.findByIsbn("1234567890");

        assertTrue(foundBook.isPresent(), "Book should be found by ISBN");
        assertEquals("Test Book", foundBook.get().getTitle(), "Book title should match");
        assertEquals("Test Author", foundBook.get().getAuthor(), "Book author should match");
        assertEquals(BookStatus.AVAILABLE, foundBook.get().getStatus(), "Book status should match");
    }

    @Test
    void testSearchByTitle() {
        // Create and save books
        Book book1 = new Book(null, "Effective Java", "Joshua Bloch", "978-0134685991", BookStatus.AVAILABLE, "cover_url");
        Book book2 = new Book(null, "Clean Code", "Robert C. Martin", "978-0132350884", BookStatus.AVAILABLE, "cover_url");
        bookRepository.save(book1);
        bookRepository.save(book2);

        // Search by title
        List<Book> results = bookRepository.searchByTitleOrAuthorOrIsbn("Effective");

        assertEquals(1, results.size(), "There should be exactly one book found by title");
        assertEquals("Effective Java", results.get(0).getTitle(), "The title of the found book should match");
    }

    @Test
    void testSearchByAuthor() {
        // Create and save books
        Book book1 = new Book(null, "Effective Java", "Joshua Bloch", "978-0134685991", BookStatus.AVAILABLE, "cover_url");
        Book book2 = new Book(null, "Clean Code", "Robert C. Martin", "978-0132350884", BookStatus.AVAILABLE, "cover_url");
        bookRepository.save(book1);
        bookRepository.save(book2);

        // Search by author
        List<Book> results = bookRepository.searchByTitleOrAuthorOrIsbn("Robert");

        assertEquals(1, results.size(), "There should be exactly one book found by author");
        assertEquals("Clean Code", results.get(0).getTitle(), "The title of the found book should match");
    }

    @Test
    void testSearchByIsbn() {
        // Create and save books
        Book book1 = new Book(null, "Effective Java", "Joshua Bloch", "978-0134685991", BookStatus.AVAILABLE, "cover_url");
        Book book2 = new Book(null, "Clean Code", "Robert C. Martin", "978-0132350884", BookStatus.AVAILABLE, "cover_url");
        bookRepository.save(book1);
        bookRepository.save(book2);

        // Search by ISBN
        List<Book> results = bookRepository.searchByTitleOrAuthorOrIsbn("978-0134685991");

        assertEquals(1, results.size(), "There should be exactly one book found by ISBN");
        assertEquals("Effective Java", results.get(0).getTitle(), "The title of the found book should match");
    }

    @Test
    void testSearchByNonexistentQuery() {
        // Create and save books
        Book book1 = new Book(null, "Effective Java", "Joshua Bloch", "978-0134685991", BookStatus.AVAILABLE, "cover_url");
        Book book2 = new Book(null, "Clean Code", "Robert C. Martin", "978-0132350884", BookStatus.AVAILABLE, "cover_url");
        bookRepository.save(book1);
        bookRepository.save(book2);

        // Search with a query that does not match any book
        List<Book> results = bookRepository.searchByTitleOrAuthorOrIsbn("Nonexistent");

        assertTrue(results.isEmpty(), "The search result should be empty for a nonexistent query");
    }
}
