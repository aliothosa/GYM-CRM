package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.dto.LoginRequest;
import com.elioth.epam.gymcrm.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final long EXPIRES_IN_SECONDS = 1200;

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
            Instant issuedAt = Instant.now();
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                    .orElseThrow();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .subject(authentication.getName())
                    .claim("role", role)
                    .issuedAt(issuedAt)
                    .expiresAt(issuedAt.plus(EXPIRES_IN_SECONDS, ChronoUnit.SECONDS))
                    .id(UUID.randomUUID().toString())
                    .build();
            String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            return ResponseEntity.ok(new LoginResponse(token, "Bearer", EXPIRES_IN_SECONDS));
        } catch (AuthenticationException | IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
