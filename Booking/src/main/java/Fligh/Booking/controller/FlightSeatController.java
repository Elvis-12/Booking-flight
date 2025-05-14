package Fligh.Booking.controller;

import Fligh.Booking.model.FlightSeat;
import Fligh.Booking.model.enums.SeatClass;
import Fligh.Booking.dto.response.MessageResponse;
import Fligh.Booking.service.FlightSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/flight-seats")
public class FlightSeatController {
    @Autowired
    private FlightSeatService flightSeatService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FlightSeat>> getAllFlightSeats() {
        List<FlightSeat> flightSeats = flightSeatService.getAllFlightSeats();
        return ResponseEntity.ok(flightSeats);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FlightSeat> getFlightSeatById(@PathVariable Long id) {
        FlightSeat flightSeat = flightSeatService.getFlightSeatById(id);
        return ResponseEntity.ok(flightSeat);
    }
    
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<FlightSeat>> getSeatsByFlight(@PathVariable Long flightId) {
        List<FlightSeat> flightSeats = flightSeatService.getFlightSeatsByFlight(flightId);
        return ResponseEntity.ok(flightSeats);
    }
    
    @GetMapping("/flight/{flightId}/available")
    public ResponseEntity<List<FlightSeat>> getAvailableSeatsByFlight(@PathVariable Long flightId) {
        List<FlightSeat> availableSeats = flightSeatService.getAvailableFlightSeatsByFlight(flightId);
        return ResponseEntity.ok(availableSeats);
    }
    
    @GetMapping("/flight/{flightId}/class/{seatClass}")
    public ResponseEntity<List<FlightSeat>> getSeatsByFlightAndClass(
            @PathVariable Long flightId, 
            @PathVariable SeatClass seatClass) {
        List<FlightSeat> flightSeats = flightSeatService.getFlightSeatsByFlightAndClass(flightId, seatClass);
        return ResponseEntity.ok(flightSeats);
    }
    
    @GetMapping("/flight/{flightId}/price/{maxPrice}")
    public ResponseEntity<List<FlightSeat>> getSeatsByFlightAndMaxPrice(
            @PathVariable Long flightId, 
            @PathVariable Double maxPrice) {
        List<FlightSeat> flightSeats = flightSeatService.getFlightSeatsByFlightAndMaxPrice(flightId, maxPrice);
        return ResponseEntity.ok(flightSeats);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFlightSeat(@RequestBody FlightSeat flightSeat) {
        try {
            FlightSeat savedFlightSeat = flightSeatService.createFlightSeat(flightSeat);
            return ResponseEntity.ok(savedFlightSeat);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/flight/{flightId}/seats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFlightSeatsForFlight(
            @PathVariable Long flightId,
            @RequestBody List<Long> seatIds) {
        try {
            List<FlightSeat> createdSeats = flightSeatService.createFlightSeatsForFlight(flightId, seatIds);
            return ResponseEntity.ok(createdSeats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFlightSeat(@PathVariable Long id, @RequestBody FlightSeat flightSeatDetails) {
        FlightSeat updatedFlightSeat = flightSeatService.updateFlightSeat(id, flightSeatDetails);
        return ResponseEntity.ok(updatedFlightSeat);
    }
    
    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSeatAvailability(
            @PathVariable Long id,
            @RequestParam boolean isAvailable,
            @RequestParam boolean isReserved) {
        flightSeatService.updateSeatAvailability(id, isAvailable, isReserved);
        return ResponseEntity.ok(new MessageResponse("Seat availability updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFlightSeat(@PathVariable Long id) {
        flightSeatService.deleteFlightSeat(id);
        return ResponseEntity.ok(new MessageResponse("Flight seat deleted successfully"));
    }
}