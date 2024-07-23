package com.project.scholarship.service;

import com.project.scholarship.entity.admin;
import com.project.scholarship.repository.adminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class adminService {

    private final adminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public adminService(adminRepository adminRepository , PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public void save(admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);

    }
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

}
