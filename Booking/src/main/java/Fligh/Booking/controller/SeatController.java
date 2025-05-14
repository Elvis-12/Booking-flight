package Fligh.Booking.controller;

import Fligh.Booking.model.Seat;
import Fligh.Booking.model.enums.SeatClass;
import Fligh.Booking.dto.response.MessageResponse;
import Fligh.Booking.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/seats")
public class SeatController {
    @Autowired
    private SeatService seatService;

    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        List<Seat> seats = seatService.getAllSeats();
        return ResponseEntity.ok(seats);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Seat> getSeatById(@PathVariable Long id) {
        Seat seat = seatService.getSeatById(id);
        return ResponseEntity.ok(seat);
    }
    
    @GetMapping("/class/{seatClass}")
    public ResponseEntity<List<Seat>> getSeatsByClass(@PathVariable SeatClass seatClass) {
        List<Seat> seats = seatService.getSeatsByClass(seatClass);
        return ResponseEntity.ok(seats);
    }
    
    @GetMapping("/price/{maxPrice}")
    public ResponseEntity<List<Seat>> getSeatsByMaxPrice(@PathVariable Double maxPrice) {
        List<Seat> seats = seatService.getSeatsByMaxPrice(maxPrice);
        return ResponseEntity.ok(seats);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Seat> createSeat(@RequestBody Seat seat) {
        Seat savedSeat = seatService.createSeat(seat);
        return ResponseEntity.ok(savedSeat);
    }
    
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Seat>> createBulkSeats(@RequestBody List<Seat> seats) {
        List<Seat> savedSeats = seatService.createBulkSeats(seats);
        return ResponseEntity.ok(savedSeats);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Seat> updateSeat(@PathVariable Long id, @RequestBody Seat seatDetails) {
        Seat updatedSeat = seatService.updateSeat(id, seatDetails);
        return ResponseEntity.ok(updatedSeat);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return ResponseEntity.ok(new MessageResponse("Seat deleted successfully"));
    }
    
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAllSeats() {
        seatService.deleteAllSeats();
        return ResponseEntity.ok(new MessageResponse("All seats deleted successfully"));
    }
}