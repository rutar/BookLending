package com.example.booklending.service;

import com.example.booklending.dto.ActionDto;
import com.example.booklending.model.*;
import com.example.booklending.repository.ActionRepository;
import com.example.booklending.repository.BookRepository;
import com.example.booklending.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

import static com.example.booklending.configuration.Constants.ADMIN_ROLE;
import static com.example.booklending.configuration.Constants.USER_ROLE;

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
    public ActionDto reserveBook(String userName, Long bookId) {
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found"));
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
        action.setAction(ActionType.RESERVE_BOOK);
        action.setActionDate(LocalDateTime.now());
        action.setDueDate(LocalDateTime.now().plusHours(24)); // reservation up to 24 hours

        book.setStatus(BookStatus.RESERVED);

        Action savedAction = actionRepository.save(action);
        bookRepository.save(book);

        return convertToDto(savedAction);
    }

    @Transactional
    public ActionDto cancelReservation(String userName, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found"));

        Action reservation = actionRepository.findByBookIdAndAction(bookId, ActionType.RESERVE_BOOK)
                .stream()
                .filter(action -> action.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setAction(ActionType.CANCEL_BOOK_RESERVATION);

        book.setStatus(BookStatus.AVAILABLE);

        Action updatedAction = actionRepository.save(reservation);
        bookRepository.save(book);

        return convertToDto(updatedAction);
    }

    @Transactional
    public ActionDto markAsReceived(String userName, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new EntityNotFoundException("User not found"));

        Action lentOutAction = actionRepository.findByBookIdAndAction(bookId, ActionType.LENT_OUT_BOOK)
                .stream()
                .filter(action -> action.getUser().getUsername().equals(userName))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Book is not lent out!"));

        if (!book.getStatus().equals(BookStatus.LENT_OUT)) {
            throw new EntityNotFoundException("Book is not lent out!");
        }


        Action receivedAction = new Action();
        receivedAction.setBook(book);
        receivedAction.setUser(user);
        receivedAction.setAction(ActionType.RECEIVE_BOOK);
        receivedAction.setActionDate(LocalDateTime.now());
        receivedAction.setDueDate(lentOutAction.getDueDate()); // 4-week borrowing period TODO: change!

        book.setStatus(BookStatus.BORROWED);

        Action savedAction = actionRepository.save(receivedAction);
        bookRepository.save(book);

        return convertToDto(savedAction);
    }

    @Transactional
    public ActionDto markAsReturned(String userName, Long bookId) {

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Action savedAction = null;

        if (user.getRole().getName().equals(USER_ROLE)) {

            if (!book.getStatus().equals(BookStatus.BORROWED)) {
                new EntityNotFoundException("Book currently is not borrowed by user!");
            }

            Action returnAction = new Action();
            returnAction.setBook(book);
            returnAction.setUser(user);
            returnAction.setAction(ActionType.RETURN_BOOK);
            returnAction.setActionDate(LocalDateTime.now());

            book.setStatus(BookStatus.RETURNED);

            savedAction = actionRepository.save(returnAction);
            bookRepository.save(book);
        }

        if (user.getRole().getName().equals(ADMIN_ROLE)) {

            if (!book.getStatus().equals(BookStatus.RETURNED)) {
                new EntityNotFoundException("Book currently is not returned by user!");
            }

            Action returnAction = new Action();
            returnAction.setBook(book);
            returnAction.setUser(user);
            returnAction.setAction(ActionType.RETURN_BOOK);
            returnAction.setActionDate(LocalDateTime.now());
            book.setStatus(BookStatus.AVAILABLE);

            savedAction = actionRepository.save(returnAction);
            bookRepository.save(book);
        }

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

    @Transactional
    public ActionDto markAsLentOut(String userName, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Action lentOutAction = new Action();

        // find last reservation
        Optional<Action> reservation = actionRepository.findByBookIdAndAction(bookId, ActionType.RESERVE_BOOK)
                .stream()
                .max(Comparator.comparing(Action::getActionDate));

        if (!reservation.isPresent()) {
            throw new EntityNotFoundException("Reservation not found");
        }

        book.setStatus(BookStatus.LENT_OUT);
        bookRepository.save(book);

        lentOutAction.setBook(book);
        lentOutAction.setUser(reservation.get().getUser());
        lentOutAction.setAction(ActionType.LENT_OUT_BOOK);
        lentOutAction.setActionDate(LocalDateTime.now());
        lentOutAction.setDueDate(LocalDateTime.now().plusWeeks(4)); // 4-week borrowing period

        Action savedAction = actionRepository.save(lentOutAction);

        return convertToDto(savedAction);
    }

}
