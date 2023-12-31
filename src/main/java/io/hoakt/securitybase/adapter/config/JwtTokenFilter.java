package io.hoakt.securitybase.adapter.config;

import io.hoakt.securitybase.application.port.outgoing.jwt.GetClaimsFromTokenPort;
import io.hoakt.securitybase.application.port.outgoing.jwt.ValidateTokenPort;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String ROLE_PREFIX = "ROLE_";

    private final ValidateTokenPort validateTokenPort;
    private final GetClaimsFromTokenPort getClaimsFromTokenPort;


    @Value("${jwt.roles_claim_key}")
    private String rolesClaimKey;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws IOException, ServletException {
        if (request.getRequestURI().contains(SecurityConfig.PUBLIC_API_PATH)) {
            filterChain.doFilter(request, response); // proceed since we do not need a token
        } else {
            try {
                String token = extractToken(request);
                if (token != null && validateTokenPort.validateToken(token)) {
                    setAuthentication(token);
                    filterChain.doFilter(request, response); // proceed since token is valid
                    return;
                }
            } catch (Exception e) {
                log.error("Error during authentication", e);
            }

            handleUnauthorized(response);
        }
    }

    private void setAuthentication(String token) {
        Optional<Claims> claims = getClaimsFromTokenPort.extractClaimsFromToken(token);
        if (claims.isPresent()) {
            String username = claims.get().getSubject();
            String roles = claims.get().get(rolesClaimKey, String.class);
            List<SimpleGrantedAuthority> authorities = Objects.isNull(roles) ? Collections.emptyList() : Arrays.stream(roles.split(","))
                    .map(role -> role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else
            throw new IllegalStateException("trying to get claims from a token that is not valid"); // TODO better handling
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void handleUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("Unauthorized: Invalid or missing token");
    }
}
