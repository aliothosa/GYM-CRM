package com.elioth.epam.gymcrm.controller;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.exception.EntityNotFoundException;
import com.elioth.epam.gymcrm.logging.UserLogger;
import com.elioth.epam.gymcrm.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/auth")
@Api(tags = "Authentication", description = "Authentication operations")
public class AuthenticationController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserLogger userLogger;

    private final AuthService authService;

    @Autowired
    public AuthenticationController(AuthService authService, UserLogger userLogger) {
        this.userLogger = userLogger;
        this.authService = authService;
    }

    @ApiOperation(
            value = "Log in",
            notes = "Authenticates a trainee or trainer and stores the authenticated session",
            response = Void.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "User authenticated successfully"),
            @ApiResponse(code = 401, message = "Invalid username or password")
    })
    @PostMapping(value = "/login")
    public ResponseEntity<Void> login(
            @ApiParam(value = "Username", required = true)
            @RequestParam String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password,
            @ApiParam(hidden = true)
            HttpSession httpSession
    ){
        logger.info("Attempting to authenticate user: {}", username);

        AuthSession authSession;

        try{
            authSession = authService.loginTrainee(username, password);
        }catch (EntityNotFoundException e){
           try {
               authSession = authService.loginTrainer(username, password);
           } catch (EntityNotFoundException e1) {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
           }
        }

        httpSession.setAttribute("AUTH_SESSION", authSession);

        userLogger.log(username,"Successfully authenticated user: {} at {}", username, LocalDateTime.now());

        return ResponseEntity.ok().build();
    }

    @ApiOperation(
            value = "Change authenticated user",
            notes = "Replaces the current authenticated session with another valid user",
            response = Void.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Authenticated user changed successfully"),
            @ApiResponse(code = 401, message = "Invalid username or password")
    })
    @PutMapping(value = "/login")
    public ResponseEntity<Void> changeLogin(
            @ApiParam(value = "Username", required = true)
            @RequestParam String username,
            @ApiParam(value = "User password", required = true)
            @RequestParam String password,
            @ApiParam(hidden = true)
            @SessionAttribute("AUTH_SESSION")
                    AuthSession pastAuthSession,
            @ApiParam(hidden = true)
            HttpSession httpSession
    ){
        logger.info("Attempting to change login from user: {} to user {}", pastAuthSession.username(), username);

        AuthSession newAuthSession;

        try{
            newAuthSession = authService.loginTrainee(username, password);
        }catch (EntityNotFoundException e){
            try {
                newAuthSession = authService.loginTrainer(username, password);
            } catch (EntityNotFoundException e1) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        httpSession.setAttribute("AUTH_SESSION", newAuthSession);

        userLogger.log(username,"Successfully authenticated user: {}", username);

        userLogger.log(pastAuthSession.username(), "logged out of account of user", pastAuthSession.username());

        return ResponseEntity.ok().build();
    }
}
