package com.example.booklending.repository;

import com.example.booklending.model.Action;
import com.example.booklending.model.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    List<Action> findByUserIdAndAction(Long userId, ActionType action);

    List<Action> findByBookIdAndAction(Long bookId, ActionType action);
}
