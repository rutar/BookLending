package com.example.booklending.repository;

import com.example.booklending.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find a book by its ISBN.
     *
     * @param isbn the ISBN of the book
     * @return an Optional containing the book if found, or empty if not found
     */
    Optional<Book> findByIsbn(String isbn);
}
