package com.project.scholarship.controller;

import com.project.scholarship.entity.admin;
import com.project.scholarship.service.adminService;
import com.project.scholarship.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth/admin")
public class adminController {
    private final StudentService studentService;

    @Autowired
    private adminService adminService;



    @Autowired
    public adminController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerAdmin(@RequestBody admin admin) {
        if (adminService.findByUsername(admin.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }
        adminService.save(admin);
        return ResponseEntity.ok("Admin registered successfully.");
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "index";
    }

    @PostMapping("/login")
    public String loginAdmin(@ModelAttribute admin admin, HttpSession session, Model model) {
        try {
            admin existingAdmin = adminService.findByUsername(admin.getUsername());

            // Check if admin exists and password matches
            if (existingAdmin == null || !adminService.getPasswordEncoder().matches(admin.getPassword(), existingAdmin.getPassword())) {
                model.addAttribute("error", "Invalid username or password.");
                return "index"; // Return to the login page with error message
            }

            // Set admin in session
            session.setAttribute("admin", existingAdmin);

            // Redirect to dashboard
            return "redirect:/auth/admin/dashboard";

        } catch (Exception e) {

            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            return "index"; // Return to the login page with error message
        }
    }




    @GetMapping("/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/";
        }

        List<String> scholarshipNames = studentService.findDistinctScholarshipNames();
        List<String> departmentNames = studentService.findDistinctDepartments();

        model.addAttribute("homeSection", true);
        model.addAttribute("totalStudents", studentService.countTotalStudents());
        model.addAttribute("totalDepartments", studentService.countTotalDepartments());
        model.addAttribute("studentsAvailedScholarship", studentService.countStudentsAvailedScholarship());
        model.addAttribute("studentsAvailedByScheme", studentService.getDistinctScholarshipNames());
        model.addAttribute("studentsAvailedByDepartment", studentService.getDistinctDepartments()); // Initial value, will be updated via JS
        model.addAttribute("scholarshipNames", scholarshipNames);
        model.addAttribute("departmentNames", departmentNames);
        return "admin-dashboard";
    }
    @GetMapping("/dashboard/schemeCount")
    public ResponseEntity<Map<String, Long>> getSchemeCount(@RequestParam String scheme) {
        try {
            long count = studentService.countByScheme(scheme);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();  // Log the error for debugging
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/dashboard/departmentCount")
    public ResponseEntity<Map<String, Long>> getDepartmentCount(@RequestParam String department) {
        try {
            long count = studentService.countByDepartment(department);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();  // Log the error for debugging
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/logout")
    public String logoutAdmin(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
