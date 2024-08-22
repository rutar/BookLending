package com.example.booklending.repository;

import com.example.booklending.AbstractIntegrationTest;
import com.example.booklending.model.Book;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        book.setStatus("available");

        bookRepository.save(book);

        // Find the book by ISBN
        Optional<Book> foundBook = bookRepository.findByIsbn("1234567890");

        assertTrue(foundBook.isPresent(), "Book should be found by ISBN");
        assertEquals("Test Book", foundBook.get().getTitle(), "Book title should match");
        assertEquals("Test Author", foundBook.get().getAuthor(), "Book author should match");
        assertEquals("available", foundBook.get().getStatus(), "Book status should match");
    }

    @Test
    void testFindBookByNonexistentIsbn() {
        // Try to find a book that does not exist
        Optional<Book> foundBook = bookRepository.findByIsbn("0000000000");

        assertTrue(foundBook.isEmpty(), "Book with nonexistent ISBN should not be found");
    }
}
