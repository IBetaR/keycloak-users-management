package com.ibetar.keycloak.controller;

import com.ibetar.keycloak.entity.UserDTO;
import com.ibetar.keycloak.service.KeyCloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("api/v1/keycloak/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class KeyCloakController {
    private final KeyCloakService service;
    @GetMapping
    public ResponseEntity<?> findAllUsers() {
        return ResponseEntity.ok(service.findAllUsers());
    }

    @GetMapping("user/{username}")
    public ResponseEntity<?> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(service.findByUsername(username));
    }

    @PostMapping("create")
    public ResponseEntity<?> createUser(
            @RequestBody UserDTO userDTO)
            throws URISyntaxException
    {
        String response = service.createUser(userDTO);
        return ResponseEntity.created(new URI("keycloak/users/create")).body(response);
    }

    @PutMapping("update/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @RequestBody UserDTO userDTO
    )
    {
        service.updateUser(userId, userDTO);
        String response = String.format(
                "User %s %s, with username: %s updated successfully!",
                userDTO.firstName(),
                userDTO.lastName(),
                userDTO.username()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        service.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}