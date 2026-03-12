package com.campus.food.repository;

import com.campus.food.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Announcement findFirstByOrderByCreatedAtDesc();
}

