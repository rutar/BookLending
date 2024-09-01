package com.example.booklending.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("unit")
class ActionTest {

    @Test
    public void testActionCreation() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");
        // Ideally, you'd set other required fields for Book if any

        User user = new User();
        user.setUsername("Test User");
        // Ideally, you'd set other required fields for User if any

        Action action = new Action();
        action.setBook(book);
        action.setUser(user);
        action.setAction(ActionType.RECEIVE_BOOK);
        action.setActionDate(LocalDateTime.of(2024, 9, 1, 12, 0));  // Set specific date-time
        action.setDueDate(LocalDateTime.of(2024, 9, 15, 12, 0));    // Set specific due date

        // Act & Assert
        assertNotNull(action.getBook());
        assertNotNull(action.getUser());
        assertEquals(book, action.getBook());
        assertEquals(user, action.getUser());
        assertEquals(ActionType.RECEIVE_BOOK, action.getAction());
        assertEquals(LocalDateTime.of(2024, 9, 1, 12, 0), action.getActionDate());
        assertEquals(LocalDateTime.of(2024, 9, 15, 12, 0), action.getDueDate());
    }

    @Test
    public void testActionDateDefaultValue() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");
        // Ideally, you'd set other required fields for Book if any

        User user = new User();
        user.setUsername("Test User");
        // Ideally, you'd set other required fields for User if any

        Action action = new Action();
        action.setBook(book);
        action.setUser(user);
        action.setAction(ActionType.RESERVE_BOOK);

        // Act
        LocalDateTime now = LocalDateTime.now();
        action.setActionDate(now);
        // Test that actionDate was set correctly

        // Assert
        assertNotNull(action.getActionDate());
        assertEquals(now.toLocalDate(), action.getActionDate().toLocalDate());
        assertEquals(now.toLocalTime().getHour(), action.getActionDate().toLocalTime().getHour());
    }
}
