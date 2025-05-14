package Fligh.Booking.model.enums;

/**
 * Enum representing the status of a booking.
 */
public enum BookingStatus {
    PENDING,      // Booking is in the process of being confirmed
    CONFIRMED,    // Booking has been confirmed
    CANCELLED,    // Booking has been cancelled
    COMPLETED,    // Booking has been completed (post-flight)
    REFUNDED,     // Booking has been refunded
    EXPIRED       // Booking has expired due to no payment or confirmation
}