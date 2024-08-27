package com.example.booklending.service;

import com.example.booklending.dto.ActionDto;
import com.example.booklending.model.*;
import com.example.booklending.repository.ActionRepository;
import com.example.booklending.repository.BookRepository;
import com.example.booklending.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ActionService {

    private final ActionRepository actionRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ActionService(ActionRepository actionRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.actionRepository = actionRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ActionDto reserveBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        if (user.getRole().getId() != 2) {
            throw new RuntimeException("User is not a borrower");
        }
        if (!book.getStatus().equals(BookStatus.AVAILABLE)) {
            throw new RuntimeException("Book is not available for reservation");
        }

        Action action = new Action();
        action.setBook(book);
        action.setUser(user);
        action.setAction(ActionType.RESERVE);
        action.setActionDate(LocalDateTime.now());
        action.setDueDate(LocalDateTime.now().plusHours(24)); // reservation up to 24 hours

        book.setStatus(BookStatus.RESERVED);

        Action savedAction = actionRepository.save(action);
        bookRepository.save(book);

        return convertToDto(savedAction);
    }

    @Transactional
    public ActionDto cancelReservation(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        Action reservation = actionRepository.findByBookIdAndAction(bookId, ActionType.RESERVE)
                .stream()
                .filter(action -> action.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setAction(ActionType.CANCEL);

        book.setStatus(BookStatus.AVAILABLE);

        Action updatedAction = actionRepository.save(reservation);
        bookRepository.save(book);

        return convertToDto(updatedAction);
    }

    @Transactional
    public ActionDto markAsReceived(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Action reservation = actionRepository.findByBookIdAndAction(bookId, ActionType.RESERVE)
                .stream()
                .filter(action -> action.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        Action receivedAction = new Action();
        receivedAction.setBook(book);
        receivedAction.setUser(user);
        receivedAction.setAction(ActionType.RECEIVE);
        receivedAction.setActionDate(LocalDateTime.now());
        receivedAction.setDueDate(LocalDateTime.now().plusWeeks(4)); // 4-week borrowing period

        book.setStatus(BookStatus.BORROWED);

        Action savedAction = actionRepository.save(receivedAction);
        bookRepository.save(book);

        return convertToDto(savedAction);
    }

    @Transactional
    public ActionDto markAsReturned(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Action receivedAction = actionRepository.findByBookIdAndAction(bookId, ActionType.RECEIVE)
                .stream()
                .filter(action -> action.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not currently borrowed by user"));

        Action returnAction = new Action();
        returnAction.setBook(book);
        returnAction.setUser(user);
        returnAction.setAction(ActionType.RETURN);
        returnAction.setActionDate(LocalDateTime.now());

        book.setStatus(BookStatus.AVAILABLE);

        Action savedAction = actionRepository.save(returnAction);
        bookRepository.save(book);

        return convertToDto(savedAction);
    }

    private ActionDto convertToDto(Action action) {
        return new ActionDto(
                action.getId(),
                action.getBook().getId(),
                action.getUser().getId(),
                action.getAction(),
                action.getActionDate(),
                action.getDueDate()
        );
    }

    public Action convertToEntity(ActionDto actionDto) {
        Action action = new Action();
        action.setId(actionDto.getId());
        action.setBook(bookRepository.findById(actionDto.getBookId()).orElseThrow());
        action.setUser(userRepository.findById(actionDto.getUserId()).orElseThrow());
        action.setAction(actionDto.getActionType());
        action.setActionDate(actionDto.getActionDate());
        action.setDueDate(actionDto.getDueDate());
        return action;
    }
}
