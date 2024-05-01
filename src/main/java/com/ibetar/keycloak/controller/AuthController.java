package com.ibetar.keycloak.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
//    @GetMapping
//    @PreAuthorize("hasRole('customer-profile')")
//    public String hello() {
//        return "Hello from Authentication Using Key Cloak";
//    }

//    @GetMapping("admin")
//    @PreAuthorize("hasRole('customer-admin')")
//    public String asAdmin() {
//        return "Hello as Admin from Authentication Using Key Cloak";
//    }

    @GetMapping("admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String asPlatformAdmin() {
        return "Hello as Platform Admin from Authentication Using Key Cloak";
    }

    @GetMapping("profile")
    @PreAuthorize("hasAnyRole" +
            "('PERSON', 'ENTERPRISE', 'NON_PROFIT', 'GOVERNMENT', 'SOCIAL_MOVEMENT', 'RELIGIOUS_ENTITY')" +
            " or hasRole('ADMIN')")
    public String asProfile() {
        return "Hello as Profile from Authentication Using Key Cloak";
    }

}
