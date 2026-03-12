package com.campus.food.controller;

import com.campus.food.model.Student;
import com.campus.food.repository.StudentRepository;
import com.campus.food.security.CurrentUser;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.Random;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@RequiredArgsConstructor
public class StudentAuthController {

    private final StudentRepository studentRepository;
    private final Random random = new Random();

    @GetMapping("/student/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("form", new StudentRegisterForm());
        return "student/register";
    }

    @PostMapping("/student/register")
    public String handleRegister(@ModelAttribute("form") StudentRegisterForm form,
                                 BindingResult bindingResult,
                                 Model model) {
        if (form.getPhone() == null || form.getPhone().length() < 4) {
            bindingResult.rejectValue("phone", "invalid", "手机号至少为 4 位");
        }

        if (bindingResult.hasErrors()) {
            return "student/register";
        }

        Student student = new Student();
        student.setName(form.getName());
        student.setClazz(form.getClazz());
        student.setPhone(form.getPhone());
        student.setUsername(generateUniqueUsername());
        studentRepository.save(student);

        model.addAttribute("username", student.getUsername());
        model.addAttribute("phoneSuffix", student.getPhone().substring(student.getPhone().length() - 4));
        return "student/register-success";
    }

    @GetMapping("/student/login")
    public String showLoginForm(Model model) {
        model.addAttribute("form", new StudentLoginForm());
        return "student/login";
    }

    @PostMapping("/student/login")
    public String handleLogin(@ModelAttribute("form") StudentLoginForm form,
                              BindingResult bindingResult,
                              HttpServletRequest request) {
        Optional<Student> optionalStudent = studentRepository.findByUsername(form.getUsername());
        if (optionalStudent.isEmpty()) {
            bindingResult.rejectValue("username", "notfound", "账号不存在");
        } else {
            Student student = optionalStudent.get();
            String phone = student.getPhone();
            String suffix = phone.substring(Math.max(0, phone.length() - 4));
            if (!suffix.equals(form.getPhoneSuffix())) {
                bindingResult.rejectValue("phoneSuffix", "invalid", "手机号后四位不正确");
            }
        }

        if (bindingResult.hasErrors()) {
            return "student/login";
        }

        Student student = optionalStudent.get();
        CurrentUser principal = new CurrentUser(student.getId(), student.getUsername(), "", "ROLE_STUDENT");
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        HttpSession session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);

        return "redirect:/student/profile";
    }

    private String generateUniqueUsername() {
        while (true) {
            String candidate = randomLowercase(4);
            if (studentRepository.findByUsername(candidate).isEmpty()) {
                return candidate;
            }
        }
    }

    private String randomLowercase(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = (char) ('a' + random.nextInt(26));
            sb.append(c);
        }
        return sb.toString();
    }

    @Data
    public static class StudentRegisterForm {
        @NotBlank
        private String name;
        @NotBlank
        private String clazz;
        @NotBlank
        private String phone;
    }

    @Data
    public static class StudentLoginForm {
        @NotBlank
        private String username; // 四位小写字母账户名
        @NotBlank
        private String phoneSuffix; // 手机号后四位
    }
}

