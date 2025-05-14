package Fligh.Booking.model.enums;

/**
 * Enum representing the status of a schedule.
 */
public enum ScheduleStatus {
    SCHEDULED,     // Schedule is planned as specified
    BOARDING,      // Boarding is in progress
    DEPARTED,      // Flight has departed
    IN_TRANSIT,    // Flight is in transit
    ARRIVED,       // Flight has arrived at destination
    DELAYED,       // Schedule is delayed
    CANCELLED,     // Schedule has been cancelled
    RESCHEDULED    // Schedule has been changed to a different time
}