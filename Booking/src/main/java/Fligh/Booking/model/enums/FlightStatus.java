package Fligh.Booking.model.enums;

/**
 * Enum representing the status of a flight.
 */
public enum FlightStatus {
    SCHEDULED,    // Flight is scheduled as planned
    BOARDING,     // Boarding is in progress
    DEPARTED,     // Flight has departed
    IN_AIR,       // Flight is currently in the air
    LANDED,       // Flight has landed at the destination
    DELAYED,      // Flight is delayed
    CANCELLED,    // Flight has been cancelled
    DIVERTED      // Flight has been diverted to a different airport
}