package com.example.booklending.dto;

import com.example.booklending.model.BookStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Book")
public class BookDto {

    @Schema(description = "Unique identifier of the book", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotEmpty(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "Title of the book", example = "The Great Gatsby", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotEmpty(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald", requiredMode = Schema.RequiredMode.REQUIRED)
    private String author;

    @NotEmpty(message = "ISBN is required")
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    @Schema(description = "ISBN of the book", example = "9780743273565", requiredMode = Schema.RequiredMode.REQUIRED)
    private String isbn;

    @Schema(description = "Status of the book (available, lent_out, reserved)", example = "available")
    private BookStatus status;

    @Schema(description = "URL of the cover image of the book", example = "https://example.com/cover.jpg")
    private String coverUrl;

}
