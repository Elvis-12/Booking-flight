package Fligh.Booking.service;

import Fligh.Booking.model.Booking;
import Fligh.Booking.model.FlightSeat;
import Fligh.Booking.model.Ticket;
import Fligh.Booking.model.enums.TicketStatus;
import Fligh.Booking.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // CRUD Operations
    
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
    
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }
    
    public Optional<Ticket> getTicketByNumber(String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber);
    }
    
    @Transactional
    public Ticket createTicket(Ticket ticket) {
        // Generate a ticket number if not provided
        if (ticket.getTicketNumber() == null || ticket.getTicketNumber().isEmpty()) {
            ticket.setTicketNumber(generateTicketNumber());
        }
        
        // Set booking time if not provided
        if (ticket.getBookingTime() == null) {
            ticket.setBookingTime(LocalDateTime.now());
        }
        
        return ticketRepository.save(ticket);
    }
    
    @Transactional
    public Ticket updateTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
    
    @Transactional
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }
    
    // Business methods
    
    @Transactional
    public Ticket createTicketForBookingAndSeat(Booking booking, FlightSeat flightSeat) {
        Ticket ticket = new Ticket();
        ticket.setBooking(booking);
        ticket.setFlightSeat(flightSeat);
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setBookingTime(LocalDateTime.now());
        ticket.setStatus(TicketStatus.CONFIRMED);
        
        return ticketRepository.save(ticket);
    }
    
    @Transactional
    public Ticket updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        Ticket ticket = getTicketById(ticketId);
        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }
    
    // Methods that correspond to repository methods
    
    public List<Ticket> getTicketsByBooking(Long bookingId) {
        return ticketRepository.findByBooking_Id(bookingId);
    }
    
    public List<Ticket> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status.name());
    }
    
    public List<Ticket> getTicketsByUserId(Long userId) {
        return ticketRepository.findTicketsByUserId(userId);
    }
    
    public List<Ticket> getTicketsByFlightId(Long flightId) {
        return ticketRepository.findTicketsByFlightId(flightId);
    }
    
    // Helper methods
    
    private String generateTicketNumber() {
        // Generate a unique ticket number, e.g., using UUID
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    @Transactional
    public Ticket cancelTicket(Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
        
        // Check if the ticket can be cancelled
        if (ticket.getStatus() == TicketStatus.CHECKED_IN) {
            throw new RuntimeException("Cannot cancel a checked-in ticket");
        }
        
        ticket.setStatus(TicketStatus.CANCELLED);
        return ticketRepository.save(ticket);
    }
    
    @Transactional
    public Ticket checkInTicket(Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
        
        // Validate if ticket can be checked in
        if (ticket.getStatus() != TicketStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed tickets can be checked in");
        }
        
        // Check if it's within the valid check-in time frame
        // This would normally include business logic to check if the flight hasn't departed yet
        // For now, we'll just check the status
        
        ticket.setStatus(TicketStatus.CHECKED_IN);
        return ticketRepository.save(ticket);
    }
    
    @Transactional
    public boolean validateTicket(String ticketNumber) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketNumber(ticketNumber);
        return ticketOpt.isPresent() && 
               (ticketOpt.get().getStatus() == TicketStatus.CONFIRMED || 
                ticketOpt.get().getStatus() == TicketStatus.CHECKED_IN);
    }
}