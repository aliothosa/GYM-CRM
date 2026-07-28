package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.dto.LoginRequest;
import com.elioth.epam.gymcrm.dto.LoginResponse;
import com.elioth.epam.gymcrm.security.LoginAttemptService;
import com.elioth.epam.gymcrm.security.TokenRevocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final long EXPIRES_IN_SECONDS = 1200;

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final LoginAttemptService loginAttemptService;
    private final TokenRevocationService tokenRevocationService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder,
                                    LoginAttemptService loginAttemptService, TokenRevocationService tokenRevocationService) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.loginAttemptService = loginAttemptService;
        this.tokenRevocationService = tokenRevocationService;
    }

    @PostMapping("/login")
    @SecurityRequirements
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (loginAttemptService.isBlocked(request.username())) {
            return ResponseEntity.status(HttpStatus.LOCKED).build();
        }
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
            loginAttemptService.reset(request.username());
            return ResponseEntity.ok(new LoginResponse(token, "Bearer", EXPIRES_IN_SECONDS));
        } catch (AuthenticationException | IllegalArgumentException exception) {
            return ResponseEntity.status(loginAttemptService.recordFailure(request.username())
                    ? HttpStatus.LOCKED : HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication;
        tokenRevocationService.revoke(jwtAuthentication.getToken().getId(), jwtAuthentication.getToken().getExpiresAt());
        return ResponseEntity.noContent().build();
    }
}
