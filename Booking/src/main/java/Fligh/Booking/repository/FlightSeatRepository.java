package Fligh.Booking.repository;

import Fligh.Booking.model.Flight;
import Fligh.Booking.model.FlightSeat;
import Fligh.Booking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightSeatRepository extends JpaRepository<FlightSeat, Long> {
    List<FlightSeat> findByFlight(Flight flight);
    
    List<FlightSeat> findByFlightAndIsAvailable(Flight flight, Boolean isAvailable);
    
    List<FlightSeat> findByFlightAndSeat(Flight flight, Seat seat);
    
    List<FlightSeat> findByFlightAndSeat_SeatClass(Flight flight, String seatClass);
    
    List<FlightSeat> findByFlightAndPriceLessThanEqual(Flight flight, Double price);
}