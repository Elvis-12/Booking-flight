package Fligh.Booking.model.enums;

/**
 * Enum representing the status of a ticket.
 */
public enum TicketStatus {
    CONFIRMED,    // Ticket has been confirmed
    CANCELLED,    // Ticket has been cancelled
    CHECKED_IN,   // Passenger has checked in
    BOARDED,      // Passenger has boarded the plane
    NO_SHOW,      // Passenger did not show up for the flight
    REFUNDED,     // Ticket has been refunded
    EXCHANGED     // Ticket has been exchanged for another flight
}