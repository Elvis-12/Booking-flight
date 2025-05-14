package Fligh.Booking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Password Reset Request";
        String text = "To reset your password, click the link below:\n\n" +
                "http://localhost:8080/api/auth/reset-password?token=" + token + "\n\n" +
                "If you did not request a password reset, please ignore this email.";
        
        sendSimpleEmail(to, subject, text);
    }
    
    @Async
    public void sendBookingConfirmationEmail(String to, String bookingDetails, String ticketNumber) {
        String subject = "Booking Confirmation";
        String text = "Thank you for your booking!\n\n" +
                "Booking Details:\n" + bookingDetails + "\n\n" +
                "Ticket Number: " + ticketNumber + "\n\n" +
                "Have a safe flight!";
        
        sendSimpleEmail(to, subject, text);
    }
}