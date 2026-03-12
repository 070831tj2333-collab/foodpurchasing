package com.campus.food.controller;

import com.campus.food.model.Admin;
import com.campus.food.repository.AdminRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AdminAuthController {

    private static final String REGISTER_SECRET = "qurvew-dohHyx-mydwy7";

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/admin/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("form", new AdminRegisterForm());
        return "admin/register";
    }

    @PostMapping("/admin/register")
    public String handleRegister(@ModelAttribute("form") AdminRegisterForm form,
                                 BindingResult bindingResult,
                                 Model model) {
        if (!REGISTER_SECRET.equals(form.getRegisterPassword())) {
            bindingResult.rejectValue("registerPassword", "invalid", "注册密码不正确");
        }
        if (adminRepository.existsByUsername(form.getUsername())) {
            bindingResult.rejectValue("username", "exists", "该管理员账号已存在");
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "两次输入的密码不一致");
        }

        if (bindingResult.hasErrors()) {
            return "admin/register";
        }

        Admin admin = new Admin();
        admin.setUsername(form.getUsername());
        admin.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        adminRepository.save(admin);

        model.addAttribute("message", "注册成功，请使用账号密码登录后台。");
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/login")
    public String showLoginForm() {
        return "admin/login";
    }

    @Data
    public static class AdminRegisterForm {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        @NotBlank
        private String confirmPassword;
        @NotBlank
        private String registerPassword;
    }
}

