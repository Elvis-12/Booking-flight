package Fligh.Booking.service;

import Fligh.Booking.model.Flight;
import Fligh.Booking.model.FlightSeat;
import Fligh.Booking.model.Seat;
import Fligh.Booking.model.enums.SeatClass;
import Fligh.Booking.repository.FlightRepository;
import Fligh.Booking.repository.FlightSeatRepository;
import Fligh.Booking.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightSeatService {
    @Autowired
    private FlightSeatRepository flightSeatRepository;
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private SeatRepository seatRepository;

    public List<FlightSeat> getAllFlightSeats() {
        return flightSeatRepository.findAll();
    }

    public FlightSeat getFlightSeatById(Long id) {
        return flightSeatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight seat not found with id: " + id));
    }

    public List<FlightSeat> getFlightSeatsByFlight(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
        return flightSeatRepository.findByFlight(flight);
    }

    public List<FlightSeat> getAvailableFlightSeatsByFlight(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
        return flightSeatRepository.findByFlightAndIsAvailable(flight, true);
    }

    public List<FlightSeat> getFlightSeatsByFlightAndClass(Long flightId, SeatClass seatClass) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
        return flightSeatRepository.findByFlightAndSeat_SeatClass(flight, seatClass.toString());
    }

    public List<FlightSeat> getFlightSeatsByFlightAndMaxPrice(Long flightId, Double maxPrice) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
        return flightSeatRepository.findByFlightAndPriceLessThanEqual(flight, maxPrice);
    }

    @Transactional
    public FlightSeat createFlightSeat(FlightSeat flightSeat) {
        Flight flight = flightRepository.findById(flightSeat.getFlight().getId())
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightSeat.getFlight().getId()));
        
        Seat seat = seatRepository.findById(flightSeat.getSeat().getId())
                .orElseThrow(() -> new RuntimeException("Seat not found with id: " + flightSeat.getSeat().getId()));
        
        // Check if this flight-seat combination already exists
        List<FlightSeat> existingFlightSeats = flightSeatRepository.findByFlightAndSeat(flight, seat);
        if (!existingFlightSeats.isEmpty()) {
            throw new RuntimeException("This seat is already assigned to this flight!");
        }
        
        flightSeat.setFlight(flight);
        flightSeat.setSeat(seat);
        
        // Default values if not set
        if (flightSeat.getIsAvailable() == null) {
            flightSeat.setIsAvailable(true);
        }
        if (flightSeat.getIsReserved() == null) {
            flightSeat.setIsReserved(false);
        }
        
        // Set default price from seat if not provided
        if (flightSeat.getPrice() == null) {
            flightSeat.setPrice(seat.getPrice());
        }
        
        return flightSeatRepository.save(flightSeat);
    }

    @Transactional
    public FlightSeat updateFlightSeat(Long id, FlightSeat flightSeatDetails) {
        FlightSeat flightSeat = getFlightSeatById(id);
        
        flightSeat.setPrice(flightSeatDetails.getPrice());
        flightSeat.setIsAvailable(flightSeatDetails.getIsAvailable());
        flightSeat.setIsReserved(flightSeatDetails.getIsReserved());
        
        return flightSeatRepository.save(flightSeat);
    }

    @Transactional
    public void deleteFlightSeat(Long id) {
        FlightSeat flightSeat = getFlightSeatById(id);
        flightSeatRepository.delete(flightSeat);
    }
    
    @Transactional
    public List<FlightSeat> createFlightSeatsForFlight(Long flightId, List<Long> seatIds) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
                
        List<FlightSeat> createdSeats = new ArrayList<>();
        
        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found with id: " + seatId));
                    
            // Check if this flight-seat combination already exists
            List<FlightSeat> existingFlightSeats = flightSeatRepository.findByFlightAndSeat(flight, seat);
            if (existingFlightSeats.isEmpty()) {
                FlightSeat flightSeat = new FlightSeat();
                flightSeat.setFlight(flight);
                flightSeat.setSeat(seat);
                flightSeat.setIsAvailable(true);
                flightSeat.setIsReserved(false);
                flightSeat.setPrice(seat.getPrice());
                
                createdSeats.add(flightSeatRepository.save(flightSeat));
            }
        }
        
        return createdSeats;
    }
    
    @Transactional
    public void updateSeatAvailability(Long flightSeatId, boolean isAvailable, boolean isReserved) {
        FlightSeat flightSeat = getFlightSeatById(flightSeatId);
        flightSeat.setIsAvailable(isAvailable);
        flightSeat.setIsReserved(isReserved);
        flightSeatRepository.save(flightSeat);
    }
}