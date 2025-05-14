package Fligh.Booking.repository;

import Fligh.Booking.model.Airline;
import Fligh.Booking.model.Airport;
import Fligh.Booking.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByFlightNumber(String flightNumber);
    
    List<Flight> findByAirline(Airline airline);
    
    List<Flight> findByOriginAirportAndDestinationAirport(Airport originAirport, Airport destinationAirport);
    
    @Query("SELECT f FROM Flight f WHERE f.originAirport.id = :originId AND f.destinationAirport.id = :destinationId AND f.departureDate BETWEEN :startDate AND :endDate")
    List<Flight> findFlights(
            @Param("originId") Long originId,
            @Param("destinationId") Long destinationId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    List<Flight> findByStatus(String status);
}