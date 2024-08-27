package com.example.booklending.repository;

import com.example.booklending.AbstractIntegrationTest;
import com.example.booklending.model.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Tag("integration")
@Transactional
public class ActionRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ActionRepository ActionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByUserIdAndAction() {
        // Set up test data

        User user = new User();
        user.setEmail("user@2example.com");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(roleRepository.save(new Role("USER")));
        User savedUser = userRepository.save(user);

        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setStatus(BookStatus.AVAILABLE);
        book.setAuthor("Author of book");
        Book savedbook = bookRepository.save(book);

        Action action = new Action();
        action.setUser(savedUser);
        action.setBook(savedbook);
        action.setAction(ActionType.RESERVE);
        action.setActionDate(LocalDateTime.now());
        ActionRepository.save(action);

        // Test the repository method
        List<Action> actions = ActionRepository.findByUserIdAndAction(user.getId(), ActionType.RESERVE);

        assertEquals(1, actions.size());
        assertTrue(actions.stream().anyMatch(act -> act.getUser().equals(user)
                && act.getAction() == ActionType.RESERVE));
    }

    @Test
    void testFindByBookIdAndAction() {

        // Set up test data
        User user = new User();
        user.setEmail("user2@example.com");
        user.setUsername("testuser2");
        user.setPassword("password");
        user.setRole(roleRepository.save(new Role("ADMIN")));
        User savedUser = userRepository.save(user);


        Book book = new Book();
        book.setTitle("Another Test Book");
        book.setIsbn("0987654321");
        book.setAuthor("Author of book");
        book.setStatus(BookStatus.AVAILABLE);
        Book savedbook = bookRepository.save(book);

        Action act = new Action();
        act.setUser(savedUser);
        act.setBook(savedbook);
        act.setAction(ActionType.RETURN);
        act.setActionDate(LocalDateTime.now());
        ActionRepository.save(act);

        // Test the repository method
        List<Action> actions = ActionRepository.findByBookIdAndAction(book.getId(), ActionType.RETURN);

        assertEquals(1, actions.size());
        assertTrue(actions.stream().anyMatch(action -> action.getBook().equals(book)
                && action.getAction() == ActionType.RETURN));
    }
}
