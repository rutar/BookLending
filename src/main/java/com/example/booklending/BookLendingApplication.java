package com.example.booklending;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Book Lending API",
                version = "v1",
                description = "API for managing book rentals"
        )
)
@SpringBootApplication
public class BookLendingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookLendingApplication.class, args);
    }

}
