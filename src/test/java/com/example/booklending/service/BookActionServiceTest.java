package com.example.booklending.service;

import com.example.booklending.dto.ActionDto;
import com.example.booklending.model.*;
import com.example.booklending.repository.ActionRepository;
import com.example.booklending.repository.BookRepository;
import com.example.booklending.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ActionServiceTest {

    @Mock
    private ActionRepository ActionRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActionService ActionService;

    private Book book;
    private User user;
    private Action Action;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        book = new Book();
        book.setId(1L);
        book.setStatus(BookStatus.AVAILABLE);

        user = new User();
        user.setId(1L);

        Role role = new Role();
        role.setName("USER");
        user.setRole(role);

        Action = new Action();
        Action.setId(1L);
        Action.setBook(book);
        Action.setUser(user);
        Action.setAction(ActionType.RESERVE);
        Action.setActionDate(LocalDateTime.now());
        Action.setDueDate(LocalDateTime.now().plusHours(24));
    }

    @Test
    void testReserveBookSuccess() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        when(ActionRepository.save(any(Action.class))).thenReturn(Action);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ActionDto result = ActionService.reserveBook(1L, 1L);

        assertNotNull(result);
        assertEquals(ActionType.RESERVE, result.getActionType());
        assertEquals(BookStatus.RESERVED, book.getStatus());
    }

    @Test
    void testReserveBookUserNotFound() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> ActionService.reserveBook(1L, 1L));

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testReserveBookBookNotFound() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> ActionService.reserveBook(1L, 1L));

        assertEquals("Book not found", thrown.getMessage());
    }

    @Test
    void testCancelReservationSuccess() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        when(ActionRepository.findByBookIdAndAction(any(Long.class), eq(ActionType.RESERVE)))
                .thenReturn(Collections.singletonList(Action));
        when(ActionRepository.save(any(Action.class))).thenReturn(Action);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ActionDto result = ActionService.cancelReservation(1L, 1L);

        assertNotNull(result);
        assertEquals(ActionType.CANCEL, result.getActionType());
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }

    @Test
    void testCancelReservationNotFound() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        when(ActionRepository.findByBookIdAndAction(any(Long.class), eq(ActionType.RESERVE)))
                .thenReturn(Collections.emptyList());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> ActionService.cancelReservation(1L, 1L));

        assertEquals("Reservation not found", thrown.getMessage());
    }

    @Test
    void testMarkAsReceivedSuccess() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        when(ActionRepository.findByBookIdAndAction(any(Long.class), eq(ActionType.RESERVE)))
                .thenReturn(Collections.singletonList(Action));
        when(ActionRepository.save(any(Action.class))).thenReturn(Action);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ActionDto result = ActionService.markAsReceived(1L, 1L);

        assertNotNull(result);
        assertEquals(ActionType.RECEIVE, result.getActionType());
        assertEquals(BookStatus.BORROWED, book.getStatus());
    }

    @Test
    void testMarkAsReceivedReservationNotFound() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        when(ActionRepository.findByBookIdAndAction(any(Long.class), eq(ActionType.RESERVE)))
                .thenReturn(Collections.emptyList());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> ActionService.markAsReceived(1L, 1L));

        assertEquals("Reservation not found", thrown.getMessage());
    }

    @Test
    void testMarkAsReturnedSuccess() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        when(ActionRepository.findByBookIdAndAction(any(Long.class), eq(ActionType.RECEIVE)))
                .thenReturn(Collections.singletonList(Action));
        when(ActionRepository.save(any(Action.class))).thenReturn(Action);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ActionDto result = ActionService.markAsReturned(1L, 1L);

        assertNotNull(result);
        assertEquals(ActionType.RETURN, result.getActionType());
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }

    @Test
    void testMarkAsReturnedNotFound() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        when(ActionRepository.findByBookIdAndAction(any(Long.class), eq(ActionType.RECEIVE)))
                .thenReturn(Collections.emptyList());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> ActionService.markAsReturned(1L, 1L));

        assertEquals("Book not currently borrowed by user", thrown.getMessage());
    }
}
