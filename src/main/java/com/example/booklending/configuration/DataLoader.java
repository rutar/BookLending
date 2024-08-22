package com.example.booklending.configuration;

import com.example.booklending.model.Book;
import com.example.booklending.repository.BookRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Configuration
public class DataLoader {

    @Value("${data.populate.enabled}")
    private boolean isDataPopulationEnabled;


    @Bean
    public CommandLineRunner conditionalPopulateData(BookRepository bookRepository) {
        return args -> {
            if (isDataPopulationEnabled) {
                ObjectMapper mapper = new ObjectMapper();
                try (InputStream is = DataLoader.class.getResourceAsStream("/books.json")) {
                    // Deserialize JSON data into a list of Book objects
                    List<Book> books = mapper.readValue(is, new TypeReference<>() {
                    });

                    // Save each book to the repository if it does not already exist
                    for (Book book : books) {
                        Optional<Book> existingBook = bookRepository.findByIsbn(book.getIsbn());
                        if (existingBook.isEmpty()) {
                            bookRepository.save(book);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Failed to load and save books from JSON: " + e.getMessage());
                }
            } else {
                System.out.println("Data population is disabled. No data loaded.");
            }
        };
    }
}


