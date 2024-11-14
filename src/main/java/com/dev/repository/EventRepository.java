package com.dev.repository;

import com.dev.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByEndTimeBeforeAndActiveTrue(LocalDateTime dateTime);
}
