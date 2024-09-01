package com.example.booklending.repository;

import com.example.booklending.AbstractIntegrationTest;
import com.example.booklending.model.Book;
import com.example.booklending.model.BookStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag("integration")
@Transactional
public class BookRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void testFindByIsbn_whenBookExists() {
        Book book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        Optional<Book> found = bookRepository.findByIsbn("1234567890");

        assertTrue(found.isPresent());
        assertEquals("Test Book", found.get().getTitle());
    }

    @Test
    void testFindByIsbn_whenBookDoesNotExist() {
        Optional<Book> found = bookRepository.findByIsbn("9876543210");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll_withSpecificationAndSort() {
        Book book1 = new Book();
        book1.setTitle("ABC");
        book1.setAuthor("Author 1");
        book1.setIsbn("1234567891");
        book1.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("XYZ");
        book2.setAuthor("Author 2");
        book2.setStatus(BookStatus.RESERVED);
        book2.setIsbn("1234567892");

        bookRepository.save(book2);

        Book book3 = new Book();
        book3.setTitle("DEF");
        book3.setAuthor("Author 1");
        book3.setIsbn("1234567893");
        book3.setStatus(BookStatus.LENT_OUT);
        bookRepository.save(book3);

        Specification<Book> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("author"), "Author 1");
        Sort sort = Sort.by(Sort.Direction.DESC, "title");

        List<Book> books = bookRepository.findAll(spec, sort);

        assertEquals(2, books.size());
        assertEquals("DEF", books.get(0).getTitle());
        assertEquals("ABC", books.get(1).getTitle());
    }
}