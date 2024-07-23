package com.project.scholarship.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth/admin/scholarships")
public class ScholarshipController {

    @GetMapping
    public String viewScholarships(Model model) {
        model.addAttribute("scholarshipSection", true);
        return "admin-dashboard";
    }
}

