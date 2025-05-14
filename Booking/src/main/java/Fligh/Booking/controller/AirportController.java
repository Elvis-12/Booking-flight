package Fligh.Booking.controller;

import Fligh.Booking.model.Airport;
import Fligh.Booking.dto.response.MessageResponse;
import Fligh.Booking.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/airports")
public class AirportController {
    @Autowired
    private AirportService airportService;

    @GetMapping
    public ResponseEntity<List<Airport>> getAllAirports() {
        List<Airport> airports = airportService.getAllAirports();
        return ResponseEntity.ok(airports);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Airport> getAirportById(@PathVariable Long id) {
        Airport airport = airportService.getAirportById(id);
        return ResponseEntity.ok(airport);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<Airport> getAirportByCode(@PathVariable String code) {
        Airport airport = airportService.getAirportByCode(code)
                .orElseThrow(() -> new RuntimeException("Airport not found with code: " + code));
        return ResponseEntity.ok(airport);
    }
    
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Airport>> getAirportsByCountry(@PathVariable String country) {
        List<Airport> airports = airportService.getAirportsByCountry(country);
        return ResponseEntity.ok(airports);
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Airport>> getAirportsByCity(@PathVariable String city) {
        List<Airport> airports = airportService.getAirportsByCity(city);
        return ResponseEntity.ok(airports);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAirport(@RequestBody Airport airport) {
        try {
            Airport savedAirport = airportService.createAirport(airport);
            return ResponseEntity.ok(savedAirport);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAirport(@PathVariable Long id, @RequestBody Airport airportDetails) {
        try {
            Airport updatedAirport = airportService.updateAirport(id, airportDetails);
            return ResponseEntity.ok(updatedAirport);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAirport(@PathVariable Long id) {
        airportService.deleteAirport(id);
        return ResponseEntity.ok(new MessageResponse("Airport deleted successfully"));
    }
}