package com.campus.food.controller;

import com.campus.food.model.Announcement;
import com.campus.food.model.Food;
import com.campus.food.model.FoodLike;
import com.campus.food.model.Student;
import com.campus.food.repository.AnnouncementRepository;
import com.campus.food.repository.FoodLikeRepository;
import com.campus.food.repository.FoodRepository;
import com.campus.food.repository.StudentRepository;
import com.campus.food.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class FoodController {

    private final FoodRepository foodRepository;
    private final FoodLikeRepository foodLikeRepository;
    private final StudentRepository studentRepository;
    private final AnnouncementRepository announcementRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @GetMapping({"/", "/foods"})
    public String listFoods(Model model, Authentication authentication) {
        List<Food> foods = foodRepository.findAll();
        Announcement latest = announcementRepository.findFirstByOrderByCreatedAtDesc();
        Set<Long> likedIds = new HashSet<>();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser currentUser
                && "ROLE_STUDENT".equals(currentUser.getRole())) {
            Optional<Student> studentOpt = studentRepository.findById(currentUser.getId());
            if (studentOpt.isPresent()) {
                List<FoodLike> likes = foodLikeRepository.findByStudent(studentOpt.get());
                for (FoodLike like : likes) {
                    likedIds.add(like.getFood().getId());
                }
            }
        }
        model.addAttribute("foods", foods);
        model.addAttribute("announcement", latest);
        model.addAttribute("likedIds", likedIds);
        return "foods/list";
    }

    @PostMapping("/foods/{id}/like")
    public String likeFood(@PathVariable("id") Long id,
                           Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            return "redirect:/student/login";
        }
        if (!"ROLE_STUDENT".equals(currentUser.getRole())) {
            return "redirect:/";
        }

        Student student = studentRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food not found"));

        Optional<FoodLike> existing = foodLikeRepository.findByStudentAndFood(student, food);
        if (existing.isEmpty()) {
            FoodLike like = new FoodLike();
            like.setStudent(student);
            like.setFood(food);
            foodLikeRepository.save(like);
            food.setLikeCount(food.getLikeCount() + 1);
            foodRepository.save(food);
        }

        return "redirect:/foods";
    }

    @GetMapping("/admin/foods")
    public String adminFoods(Model model) {
        List<Food> foods = foodRepository.findAll();
        model.addAttribute("foods", foods);
        return "admin/foods";
    }

    @GetMapping("/admin/foods/new")
    public String newFoodForm() {
        return "admin/foods-new";
    }

    @PostMapping("/admin/foods")
    public String createFood(@RequestParam("name") String name,
                             @RequestParam("ingredients") String ingredients,
                             @RequestParam("image") MultipartFile image) throws IOException {
        if (image.isEmpty() || !StringUtils.hasText(name) || !StringUtils.hasText(ingredients)) {
            return "redirect:/admin/foods/new?error";
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = image.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + ext;
        Path target = uploadPath.resolve(filename);
        image.transferTo(target.toFile());

        Food food = new Food();
        food.setName(name);
        food.setIngredients(ingredients);
        food.setImagePath("/uploads/foods/" + filename);
        food.setCreatedAt(LocalDateTime.now());
        food.setUpdatedAt(LocalDateTime.now());
        foodRepository.save(food);

        return "redirect:/admin/foods";
    }
}

