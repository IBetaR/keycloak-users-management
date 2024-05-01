package com.ibetar.keycloak.utils;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.stereotype.Service;

/**
 * Utility class for providing access to Keycloak resources.
 * This class encapsulates the configuration details for connecting to the Keycloak server,
 * allowing other components to easily obtain RealmResource and UsersResource instances.
 */
@Service
public class KeyCloakProvider {
    /**
     * Configuration constants for connecting to the Keycloak server.
     * <p>
     * SERVER_URL: The URL of the Keycloak server, typically "<a href="http://localhost:8080">...</a>".
     * REALM_NAME: The name of the Keycloak realm, e.g. "spring-boot-realm-dev".
     * REALM_MASTER: The name of the master realm in Keycloak, usually "master".
     * ADMIN_CLI: The client ID for the Keycloak admin console, often "admin-cli".
     * USER_CONSOLE: The username of the admin user for the Keycloak admin console.
     * USER_PASSWORD: The password of the admin user for the Keycloak admin console.
     * CLIENT_SECRET: The client secret used for authentication with Keycloak, use your KeyCloak CLI for details.
     */

    private static final String SERVER_URL = "http://localhost:8080";
    private static final String REALM_NAME = "spring-boot-realm-dev";
    private static final String REALM_MASTER = "master";
    private static final String ADMIN_CLI = "admin-cli";
    private static final String USER_CONSOLE = "admin";
    private static final String USER_PASSWORD = "admin";
    private static final String CLIENT_SECRET = "dQtK0TXRlyDKkD0LakkGvB6FGJz0lvd4";

    /**
     * Retrieves the RealmResource associated with the configured Keycloak server.
     *
     * @return The RealmResource instance for the specified realm.
     */
    public static RealmResource getRealmResource() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM_MASTER)
                .clientId(ADMIN_CLI)
                .username(USER_CONSOLE)
                .password(USER_PASSWORD)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();
        return keycloak.realm(REALM_NAME);
    }


    /**
     * Retrieves the UsersResource associated with the configured Keycloak realm.
     *
     * @return The UsersResource instance for the specified realm.
     */
    public static UsersResource getUserResource() {
        RealmResource realm = getRealmResource();
        return realm.users();
    }

}
