package com.campus.food.controller;

import com.campus.food.model.Student;
import com.campus.food.repository.StudentRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminStudentController {

    private final StudentRepository studentRepository;

    @GetMapping("/admin/students")
    public String listStudents(@RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "id"));
        Page<Student> studentPage;
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.trim();
            studentPage = studentRepository
                    .findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCaseOrClazzContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                            k, k, k, k, pageable);
        } else {
            studentPage = studentRepository.findAll(pageable);
        }
        model.addAttribute("page", studentPage);
        model.addAttribute("keyword", keyword);
        return "admin/students";
    }

    @PostMapping("/admin/students/{id}/balance")
    public String updateBalance(@PathVariable("id") Long id,
                                @RequestParam("balance") BigDecimal balance,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "keyword", required = false) String keyword) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        student.setBalance(balance);
        studentRepository.save(student);
        StringBuilder redirect = new StringBuilder("redirect:/admin/students?page=" + page);
        if (keyword != null && !keyword.isBlank()) {
            redirect.append("&keyword=").append(URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        }
        return redirect.toString();
    }

    @GetMapping("/admin/students/export")
    public void exportStudents(HttpServletResponse response) throws IOException {
        List<Student> students = studentRepository.findAll(Sort.by("id"));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("students");
        int rowIdx = 0;
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("账号名");
        header.createCell(1).setCellValue("姓名");
        header.createCell(2).setCellValue("手机号");

        for (Student s : students) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(s.getUsername());
            row.createCell(1).setCellValue(s.getName());
            row.createCell(2).setCellValue(s.getPhone());
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = URLEncoder.encode("students.xlsx", StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}

