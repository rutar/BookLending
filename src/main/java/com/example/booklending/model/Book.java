package com.example.booklending.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Represents a book in the library system.")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The unique ID of the book", example = "1", required = true, position = 1)
    private Long id;

    @Column(name = "title", nullable = false)
    @ApiModelProperty(notes = "The title of the book", example = "The Great Gatsby", required = true, position = 2)
    private String title;

    @Column(name = "author", nullable = false)
    @ApiModelProperty(notes = "The author of the book", example = "F. Scott Fitzgerald", required = true, position = 3)
    private String author;

    @Column(name = "isbn", unique = true, nullable = false)
    @ApiModelProperty(notes = "The ISBN of the book", example = "978-0743273565", required = true, position = 4)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @ApiModelProperty(notes = "The status of the book (e.g., AVAILABLE, BORROWED, RESERVED)", example = "AVAILABLE", required = true, position = 5)
    private BookStatus status;

    @Column(name = "cover_url")
    @ApiModelProperty(notes = "The URL of the book's cover image", example = "http://example.com/cover.jpg", position = 6)
    private String coverUrl;
}
