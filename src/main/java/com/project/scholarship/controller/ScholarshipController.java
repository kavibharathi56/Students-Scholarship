package com.project.scholarship.controller;

import com.project.scholarship.entity.Student;
import com.project.scholarship.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/auth/admin")
public class  ScholarshipController {

    private final StudentService studentService;

    @Autowired
    public ScholarshipController(StudentService scholarshipService) {
        this.studentService = scholarshipService;
    }

    @GetMapping("/scholarships")
    public String viewScholarships(Model model) {
        model.addAttribute("scholarshipSection", true);
        return "admin-dashboard"; // Ensure this points to your scholarship.html template
    }

    @GetMapping("/scholarships/students")
    @ResponseBody
    public List<Student> getStudentsByScholarshipAndDepartment(
            @RequestParam(value = "scholarship", required = false) String scholarship,
            @RequestParam(value = "department", required = false) String department) {

        if ((scholarship == null || scholarship.isEmpty()) && (department == null || department.isEmpty())) {
            return studentService.findAll();
        } else if (scholarship == null || scholarship.isEmpty()) {
            return studentService.findByDepartment(department);
        } else if (department == null || department.isEmpty()) {
            return studentService.findByScholarshipName(scholarship);
        } else {
            return studentService.findByScholarshipNameAndDepartment(scholarship, department);
        }
    }
}
