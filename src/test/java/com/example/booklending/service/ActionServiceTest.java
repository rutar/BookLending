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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActionServiceTest {

    @Mock
    private ActionRepository actionRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActionService actionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReserveBook_Success() {
        // Arrange
        String userName = "testUser";
        Long bookId = 1L;
        User user = new User();
        user.setId(1L);
        user.setUsername(userName);
        Role role = new Role();
        role.setId(2);
        user.setRole(role);

        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.AVAILABLE);

        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(actionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ActionDto result = actionService.reserveBook(userName, bookId);

        // Assert
        assertNotNull(result);
        assertEquals(ActionType.RESERVE_BOOK, result.getActionType());
        verify(bookRepository).save(book);
        assertEquals(BookStatus.RESERVED, book.getStatus());
    }

    @Test
    void testReserveBook_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> actionService.reserveBook("nonexistentUser", 1L));
    }

    @Test
    void testReserveBook_BookNotAvailable() {
        // Arrange
        User user = new User();
        user.setRole(new Role(2, "USER"));
        Book book = new Book();
        book.setStatus(BookStatus.BORROWED);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> actionService.reserveBook("testUser", 1L));
    }

    @Test
    void testCancelReservation_Success() {
        // Arrange
        String userName = "testUser";
        Long bookId = 1L;
        User user = new User();
        user.setUsername(userName);
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.RESERVED);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));
        when(actionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ActionDto result = actionService.cancelReservation(userName, bookId);

        // Assert
        assertNotNull(result);
        assertEquals(ActionType.CANCEL_BOOK_RESERVATION, result.getActionType());
        verify(bookRepository).save(book);
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }

    @Test
    void testMarkAsLentOut_Success() {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.RESERVED);

        Action reservationAction = new Action();
        reservationAction.setBook(book);
        reservationAction.setUser(new User());
        reservationAction.setAction(ActionType.RESERVE_BOOK);
        reservationAction.setActionDate(LocalDateTime.now().minusHours(1));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(actionRepository.findByBookIdAndAction(bookId, ActionType.RESERVE_BOOK))
                .thenReturn(java.util.List.of(reservationAction));
        when(actionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ActionDto result = actionService.markAsLentOut(bookId);

        // Assert
        assertNotNull(result);
        assertEquals(ActionType.LENT_OUT_BOOK, result.getActionType());
        verify(bookRepository).save(book);
        assertEquals(BookStatus.LENT_OUT, book.getStatus());
    }

    @Test
    void testMarkAsReceived_Success() {
        // Arrange
        String userName = "testUser";
        Long bookId = 1L;
        User user = new User();
        user.setUsername(userName);
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.LENT_OUT);

        Action lentOutAction = new Action();
        lentOutAction.setBook(book);
        lentOutAction.setUser(user);
        lentOutAction.setAction(ActionType.LENT_OUT_BOOK);
        lentOutAction.setDueDate(LocalDateTime.now().plusWeeks(4));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));
        when(actionRepository.findByBookIdAndAction(bookId, ActionType.LENT_OUT_BOOK))
                .thenReturn(java.util.List.of(lentOutAction));
        when(actionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ActionDto result = actionService.markAsReceived(userName, bookId);

        // Assert
        assertNotNull(result);
        assertEquals(ActionType.RECEIVE_BOOK, result.getActionType());
        verify(bookRepository).save(book);
        assertEquals(BookStatus.BORROWED, book.getStatus());
    }

    @Test
    void testMarkAsReturned_ByUser_Success() {
        // Arrange
        String userName = "testUser";
        Long bookId = 1L;
        User user = new User();
        user.setUsername(userName);
        Role userRole = new Role();
        userRole.setName("USER");
        user.setRole(userRole);
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.BORROWED);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));
        when(actionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ActionDto result = actionService.markAsReturned(userName, bookId);

        // Assert
        assertNotNull(result);
        assertEquals(ActionType.RETURN_BOOK, result.getActionType());
        verify(bookRepository).save(book);
        assertEquals(BookStatus.RETURNED, book.getStatus());
    }

    @Test
    void testMarkAsReturned_ByAdmin_Success() {
        // Arrange
        String adminName = "adminUser";
        Long bookId = 1L;
        User admin = new User();
        admin.setUsername(adminName);
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        admin.setRole(adminRole);
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(BookStatus.RETURNED);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findByUsername(adminName)).thenReturn(Optional.of(admin));
        when(actionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ActionDto result = actionService.markAsReturned(adminName, bookId);

        // Assert
        assertNotNull(result);
        assertEquals(ActionType.RETURN_BOOK, result.getActionType());
        verify(bookRepository).save(book);
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }
}