package com.example.booklending.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "actions")
@Setter
@Getter
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private ActionType action;  // Enum for actions: RESERVE, CANCEL, RECEIVE, RETURN

    @Column(name = "action_date", nullable = false, updatable = false)
    private LocalDateTime actionDate = LocalDateTime.now();

    @Column(name = "due_date")
    private LocalDateTime dueDate; // Only applicable for RECEIVE action

}
