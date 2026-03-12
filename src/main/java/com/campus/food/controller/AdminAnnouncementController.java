package com.campus.food.controller;

import com.campus.food.model.Announcement;
import com.campus.food.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AnnouncementRepository announcementRepository;

    @GetMapping("/admin/announcements")
    public String editPage(Model model) {
        Announcement latest = announcementRepository.findFirstByOrderByCreatedAtDesc();
        if (latest == null) {
            latest = new Announcement();
        }
        model.addAttribute("announcement", latest);
        return "admin/announcements";
    }

    @PostMapping("/admin/announcements")
    public String saveAnnouncement(@RequestParam(value = "title", required = false) String title,
                                   @RequestParam("content") String content) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcementRepository.save(announcement);
        return "redirect:/admin/announcements?success";
    }
}

