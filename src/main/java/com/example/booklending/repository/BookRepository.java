package com.example.booklending.repository;

import com.example.booklending.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Searches for books by title, author, or ISBN.
     * <p>
     * This method performs a case-insensitive search across the title, author, and ISBN fields.
     * It returns a list of books where any of these fields contain the provided query string.
     * </p>
     *
     * @param query the search query to match against the title, author, and ISBN fields.
     *              The search is case-insensitive and matches any part of the fields.
     * @return a list of {@link Book} objects that match the search criteria.
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR b.isbn LIKE CONCAT('%', :query, '%')")
    List<Book> searchByTitleOrAuthorOrIsbn(@Param("query") String query);
}
