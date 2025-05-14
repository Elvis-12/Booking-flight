package Fligh.Booking.repository;

import Fligh.Booking.model.Booking;
import Fligh.Booking.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByBooking(Booking booking);
    
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    
    List<Ticket> findByStatus(String status);
    
    @Query("SELECT t FROM Ticket t JOIN t.booking b JOIN b.user u WHERE u.id = :userId")
    List<Ticket> findTicketsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Ticket t JOIN t.flightSeat fs JOIN fs.flight f WHERE f.id = :flightId")
    List<Ticket> findTicketsByFlightId(@Param("flightId") Long flightId);
    
    List<Ticket> findByBooking_Id(Long bookingId);
}