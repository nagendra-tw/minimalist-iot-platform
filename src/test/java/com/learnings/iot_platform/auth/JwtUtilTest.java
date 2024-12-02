package com.learnings.iot_platform.auth;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {
    private JwtUtil jwtUtil;
    private Key secretKey;
    private String username = "testUser";
    private String password = "testPassword";
    private String token;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        secretKey = Keys.hmacShaKeyFor("your-very-long-secret-key-for-testing-purposes-12345".getBytes());
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        token = jwtUtil.generateToken(authentication);
    }

    @Test
    void testGenerateToken() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGetUsernameFromToken() {
        String extractedUsername = jwtUtil.getUsernameFromToken(token);
        assertNotNull(extractedUsername);
        assertEquals(username, extractedUsername );
    }

    @Test
    void testValidateToken_ShouldReturnTrue() {
        boolean isValid = jwtUtil.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    void testExpiredToken_ShouldThrowAuthenticationCredentialsNotFoundException() {
        String expiredToken = generateExpiredToken();

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> jwtUtil.validateToken(expiredToken));
    }

    @Test
    void testInvalidSignatureToken_ShouldThrowAuthenticationCredentialsNotFoundException() {
        String invalidSignatureToken = generateInvalidSignatureToken();
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> jwtUtil.validateToken(invalidSignatureToken));
    }

    @Test
    void testMalformedToken_ShouldThrowAuthenticationCredentialsNotFoundException() {
        String malformedToken = "invalid.token.here";
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            jwtUtil.validateToken(malformedToken);
        });
    }

    @Test
    void testUnsupportedToken_ShouldThrowAuthenticationCredentialsNotFoundException() {
        String unsupportedToken = generateUnsupportedToken();
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            jwtUtil.validateToken(unsupportedToken);
        });
    }


    private String generateExpiredToken() {
        return Jwts.builder()
                .subject("testuser")
                .issuer("test-issuer")
                .issuedAt(new Date(System.currentTimeMillis() - 2 * 3600000)) // Issued 2 hours ago
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // Expired 1 hour ago
                .signWith(secretKey)
                .compact();
    }

    private String generateInvalidSignatureToken() {
        Key differentKey = Keys.hmacShaKeyFor("different-secret-key-for-testing".getBytes());
        return Jwts.builder()
                .subject("testuser")
                .issuer("test-issuer")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(differentKey)
                .compact();
    }

    private String generateUnsupportedToken() {
        return Base64.getEncoder().encodeToString("unsupported-token-content".getBytes());
    }
}
