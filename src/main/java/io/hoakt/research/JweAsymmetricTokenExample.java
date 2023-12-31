package io.hoakt.research;

import io.hoakt.securitybase.application.port.outgoing.util.IdGeneratorPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JweAsymmetricTokenExample {

    private static final String ROLES_CLAIM_KEY = "ROLES";
    private static final String ISSUER = "https://io.hoakt/base-springboot-jwt-security";
    private static final String AUDIENCE = "hoakt.io";

    private final IdGeneratorPort<?> idGeneratorPort;
    private final Clock clock;
    private final long allowedClockSkew;
    private final long authTokenValiditySeconds;
    private final long refreshTokenValiditySeconds;
    private KeyPair keyPair;

    @PostConstruct
    public void init() {
        try {
            String privateKeyPEM = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("pkcs8_private_key.pem")).toURI())));
            String publicKeyPEM = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("public-key.pem")).toURI())));

            // Remove the first and last lines
            privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "").replaceAll(System.lineSeparator(), "").replace("-----END PRIVATE KEY-----", "");
            publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "").replaceAll(System.lineSeparator(), "").replace("-----END PUBLIC KEY-----", "");

            // Base64 decode the data
            byte[] encodedPrivateKey = Base64.getDecoder().decode(privateKeyPEM);
            byte[] encodedPublicKey = Base64.getDecoder().decode(publicKeyPEM);

            KeyFactory keyFactory = KeyFactory.getInstance("EC");

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);

            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            this.keyPair = new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            log.error("could not load KeyPair for jwt security", e);
            this.keyPair = null;
        }
    }

    public String createRefreshToken(@Nonnull String subject) {
        String tokenId = idGeneratorPort.toString();
        // TODO: store token inf in DB to allow revoking and tracking

        return commonTokenBuilder(tokenId, refreshTokenValiditySeconds)
                .subject(subject)
                .compact();
    }

    public String createAuthToken(@Nonnull String subject, @Nonnull String[] roles) {
        String tokenId = idGeneratorPort.toString();
        // TODO: store token inf in DB to allow revoking and tracking

        return commonTokenBuilder(tokenId, authTokenValiditySeconds)
                .subject(subject)
                .claims()
                .add(ROLES_CLAIM_KEY, new String[]{"ADMIN", "USER"})
                .and()
                .compact();
    }

    public Optional<Claims> extractAllClaims(String jwe) {
        PrivateKey privateKey = getKeyPair()
                .map(KeyPair::getPrivate)
                .orElseThrow(() -> new IllegalStateException("PrivateKey for JWT decryption could not be found"));
        try {
            return Optional.ofNullable(Jwts.parser()
                    .clock(() -> new Date(clock.millis()))
                    .zip()
                    .add(Jwts.ZIP.GZIP)
                    .and()
                    .decryptWith(privateKey)
                    .clockSkewSeconds(allowedClockSkew)
                    .requireAudience(AUDIENCE)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseEncryptedClaims(jwe)
                    .getPayload());
        } catch (JwtException e) {
            // TODO error handling and logging
        }

        return Optional.empty();
    }

    public boolean isTokenValid(String token) {
        Optional<Claims> optionalClaims = extractAllClaims(token);

        //could not extract claims, so it might already be expired or the decryption did not succeed etc. ==> invalid token
        if (optionalClaims.isEmpty()) {
            return false;
        }

        Claims claims = optionalClaims.get();

        String tokenId = claims.getId();
        // TODO: validate that the token has not been revoked, and that the fingerprints match for now assume it passes that validation

        return true;
    }

    private JwtBuilder commonTokenBuilder(String tokenId, long tokenValiditySeconds) {
        KeyPair key = getKeyPair()
                .orElseThrow(() -> new IllegalStateException("Keypair for JWT encryption could not be found"));
        String keyId = getKeyId(key);

        Instant currentInstant = clock.instant(); // use clock to have control over the time, so we can unit test this better
        Instant issuedAt = currentInstant.truncatedTo(ChronoUnit.SECONDS);
        Instant notBefore = currentInstant.truncatedTo(ChronoUnit.SECONDS);
        Instant expiration = currentInstant.plus(tokenValiditySeconds, ChronoUnit.SECONDS).truncatedTo(ChronoUnit.SECONDS);

        return Jwts.builder()
                .header().keyId(keyId)
                .and()
                .audience()
                .add(AUDIENCE)
                .and()
                .issuer(ISSUER)
                .issuedAt(new Date(issuedAt.toEpochMilli()))
                .compressWith(Jwts.ZIP.GZIP)
                .encryptWith(key.getPublic(), Jwts.KEY.ECDH_ES_A256KW, Jwts.ENC.A256GCM)
                .expiration(new Date(expiration.toEpochMilli()))
                .id(tokenId)
                .notBefore(new Date(notBefore.toEpochMilli()));
    }

    private Optional<KeyPair> getKeyPair() {
        return Optional.ofNullable(this.keyPair);
    }

    private String getKeyId(KeyPair keyPair) {
        try {
            // Create a SHA-256 digest
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Compute the digest for the encoded public key
            byte[] hash = digest.digest(keyPair.getPublic().getEncoded());

            // Encode the hash as a base64 URL-safe string
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating key ID", e); //TODO better error handling
        }
    }
}
