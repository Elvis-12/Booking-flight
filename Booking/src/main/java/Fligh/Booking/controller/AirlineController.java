package Fligh.Booking.controller;

import Fligh.Booking.model.Airline;
import Fligh.Booking.dto.response.MessageResponse;
import Fligh.Booking.service.AirlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/airlines")
public class AirlineController {
    @Autowired
    private AirlineService airlineService;

    @GetMapping
    public ResponseEntity<List<Airline>> getAllAirlines() {
        List<Airline> airlines = airlineService.getAllAirlines();
        return ResponseEntity.ok(airlines);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Airline> getAirlineById(@PathVariable Long id) {
        Airline airline = airlineService.getAirlineById(id);
        return ResponseEntity.ok(airline);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<Airline> getAirlineByCode(@PathVariable String code) {
        Airline airline = airlineService.getAirlineByCode(code)
                .orElseThrow(() -> new RuntimeException("Airline not found with code: " + code));
        return ResponseEntity.ok(airline);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAirline(@RequestBody Airline airline) {
        try {
            Airline savedAirline = airlineService.createAirline(airline);
            return ResponseEntity.ok(savedAirline);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAirline(@PathVariable Long id, @RequestBody Airline airlineDetails) {
        try {
            Airline updatedAirline = airlineService.updateAirline(id, airlineDetails);
            return ResponseEntity.ok(updatedAirline);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAirline(@PathVariable Long id) {
        airlineService.deleteAirline(id);
        return ResponseEntity.ok(new MessageResponse("Airline deleted successfully"));
    }
}