package com.ibetar.keycloak.jwt;

import com.ibetar.keycloak.config.KeycloakLogoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthConverter.class);
    private final JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
    private final MappingJwtGrantedAuthoritiesConverter mapping;
    @Value("${jwt.auth.converter.principal-attr}")
    private String keyCloakPrincipalAttr;
    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    public JwtAuthConverter(MappingJwtGrantedAuthoritiesConverter mapping) {
        this.mapping = mapping;
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwtSource) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                converter.convert(jwtSource).stream(),
                extractResourceRoles(jwtSource).stream())
                .collect(Collectors.toSet());

        return new JwtAuthenticationToken(
                jwtSource,
                authorities,
                getPrincipalClaimName(jwtSource)
        );
    }

    private String getPrincipalClaimName(Jwt jwtSource) {
        String claimName = JwtClaimNames.SUB;
        if (keyCloakPrincipalAttr != null) {
            claimName = keyCloakPrincipalAttr;
        }
        return jwtSource.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwtSource) {
        Map<String, Object> resourceAccess = jwtSource.getClaim("resource_access");
        if (resourceAccess == null || !resourceAccess.containsKey(resourceId)) {
            return Set.of();
        }

        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(resourceId);
        Collection<String> resourceRoles = (Collection<String>) resource.get("roles");

        if (resourceRoles == null) {
            return Set.of();
        }

        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

}