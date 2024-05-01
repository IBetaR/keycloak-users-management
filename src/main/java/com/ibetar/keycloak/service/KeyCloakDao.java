package com.ibetar.keycloak.service;

import com.ibetar.keycloak.entity.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface KeyCloakDao {
    List<UserRepresentation> findAllUsers();
    List<UserRepresentation> findByUsername(String username);
    String createUser(UserDTO userDTO);
    void deleteUser(String userId);
    void updateUser(String userId, UserDTO userDTO);
}
