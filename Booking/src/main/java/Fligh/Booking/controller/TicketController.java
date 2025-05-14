package Fligh.Booking.controller;

import Fligh.Booking.model.Ticket;
import Fligh.Booking.model.enums.TicketStatus;
import Fligh.Booking.dto.response.MessageResponse;
import Fligh.Booking.security.services.UserDetailsImpl;
import Fligh.Booking.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTicketOwner(#id)")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }
    
    @GetMapping("/number/{ticketNumber}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTicketOwnerByNumber(#ticketNumber)")
    public ResponseEntity<Ticket> getTicketByNumber(@PathVariable String ticketNumber) {
        Ticket ticket = ticketService.getTicketByNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Ticket not found with number: " + ticketNumber));
        return ResponseEntity.ok(ticket);
    }
    
    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isBookingOwner(#bookingId)")
    public ResponseEntity<List<Ticket>> getTicketsByBooking(@PathVariable Long bookingId) {
        List<Ticket> tickets = ticketService.getTicketsByBooking(bookingId);
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/user")
    public ResponseEntity<List<Ticket>> getCurrentUserTickets(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<Ticket> tickets = ticketService.getTicketsByUserId(currentUser.getId());
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/flight/{flightId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Ticket>> getTicketsByFlight(@PathVariable Long flightId) {
        List<Ticket> tickets = ticketService.getTicketsByFlightId(flightId);
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@PathVariable TicketStatus status) {
        List<Ticket> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTicketOwner(#id)")
    public ResponseEntity<Ticket> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {
        Ticket updatedTicket = ticketService.updateTicketStatus(id, status);
        return ResponseEntity.ok(updatedTicket);
    }
    
    @PostMapping("/{id}/check-in")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTicketOwner(#id)")
    public ResponseEntity<?> checkInTicket(@PathVariable Long id) {
        try {
            Ticket updatedTicket = ticketService.checkInTicket(id);
            return ResponseEntity.ok(updatedTicket);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTicketOwner(#id)")
    public ResponseEntity<?> cancelTicket(@PathVariable Long id) {
        try {
            Ticket updatedTicket = ticketService.cancelTicket(id);
            return ResponseEntity.ok(updatedTicket);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(new MessageResponse("Ticket deleted successfully"));
    }
}