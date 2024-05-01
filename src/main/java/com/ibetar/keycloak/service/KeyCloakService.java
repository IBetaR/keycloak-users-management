package com.ibetar.keycloak.service;

import com.ibetar.keycloak.entity.UserDTO;
import com.ibetar.keycloak.utils.KeyCloakProvider;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class KeyCloakService implements KeyCloakDao {

    @Override
    public List<UserRepresentation> findAllUsers() {
        return KeyCloakProvider
                .getRealmResource()
                .users()
                .list();
    }

    @Override
    public List<UserRepresentation> findByUsername(String username) {
        return KeyCloakProvider
                .getRealmResource()
                .users()
                .searchByUsername(username, true);
    }

    @Override
    public String createUser(UserDTO userDTO) {
        int status = 0;
        UsersResource usersResource = KeyCloakProvider.getUserResource();

        // Create a new UserRepresentation and populate it with data from the UserDTO
        UserRepresentation user = buildUserRepresentationFromDTO(userDTO);

        // Call Keycloak API to create the user
        try (Response response = usersResource.create(user)) {

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                String path = response.getLocation().getPath();
                String userId = path.substring(path.lastIndexOf("/") + 1);

                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setTemporary(false);
                credential.setType(OAuth2Constants.PASSWORD);
                credential.setValue(userDTO.password());

                usersResource.get(userId).resetPassword(credential);

                RealmResource realmResource = KeyCloakProvider.getRealmResource();
                List<RoleRepresentation> roleRepresentations = null;


                return "User created successfully";
            } else {
                return "Failed to create user: " + response.getStatusInfo().getReasonPhrase();
            }
        }
        // Close the response in a finally block to ensure it gets closed even if an exception is thrown

    }

    private static UserRepresentation buildUserRepresentationFromDTO(UserDTO userDTO) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setEmailVerified(true);
        user.setEnabled(true);

        // If roles are present in the DTO, add them to the user representation
        if (userDTO.roles() != null && !userDTO.roles().isEmpty()) {
            user.setRealmRoles(userDTO.roles());
        }
        return user;
    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public void updateUser(String userId, UserDTO userDTO) {

    }
}
