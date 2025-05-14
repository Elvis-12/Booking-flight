package Fligh.Booking.controller;

import Fligh.Booking.dto.request.LoginRequest;
import Fligh.Booking.dto.request.PasswordResetRequest;
import Fligh.Booking.dto.request.SignupRequest;
import Fligh.Booking.dto.request.TwoFactorAuthRequest;
import Fligh.Booking.dto.response.JwtResponse;
import Fligh.Booking.dto.response.MessageResponse;
import Fligh.Booking.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verifyTwoFactorCode(
            @RequestParam("username") String username,
            @RequestBody TwoFactorAuthRequest twoFactorRequest) {
        JwtResponse response = authService.verifyTwoFactorCode(username, twoFactorRequest.getCode());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        MessageResponse response = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new MessageResponse("Token refreshed successfully"));
    }
    
    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        MessageResponse response = authService.requestPasswordReset(email);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest resetRequest) {
        MessageResponse response = authService.resetPassword(resetRequest.getToken(), resetRequest.getNewPassword());
        return ResponseEntity.ok(response);
    }
}