package Fligh.Booking.repository;

import Fligh.Booking.model.Seat;
import Fligh.Booking.model.enums.SeatClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findBySeatClass(SeatClass seatClass);
    
    List<Seat> findByPriceLessThanEqual(Double price);
}