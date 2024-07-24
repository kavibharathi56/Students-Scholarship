package com.project.scholarship.controller;

import com.project.scholarship.entity.Student;
import com.project.scholarship.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/auth/admin/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "index"; // Thymeleaf view name for the login page
    }

    @PostMapping("/login")
    public String loginStudent(@RequestParam String registrationNumber, @RequestParam String dob, HttpSession session, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(dob, formatter);
        } catch (Exception e) {
            model.addAttribute("studentError", "Invalid date format. Please use yyyy-MM-dd.");
            return "index";
        }

        Optional<Student> studentOpt = studentService.findByRegistrationNumberAndDob(registrationNumber, dateOfBirth);
        if (studentOpt.isEmpty()) {
            model.addAttribute("studentError", "Invalid registration number or date of birth.");
            return "index";
        }

        Student student = studentOpt.get();
        session.setAttribute("student", student);
        return "redirect:/auth/admin/students/dashboard";
    }

    @GetMapping("/dashboard")
    public String showStudentDashboard(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("student");
        if (student == null) {
            return "index";
        }
        model.addAttribute("student", student);
        return "student-dashboard";
    }

    @GetMapping("/add")
    public String showAddStudentForm(Model model) {
        model.addAttribute("studentForm", true);
        model.addAttribute("student", new Student());
        return "admin-dashboard";
    }

    @PostMapping("/add")
    public String addStudent(@ModelAttribute Student student) {
        studentService.save(student);
        return "redirect:/auth/admin/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);
        model.addAttribute("studentEdit", true);
        model.addAttribute("student", student);
        return "admin-dashboard";
    }

    @PostMapping("/edit")
    public String editStudent(@ModelAttribute Student student) {
        studentService.save(student);
        return "redirect:/auth/admin/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteById(id);
        return "redirect:/auth/admin/students";
    }


    @GetMapping
    public String viewStudents(Model model) {
        List<Student> students = studentService.findAll();
        model.addAttribute("studentsList", true);
        model.addAttribute("students", students);
        return "admin-dashboard";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                studentService.save(file);
                return ResponseEntity.status(HttpStatus.OK).body("File uploaded and data saved successfully.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Failed to upload and save data: " + e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload an excel file!");
    }

    public static class ExcelHelper {
        public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        public static boolean hasExcelFormat(MultipartFile file) {
            return TYPE.equals(file.getContentType());
        }
    }
}

