package Fligh.Booking.service;

import Fligh.Booking.dto.response.MessageResponse;
import Fligh.Booking.dto.response.TwoFactorSetupResponse;
import Fligh.Booking.model.User;
import Fligh.Booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public MessageResponse updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return new MessageResponse("Password updated successfully");
    }
    
    @Transactional
    public TwoFactorSetupResponse setupTwoFactorAuth(Long userId) {
        User user = getUserById(userId);
        
        String secret = twoFactorAuthService.generateNewSecret();
        String qrCodeImageUri = twoFactorAuthService.getQrCodeImageUri(secret, user.getUsername());
        
        // Store secret temporarily (will be confirmed later)
        user.setMfaSecret(secret);
        userRepository.save(user);
        
        return new TwoFactorSetupResponse(qrCodeImageUri, secret);
    }
    
    @Transactional
    public MessageResponse confirmTwoFactorAuth(Long userId, String code) {
        User user = getUserById(userId);
        
        if (user.getMfaSecret() == null) {
            throw new RuntimeException("Two-factor authentication has not been set up");
        }
        
        // Verify the code
        if (!twoFactorAuthService.validateCode(code, user.getMfaSecret())) {
            throw new RuntimeException("Invalid code");
        }
        
        // Enable 2FA
        twoFactorAuthService.enableTwoFactorAuth(user, user.getMfaSecret());
        
        return new MessageResponse("Two-factor authentication enabled successfully");
    }
    
    @Transactional
    public MessageResponse disableTwoFactorAuth(Long userId, String code) {
        User user = getUserById(userId);
        
        if (!user.isMfaEnabled() || user.getMfaSecret() == null) {
            throw new RuntimeException("Two-factor authentication is not enabled");
        }
        
        // Verify the code
        if (!twoFactorAuthService.validateCode(code, user.getMfaSecret())) {
            throw new RuntimeException("Invalid code");
        }
        
        // Disable 2FA
        twoFactorAuthService.disableTwoFactorAuth(user);
        
        return new MessageResponse("Two-factor authentication disabled successfully");
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }
}