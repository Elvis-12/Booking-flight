package Fligh.Booking.service;

import Fligh.Booking.model.Booking;
import Fligh.Booking.model.FlightSeat;
import Fligh.Booking.model.Ticket;
import Fligh.Booking.model.User;
import Fligh.Booking.model.enums.BookingStatus;
import Fligh.Booking.model.enums.TicketStatus;
import Fligh.Booking.repository.BookingRepository;
import Fligh.Booking.repository.FlightSeatRepository;
import Fligh.Booking.repository.TicketRepository;
import Fligh.Booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FlightSeatRepository flightSeatRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private EmailService emailService;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }
    
    public List<Booking> getBookingsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return bookingRepository.findByUser(user);
    }
    
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status.toString());
    }
    
    @Transactional
    public Booking createBooking(Long userId, List<Long> flightSeatIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);
        
        // Calculate total price
        double totalPrice = 0.0;
        List<FlightSeat> flightSeats = new ArrayList<>();
        
        for (Long seatId : flightSeatIds) {
            FlightSeat flightSeat = flightSeatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Flight seat not found with id: " + seatId));
            
            if (!flightSeat.getIsAvailable() || flightSeat.getIsReserved()) {
                throw new RuntimeException("Seat " + flightSeat.getSeat().getSeatNumber() + " is not available");
            }
            
            totalPrice += flightSeat.getPrice();
            flightSeats.add(flightSeat);
        }
        
        booking.setTotalPrice(totalPrice);
        booking = bookingRepository.save(booking);
        
        // Create tickets and update seat status
        StringBuilder bookingDetails = new StringBuilder();
        for (FlightSeat flightSeat : flightSeats) {
            // Update seat status
            flightSeat.setIsAvailable(false);
            flightSeat.setIsReserved(true);
            flightSeatRepository.save(flightSeat);
            
            // Create ticket
            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setFlightSeat(flightSeat);
            ticket.setTicketNumber(generateTicketNumber());
            ticket.setBookingTime(LocalDateTime.now());
            ticket.setStatus(TicketStatus.CONFIRMED);
            ticketRepository.save(ticket);
            
            // Add to booking details for email
            bookingDetails.append("Flight: ").append(flightSeat.getFlight().getFlightNumber())
                    .append(", Seat: ").append(flightSeat.getSeat().getSeatNumber())
                    .append(", Class: ").append(flightSeat.getSeat().getSeatClass())
                    .append(", Price: $").append(flightSeat.getPrice())
                    .append("\n");
        }
        
        // Send confirmation email
        emailService.sendBookingConfirmationEmail(
                user.getEmail(),
                bookingDetails.toString(),
                "See tickets in your account"
        );
        
        return booking;
    }
    
    @Transactional
    public Booking updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(status);
        
        // Update related tickets status
        List<Ticket> tickets = ticketRepository.findByBooking(booking);
        for (Ticket ticket : tickets) {
            // Map booking status to appropriate ticket status
            TicketStatus ticketStatus = mapBookingStatusToTicketStatus(status);
            ticket.setStatus(ticketStatus);
            ticketRepository.save(ticket);
            
            // If cancelling, make the seat available again
            if (status == BookingStatus.CANCELLED) {
                FlightSeat flightSeat = ticket.getFlightSeat();
                flightSeat.setIsAvailable(true);
                flightSeat.setIsReserved(false);
                flightSeatRepository.save(flightSeat);
            }
        }
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public void deleteBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        
        // First make all seats available again
        List<Ticket> tickets = ticketRepository.findByBooking(booking);
        for (Ticket ticket : tickets) {
            FlightSeat flightSeat = ticket.getFlightSeat();
            flightSeat.setIsAvailable(true);
            flightSeat.setIsReserved(false);
            flightSeatRepository.save(flightSeat);
            
            // Delete the ticket
            ticketRepository.delete(ticket);
        }
        
        // Delete the booking
        bookingRepository.delete(booking);
    }
    
    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private TicketStatus mapBookingStatusToTicketStatus(BookingStatus bookingStatus) {
        switch (bookingStatus) {
            case CONFIRMED:
                return TicketStatus.CONFIRMED;
            case CANCELLED:
                return TicketStatus.CANCELLED;
            case REFUNDED:
                return TicketStatus.REFUNDED;
            case COMPLETED:
                return TicketStatus.CONFIRMED; // Ticket remains confirmed even when booking is completed
            default:
                return TicketStatus.CONFIRMED;
        }
    }
}