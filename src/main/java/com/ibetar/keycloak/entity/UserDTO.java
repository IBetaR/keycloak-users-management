package com.ibetar.keycloak.entity;

import java.util.List;

public record UserDTO(
        String username,
        String email,
        String firstName,
        String lastName,
        String password,
        boolean enabled,
        List<String> roles
) {}