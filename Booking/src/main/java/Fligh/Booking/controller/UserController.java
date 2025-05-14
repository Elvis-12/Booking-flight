package Fligh.Booking.controller;

import Fligh.Booking.dto.request.TwoFactorAuthRequest;
import Fligh.Booking.dto.response.MessageResponse;
import Fligh.Booking.dto.response.TwoFactorSetupResponse;
import Fligh.Booking.model.User;
import Fligh.Booking.security.services.UserDetailsImpl;
import Fligh.Booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        User user = userService.getUserById(currentUser.getId());
        // Clear sensitive information
        user.setPassword(null);
        user.setMfaSecret(null);
        user.setPasswordResetToken(null);
        
        return ResponseEntity.ok(user);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // Clear sensitive information
        users.forEach(user -> {
            user.setPassword(null);
            user.setMfaSecret(null);
            user.setPasswordResetToken(null);
        });
        
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        // Clear sensitive information
        user.setPassword(null);
        user.setMfaSecret(null);
        user.setPasswordResetToken(null);
        
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        try {
            MessageResponse response = userService.updatePassword(currentUser.getId(), currentPassword, newPassword);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/setup-2fa")
    public ResponseEntity<TwoFactorSetupResponse> setupTwoFactorAuth(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        TwoFactorSetupResponse response = userService.setupTwoFactorAuth(currentUser.getId());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/confirm-2fa")
    public ResponseEntity<?> confirmTwoFactorAuth(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestBody TwoFactorAuthRequest request) {
        try {
            MessageResponse response = userService.confirmTwoFactorAuth(currentUser.getId(), request.getCode());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/disable-2fa")
    public ResponseEntity<?> disableTwoFactorAuth(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestBody TwoFactorAuthRequest request) {
        try {
            MessageResponse response = userService.disableTwoFactorAuth(currentUser.getId(), request.getCode());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }
}