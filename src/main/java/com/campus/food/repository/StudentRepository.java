package com.campus.food.repository;

import com.campus.food.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUsername(String username);

    Optional<Student> findByPhone(String phone);

    Page<Student> findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCaseOrClazzContainingIgnoreCaseOrPhoneContainingIgnoreCase(
            String username, String name, String clazz, String phone, Pageable pageable);
}

