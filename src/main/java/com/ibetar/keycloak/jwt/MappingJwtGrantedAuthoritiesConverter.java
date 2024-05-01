package com.ibetar.keycloak.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class MappingJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp");
    private final Map<String,String> scopes;
    @Setter
    private String authoritiesClaimName = null;
    @Setter
    private String authorityPrefix = "SCOPE_";

    MappingJwtGrantedAuthoritiesConverter(Map<String,String> scopes) {
        this.scopes = scopes == null ? Collections.emptyMap(): scopes;
    }

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {

        Collection<String> tokenScopes = parseScopesClaim(jwt);
        if ( tokenScopes.isEmpty()) {
            return Collections.emptyList();
        }

        return tokenScopes.stream()
                .map(s -> scopes.getOrDefault(s, s))
                .map(s -> this.authorityPrefix + s)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    protected Collection<String> parseScopesClaim(Jwt jwt) {

        String scopeClaim;

        if ( this.authoritiesClaimName == null ) {
            scopeClaim = WELL_KNOWN_AUTHORITIES_CLAIM_NAMES.stream()
                    .filter(jwt::hasClaim)
                    .findFirst()
                    .orElse(null);

            if ( scopeClaim == null ) {
                return Collections.emptyList();
            }
        }
        else {
            scopeClaim = this.authoritiesClaimName;
        }

        Object v = jwt.getClaim(scopeClaim);
        if ( v == null ) {
            return Collections.emptyList();
        }

        if ( v instanceof String) {
            return Arrays.asList(v.toString().split(" "));
        }
        else if ( v instanceof Collection ) {
            return ((Collection<?>)v).stream()
                    .map(Object::toString)
                    .collect(Collectors.toCollection(HashSet::new));
        }
        return Collections.emptyList();
    }
}
