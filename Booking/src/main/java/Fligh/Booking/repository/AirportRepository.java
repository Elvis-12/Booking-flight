package Fligh.Booking.repository;

import Fligh.Booking.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByCode(String code);
    
    List<Airport> findByCountry(String country);
    
    List<Airport> findByCity(String city);
    
    boolean existsByCode(String code);
}