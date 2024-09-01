package com.example.booklending.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
class BookTest {

    @Test
    public void testBookCreation() {
        // Arrange
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setIsbn("978-0134685991");
        book.setStatus(BookStatus.AVAILABLE);
        book.setCoverUrl("http://example.com/effective-java.jpg");

        // Act & Assert
        assertNotNull(book);
        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals("978-0134685991", book.getIsbn());
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        assertEquals("http://example.com/effective-java.jpg", book.getCoverUrl());
    }

    @Test
    public void testBookDefaultConstructor() {
        // Act
        Book book = new Book();

        // Assert
        assertNotNull(book);
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertNull(book.getIsbn());
        assertNull(book.getStatus());
        assertNull(book.getCoverUrl());
    }

    @Test
    public void testBookConstructorWithArgs() {
        // Arrange
        Book book = new Book(1L, "Java Concurrency in Practice", "Brian Goetz", "978-0321349606", BookStatus.RESERVED, "http://example.com/java-concurrency.jpg");

        // Act & Assert
        assertNotNull(book);
        assertEquals(1L, book.getId());
        assertEquals("Java Concurrency in Practice", book.getTitle());
        assertEquals("Brian Goetz", book.getAuthor());
        assertEquals("978-0321349606", book.getIsbn());
        assertEquals(BookStatus.RESERVED, book.getStatus());
        assertEquals("http://example.com/java-concurrency.jpg", book.getCoverUrl());
    }
}
