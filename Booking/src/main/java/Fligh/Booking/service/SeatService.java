package Fligh.Booking.service;

import Fligh.Booking.model.Seat;
import Fligh.Booking.model.enums.SeatClass;
import Fligh.Booking.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public Seat getSeatById(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found with id: " + id));
    }

    public List<Seat> getSeatsByClass(SeatClass seatClass) {
        return seatRepository.findBySeatClass(seatClass);
    }

    public List<Seat> getSeatsByMaxPrice(Double maxPrice) {
        return seatRepository.findByPriceLessThanEqual(maxPrice);
    }

    @Transactional
    public Seat createSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @Transactional
    public Seat updateSeat(Long id, Seat seatDetails) {
        Seat seat = getSeatById(id);
        
        seat.setSeatClass(seatDetails.getSeatClass());
        seat.setSeatNumber(seatDetails.getSeatNumber());
        seat.setPrice(seatDetails.getPrice());
        
        return seatRepository.save(seat);
    }

    @Transactional
    public void deleteSeat(Long id) {
        Seat seat = getSeatById(id);
        seatRepository.delete(seat);
    }
    
    @Transactional
    public List<Seat> createBulkSeats(List<Seat> seats) {
        return seatRepository.saveAll(seats);
    }
    
    @Transactional
    public void deleteAllSeats() {
        seatRepository.deleteAll();
    }
}