package Fligh.Booking.service;

import Fligh.Booking.model.Booking;
import Fligh.Booking.model.Ticket;
import Fligh.Booking.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import Fligh.Booking.repository.BookingRepository;
import Fligh.Booking.repository.TicketRepository;

import java.util.Optional;

@Service
public class SecurityService {
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    public boolean isCurrentUser(Long userId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId().equals(userId);
    }
    
    public boolean isBookingOwner(Long bookingId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            return booking.getUser().getId().equals(userDetails.getId());
        }
        
        return false;
    }
    
    public boolean isTicketOwner(Long ticketId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            return ticket.getBooking().getUser().getId().equals(userDetails.getId());
        }
        
        return false;
    }
    
    public boolean isTicketOwnerByNumber(String ticketNumber) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Ticket> ticketOptional = ticketRepository.findByTicketNumber(ticketNumber);
        
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            return ticket.getBooking().getUser().getId().equals(userDetails.getId());
        }
        
        return false;
    }
}