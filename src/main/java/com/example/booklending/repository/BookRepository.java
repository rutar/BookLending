package com.example.booklending.repository;

import com.example.booklending.model.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {


    /**
     * Find a book by its ISBN.
     *
     * @param isbn the ISBN of the book
     * @return an Optional containing the book if found, or empty if not found
     */
    Optional<Book> findByIsbn(String isbn);


    List<Book> findAll(Specification<Book> spec, Sort sort);
}
