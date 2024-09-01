package com.example.booklending.controller;

import com.example.booklending.dto.ActionDto;
import com.example.booklending.service.ActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actions")
@Tag(name = "Actions", description = "Endpoints for managing actions such as reservation, cancellation, and status updates")
public class ActionController {

    private final ActionService actionService;

    @Autowired
    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @Operation(summary = "Reserve a book", description = "Allows a user to reserve a book.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book reserved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or book unavailable"),
            @ApiResponse(responseCode = "404", description = "Book or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/reserve")
    public ResponseEntity<ActionDto> reserveBook(
            @Parameter(description = "User name making the reservation", required = true) @RequestParam String userName,
            @Parameter(description = "Book ID to be reserved", required = true) @RequestParam Long bookId) {
        try {
            ActionDto action = actionService.reserveBook(userName, bookId);
            return new ResponseEntity<>(action, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cancel a reservation", description = "Allows a user to cancel a book reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation canceled successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionDto.class))),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/cancel_reservation")
    public ResponseEntity<ActionDto> cancelReservation(
            @Parameter(description = "User name making the cancellation", required = true) @RequestParam String userName,
            @Parameter(description = "Book ID to cancel reservation", required = true) @RequestParam Long bookId) {
        try {
            ActionDto action = actionService.cancelReservation(userName, bookId);
            return new ResponseEntity<>(action, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Mark book as lent out", description = "Allows a library admin to mark a reserved book as lent out.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book marked as lent out successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionDto.class))),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/lent_out")
    public ResponseEntity<ActionDto> markAsLentOut(
            @Parameter(description = "Book ID being lent out", required = true) @RequestParam Long bookId) {
        try {
            ActionDto action = actionService.markAsLentOut(bookId);
            return new ResponseEntity<>(action, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Mark book as received", description = "Allows a user to mark a reserved book as received.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book marked as received successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionDto.class))),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/received")
    public ResponseEntity<ActionDto> markAsReceived(
            @Parameter(description = "User name receiving the book", required = true) @RequestParam String userName,
            @Parameter(description = "Book ID being received", required = true) @RequestParam Long bookId) {
        try {
            ActionDto action = actionService.markAsReceived(userName, bookId);
            return new ResponseEntity<>(action, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Mark book as returned", description = "Allows a user to mark a borrowed book as returned.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book marked as returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionDto.class))),
            @ApiResponse(responseCode = "404", description = "Book not found or not borrowed by user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/returned")
    public ResponseEntity<ActionDto> markAsReturned(
            @Parameter(description = "User name returning the book", required = true) @RequestParam String userName,
            @Parameter(description = "Book ID being returned", required = true) @RequestParam Long bookId) {
        try {
            ActionDto action = actionService.markAsReturned(userName, bookId);
            return new ResponseEntity<>(action, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
