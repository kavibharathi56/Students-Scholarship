package com.project.scholarship.controller;

import com.project.scholarship.entity.admin;
import com.project.scholarship.service.adminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth/admin")
public class authController {

    @Autowired
    private adminService adminService;

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
        model.addAttribute("homeSection", true);
        return "admin-dashboard";
    }

    @GetMapping("/logout")
    public String logoutAdmin(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
