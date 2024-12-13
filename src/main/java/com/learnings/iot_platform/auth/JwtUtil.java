package com.learnings.iot_platform.auth;

//import io.github.cdimascio.dotenv.Dotenv;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private String JWT_SECRET;

    public JwtUtil() {

        JWT_SECRET = System.getenv("JWT_SECRET");
    }

    public String generateToken(Authentication authentication) {
        String username = (String) authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + 3600 * 1000);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch(SignatureException | MalformedJwtException e) {
            System.out.println("Invalid JWT signature or claims: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            // JWT is expired
            System.out.println("JWT expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            // JWT is unsupported
            System.out.println("Unsupported JWT: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // JWT claims are invalid
            System.out.println("Invalid JWT claims: " + e.getMessage());
        } catch (JwtException e) {
            // General JWT parsing exception
            System.out.println("JWT parsing exception: " + e.getMessage());
        }
        throw new AuthenticationCredentialsNotFoundException("Invalid Token");
    }
}
