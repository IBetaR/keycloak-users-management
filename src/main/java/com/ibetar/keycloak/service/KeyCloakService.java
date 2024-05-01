package com.ibetar.keycloak.service;

import com.ibetar.keycloak.entity.UserDTO;
import com.ibetar.keycloak.utils.KeyCloakProvider;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service class for interacting with Keycloak resources.
 * Implements the KeyCloakDao interface.
 */
@Service
@Slf4j
public class KeyCloakService implements KeyCloakDao {
    /**
     * Retrieves all users from the Keycloak realm.
     *
     * @return List of UserRepresentation objects representing all users in the realm.
     */
    @Override
    public List<UserRepresentation> findAllUsers() {
        return KeyCloakProvider
                .getRealmResource()
                .users()
                .list();
    }

    /**
     * Retrieves users by username from the Keycloak realm.
     *
     * @param username The username to search for.
     * @return List of UserRepresentation objects matching the username.
     */
    @Override
    public List<UserRepresentation> findByUsername(String username) {
        return KeyCloakProvider
                .getRealmResource()
                .users()
                .searchByUsername(username, true);
    }

    /**
     * Creates a new user in the Keycloak realm based on the provided UserDTO.
     *
     * @param userDTO The UserDTO containing user information.
     * @return A message indicating the result of the user creation process.
     */
    @Override
    public String createUser(UserDTO userDTO) {
        log.info("Creating user {} {}", userDTO.firstName(), userDTO.lastName());
        int status = 0;
        UsersResource usersResource = KeyCloakProvider.getUserResource();

        // 1. Create a new UserRepresentation and populate it with data from the UserDTO
        UserRepresentation user = buildUserRepresentationFromDTO(userDTO);

        // 2. Create/Update the user
        try (Response response = usersResource.create(user)) {
            log.info("Creating Response from usersResource");

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                log.info("Response as Status CREATED");
                String path = response.getLocation().getPath();
                String userId = path.substring(path.lastIndexOf("/") + 1);

                CredentialRepresentation credential = new CredentialRepresentation();
                log.info("Creating Credential Representation...");
                credential.setTemporary(false);
                credential.setType(OAuth2Constants.PASSWORD);
                credential.setValue(userDTO.password());

                usersResource.get(userId).resetPassword(credential);

                RealmResource realmResource = KeyCloakProvider.getRealmResource();
                log.info("Retrieving RealmResource associated to user from KeyCloakProvider...");

                List<RoleRepresentation> roleRepresentations;

                if (userDTO.roles() == null || userDTO.roles().isEmpty()) {
                    log.info("Role Representations not found for user");
                    roleRepresentations = List.of(realmResource.roles().get("user").toRepresentation());

                } else {
                    log.info("Role Representations found for user");
                    roleRepresentations = realmResource.roles()
                            .list()
                            .stream()
                            .filter(role -> userDTO.roles()
                                    .stream()
                                    .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                            .toList();
                }
                log.info("Setting/Add roleRepresentations to realmResource Representations for user");
                realmResource.users()
                        .get(userId)
                        .roles()
                        .realmLevel()
                        .add(roleRepresentations);

                log.info("User created successfully");
                return "User created successfully";
            } else if (status == Response.Status.CONFLICT.getStatusCode()) {
                log.error("User already exists. Please contact the administrator.");
                return "User already exists. Please contact the administrator.";
            } else {
                log.error("Failed to create user: {}", response.getStatusInfo().getReasonPhrase());
                return "Failed to create user: " + response.getStatusInfo().getReasonPhrase();
            }
        } catch (Exception e) {
            log.error("Error occurred while creating user: {}", e.getMessage());
            return "Error occurred while creating user. Please contact the administrator";
        }
    }

    /**
     * Deletes a user from the Keycloak realm based on the provided user ID.
     *
     * @param userId The ID of the user to delete.
     */
    @Override
    public void deleteUser(String userId) {
        KeyCloakProvider.getUserResource().get(userId).remove();
    }

    /**
     * Updates a user in the Keycloak realm based on the provided user ID and UserDTO.
     *
     * @param userId  The ID of the user to update.
     * @param userDTO The UserDTO containing updated user information.
     */
    @Override
    public void updateUser(String userId, UserDTO userDTO) {
        CredentialRepresentation credentialUpdated = new CredentialRepresentation();
        log.info("Updating credentials for user {}...", userDTO.username());
        credentialUpdated.setTemporary(false);
        credentialUpdated.setType(OAuth2Constants.PASSWORD);
        credentialUpdated.setValue(userDTO.password());

        UserRepresentation updatedUser = new UserRepresentation();
        updatedUser.setUsername(userDTO.username());
        updatedUser.setEmail(userDTO.email());
        updatedUser.setFirstName(userDTO.firstName());
        updatedUser.setLastName(userDTO.lastName());
        updatedUser.setEmailVerified(true);
        updatedUser.setEnabled(true);
        updatedUser.setCredentials(Collections.singletonList(credentialUpdated));
        updatedUser.setRealmRoles(userDTO.roles());

        UserResource userResource = KeyCloakProvider.getUserResource().get(userId);

        userResource.update(updatedUser);
        log.info("UserResource {} updated successfully!", userDTO.username());
    }

    /**
     * Builds a UserRepresentation object from the provided UserDTO.
     *
     * @param userDTO The UserDTO containing user information.
     * @return A UserRepresentation object representing the user.
     */
    protected UserRepresentation buildUserRepresentationFromDTO(UserDTO userDTO) {
        log.info("Building UserRepresentationFromDTO {} {}", userDTO.firstName(), userDTO.lastName());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setEmailVerified(true);
        user.setEnabled(true);

        if (userDTO.roles() != null && !userDTO.roles().isEmpty()) {
            user.setRealmRoles(userDTO.roles());
        }
        log.info("Build completed successfully");
        return user;
    }
}