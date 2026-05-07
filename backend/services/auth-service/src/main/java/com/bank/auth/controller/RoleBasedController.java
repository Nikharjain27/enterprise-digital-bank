package com.bank.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleBasedController {

    @GetMapping("/api/v1/customer/dashboard")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public String customerDashboard() {

        return "Welcome Customer";
    }

    @GetMapping("/api/v1/admin/dashboard")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminDashboard() {

        return "Welcome Admin";
    }

    @GetMapping("/api/v1/auditor/dashboard")
    @PreAuthorize("hasAuthority('AUDITOR')")
    public String auditorDashboard() {

        return "Welcome Auditor";
    }

    @GetMapping("/api/v1/manager/dashboard")
    @PreAuthorize("hasAuthority('BANK_MANAGER')")
    public String managerDashboard() {

        return "Welcome Bank Manager";
    }
}