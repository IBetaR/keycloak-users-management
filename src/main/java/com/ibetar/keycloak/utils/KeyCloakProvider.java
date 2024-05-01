package com.ibetar.keycloak.utils;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.stereotype.Service;

@Service
public class KeyCloakProvider {
    private static final String SERVER_URL = "http://localhost:8080";
    private static final String REALM_NAME = "auth-service-api-gateway";
    private static final String REALM_MASTER = "master";
    private static final String ADMIN_CLI = "admin-cli";
    private static final String USER_CONSOLE = "admin";
    private static final String USER_PASSWORD = "admin";
    private static final String CLIENT_SECRET = "dQtK0TXRlyDKkD0LakkGvB6FGJz0lvd4";

    public static RealmResource getRealmResource() {
        try (Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM_MASTER)
                .clientId(ADMIN_CLI)
                .username(USER_CONSOLE)
                .password(USER_PASSWORD)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build()) {
            return keycloak.realm(REALM_NAME);
        }
    }

    public static UsersResource getUserResource() {
        RealmResource realm = getRealmResource();
        return realm.users();
    }


}
