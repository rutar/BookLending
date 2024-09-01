package com.example.booklending.controller;

import com.example.booklending.dto.ActionDto;
import com.example.booklending.service.ActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Tag("unit")
class ActionControllerTest {

    @Mock
    private ActionService actionService;

    @InjectMocks
    private ActionController actionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void reserveBook_Success() {
        String userName = "testUser";
        Long bookId = 1L;
        ActionDto expectedAction = new ActionDto(); // Assume appropriate constructor or setters
        when(actionService.reserveBook(userName, bookId)).thenReturn(expectedAction);

        ResponseEntity<ActionDto> response = actionController.reserveBook(userName, bookId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedAction, response.getBody());
        verify(actionService).reserveBook(userName, bookId);
    }

    @Test
    void reserveBook_BadRequest() {
        String userName = "testUser";
        Long bookId = 1L;
        when(actionService.reserveBook(userName, bookId)).thenThrow(new RuntimeException("Book unavailable"));

        ResponseEntity<ActionDto> response = actionController.reserveBook(userName, bookId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(actionService).reserveBook(userName, bookId);
    }

    @Test
    void cancelReservation_Success() {
        String userName = "testUser";
        Long bookId = 1L;
        ActionDto expectedAction = new ActionDto(); // Assume appropriate constructor or setters
        when(actionService.cancelReservation(userName, bookId)).thenReturn(expectedAction);

        ResponseEntity<ActionDto> response = actionController.cancelReservation(userName, bookId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAction, response.getBody());
        verify(actionService).cancelReservation(userName, bookId);
    }

    @Test
    void cancelReservation_NotFound() {
        String userName = "testUser";
        Long bookId = 1L;
        when(actionService.cancelReservation(userName, bookId)).thenThrow(new EntityNotFoundException("Reservation not found"));

        ResponseEntity<ActionDto> response = actionController.cancelReservation(userName, bookId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).cancelReservation(userName, bookId);
    }

    @Test
    void markAsLentOut_Success() {
        Long bookId = 1L;
        ActionDto expectedAction = new ActionDto(); // Assume appropriate constructor or setters
        when(actionService.markAsLentOut(bookId)).thenReturn(expectedAction);

        ResponseEntity<ActionDto> response = actionController.markAsLentOut(bookId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAction, response.getBody());
        verify(actionService).markAsLentOut(bookId);
    }

    @Test
    void markAsLentOut_NotFound() {
        Long bookId = 1L;
        when(actionService.markAsLentOut(bookId)).thenThrow(new EntityNotFoundException("Book not found"));

        ResponseEntity<ActionDto> response = actionController.markAsLentOut(bookId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).markAsLentOut(bookId);
    }

    @Test
    void markAsReceived_Success() {
        String userName = "testUser";
        Long bookId = 1L;
        ActionDto expectedAction = new ActionDto(); // Assume appropriate constructor or setters
        when(actionService.markAsReceived(userName, bookId)).thenReturn(expectedAction);

        ResponseEntity<ActionDto> response = actionController.markAsReceived(userName, bookId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAction, response.getBody());
        verify(actionService).markAsReceived(userName, bookId);
    }

    @Test
    void markAsReceived_NotFound() {
        String userName = "testUser";
        Long bookId = 1L;
        when(actionService.markAsReceived(userName, bookId)).thenThrow(new EntityNotFoundException("Reservation not found"));

        ResponseEntity<ActionDto> response = actionController.markAsReceived(userName, bookId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).markAsReceived(userName, bookId);
    }

    @Test
    void markAsReturned_Success() {
        String userName = "testUser";
        Long bookId = 1L;
        ActionDto expectedAction = new ActionDto(); // Assume appropriate constructor or setters
        when(actionService.markAsReturned(userName, bookId)).thenReturn(expectedAction);

        ResponseEntity<ActionDto> response = actionController.markAsReturned(userName, bookId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAction, response.getBody());
        verify(actionService).markAsReturned(userName, bookId);
    }

    @Test
    void markAsReturned_NotFound() {
        String userName = "testUser";
        Long bookId = 1L;
        when(actionService.markAsReturned(userName, bookId)).thenThrow(new EntityNotFoundException("Book not found or not borrowed by user"));

        ResponseEntity<ActionDto> response = actionController.markAsReturned(userName, bookId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).markAsReturned(userName, bookId);
    }
}