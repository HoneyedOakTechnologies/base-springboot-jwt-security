package io.hoakt.securitybase.application.service;

import io.hoakt.securitybase.application.domain.User;
import io.hoakt.securitybase.application.port.outgoing.jwt.GenerateAuthTokenForUserPort;
import io.hoakt.securitybase.application.port.outgoing.jwt.GenerateRefreshTokenForUserPort;
import io.hoakt.securitybase.application.port.outgoing.jwt.GetClaimsFromTokenPort;
import io.hoakt.securitybase.application.port.outgoing.jwt.ValidateTokenPort;
import io.hoakt.securitybase.application.port.outgoing.util.IdGeneratorPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService implements GenerateAuthTokenForUserPort, GenerateRefreshTokenForUserPort, GetClaimsFromTokenPort, ValidateTokenPort {
    private static final String ISSUER = "https://www.hoakt.io/base-springboot-jwt-security";
    private static final String AUDIENCE = "hoakt.io";

    private final IdGeneratorPort<?> idGeneratorPort;
    private final Clock clock;

    @Value("${jwt.allowed_clock_skew}")
    private long allowedClockSkew;
    @Value("${jwt.auth_token_validity_seconds}")
    private long authTokenValiditySeconds;
    @Value("${jwt.refresh_token_validity_seconds}")
    private long refreshTokenValiditySeconds;
    @Value("${jwt.roles_claim_key}")
    private String rolesClaimKey;

    @Override
    public Optional<String> generateAuthTokenForUser(User user) {
        String tokenId = idGeneratorPort.toString();
        // TODO: store token inf in DB to allow revoking and tracking

        return Optional.ofNullable(user)
                .map(usr -> commonTokenBuilder(tokenId, refreshTokenValiditySeconds)
                        .subject(user.username())
                        .claims()
                        .add(rolesClaimKey, String.join(",", user.roles()))
                        .and()
                        .compact());
    }

    @Override
    public Optional<String> generateRefreshTokenForUser(User user) {
        String tokenId = idGeneratorPort.toString();
        // TODO: store token inf in DB to allow revoking and tracking

        return Optional.ofNullable(user)
                .map(User::username)
                .map(username -> commonTokenBuilder(tokenId, authTokenValiditySeconds)
                        .subject(username)
                        .compact());
    }

    @Override
    public boolean validateToken(String token) {
        Optional<Claims> optionalClaims = extractClaimsFromToken(token);

        //could not extract claims, so it might already be expired or the decryption did not succeed etc. ==> invalid token
        if (optionalClaims.isEmpty()) {
            return false;
        }

        Claims claims = optionalClaims.get();

        String tokenId = claims.getId();
        // TODO: validate that the token has not been revoked, and that the fingerprints match for now assume it passes that validation

        return true;
    }

    @Override
    public Optional<Claims> extractClaimsFromToken(String token) {
        SecretKey secretKey = getSecretKey()
                .orElseThrow(() -> new IllegalStateException("password for JWT decryption could not be found"));
        try {
            return Optional.ofNullable(Jwts.parser()
                    .clock(() -> new Date(clock.millis()))
                    .zip()
                    .add(Jwts.ZIP.GZIP)
                    .and()
                    .decryptWith(secretKey)
                    .clockSkewSeconds(allowedClockSkew)
                    .requireAudience(AUDIENCE)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseEncryptedClaims(token)
                    .getPayload());
        } catch (JwtException e) {
            // TODO error handling and logging
        }

        return Optional.empty();
    }


    private JwtBuilder commonTokenBuilder(String tokenId, long tokenValiditySeconds) {
        SecretKey secretKey = getSecretKey()
                .orElseThrow(() -> new IllegalStateException("password.txt for JWT encryption could not be found"));

        Instant currentInstant = clock.instant(); // use clock to have control over the time, so we can unit test this better
        Instant issuedAt = currentInstant.truncatedTo(ChronoUnit.SECONDS);
        Instant notBefore = currentInstant.truncatedTo(ChronoUnit.SECONDS);
        Instant expiration = currentInstant.plus(tokenValiditySeconds, ChronoUnit.SECONDS).truncatedTo(ChronoUnit.SECONDS);

        return Jwts.builder()
                .audience()
                .add(AUDIENCE)
                .and()
                .issuer(ISSUER)
                .issuedAt(new Date(issuedAt.toEpochMilli()))
                .compressWith(Jwts.ZIP.GZIP)
                .encryptWith(secretKey, Jwts.KEY.A256GCMKW, Jwts.ENC.A256GCM)
                .expiration(new Date(expiration.toEpochMilli()))
                .id(tokenId)
                .notBefore(new Date(notBefore.toEpochMilli()));
    }

    private Optional<SecretKey> getSecretKey() {
        Optional<SecretKey> secretKey = Optional.empty();
        try {
            int keyLength = 256; // we use AES 256 bits.
            byte[] keyBytes = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("password.txt")).toURI()))).getBytes(StandardCharsets.UTF_8);
            secretKey = Optional.of(new SecretKeySpec(Arrays.copyOf(keyBytes, keyLength / 8), "AES"));
        } catch (Exception e) {
            log.error("", e); // TODO better logging
        }

        return secretKey;
    }
}
