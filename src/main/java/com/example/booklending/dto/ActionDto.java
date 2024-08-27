package com.example.booklending.dto;

import com.example.booklending.model.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Book Action")
public class ActionDto {

    @Schema(description = "Unique identifier for the book action", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Book ID is required")
    @Positive(message = "Book ID must be a positive number")
    @Schema(description = "ID of the book involved in the action", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    @Schema(description = "ID of the user performing the action", example = "202", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull(message = "Action type is required")
    @Schema(description = "Type of the book action (e.g., reserve, receive, return)", example = "RESERVE", requiredMode = Schema.RequiredMode.REQUIRED)
    private ActionType actionType;

    @NotNull(message = "Action date is required")
    @Schema(description = "Date and time when the action was performed", example = "2024-08-27T15:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime actionDate;

    @Schema(description = "Due date for the action (e.g., return date for borrowed books)", example = "2024-09-27T15:30:00")
    private LocalDateTime dueDate;
}
