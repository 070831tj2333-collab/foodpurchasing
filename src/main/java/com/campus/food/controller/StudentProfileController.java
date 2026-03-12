package com.campus.food.controller;

import com.campus.food.model.Student;
import com.campus.food.repository.StudentRepository;
import com.campus.food.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentRepository studentRepository;

    @GetMapping("/student/profile")
    public String profile(Authentication authentication, Model model) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            return "redirect:/student/login";
        }
        Student student = studentRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        model.addAttribute("student", student);
        return "student/profile";
    }
}

