package com.example.booklending.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "actions")
@Setter
@Getter
@ApiModel(description = "Represents an action performed on a book, such as reserve, cancel, receive, or return.")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The unique ID of the action", example = "1", required = true, position = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    @ApiModelProperty(notes = "The book associated with the action", required = true, position = 2)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(notes = "The user who performed the action", required = true, position = 3)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    @ApiModelProperty(notes = "The type of action performed (e.g., RESERVE, CANCEL, RECEIVE, RETURN)", required = true, position = 4)
    private ActionType action;

    @Column(name = "action_date", nullable = false, updatable = false)
    @ApiModelProperty(notes = "The date and time when the action was performed", example = "2024-09-01T12:00:00", required = true, position = 5)
    private LocalDateTime actionDate = LocalDateTime.now();

    @Column(name = "due_date")
    @ApiModelProperty(notes = "The due date for the action, applicable only for RECEIVE actions", example = "2024-09-15T12:00:00", position = 6)
    private LocalDateTime dueDate;
}
