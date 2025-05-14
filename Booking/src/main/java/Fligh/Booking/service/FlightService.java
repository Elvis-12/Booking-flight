package Fligh.Booking.service;

import Fligh.Booking.model.Airline;
import Fligh.Booking.model.Airport;
import Fligh.Booking.model.Flight;
import Fligh.Booking.model.enums.FlightStatus;
import Fligh.Booking.repository.AirlineRepository;
import Fligh.Booking.repository.AirportRepository;
import Fligh.Booking.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private AirlineRepository airlineRepository;
    
    @Autowired
    private AirportRepository airportRepository;

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));
    }
    
    public Flight getFlightByFlightNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new RuntimeException("Flight not found with flight number: " + flightNumber));
    }
    
    public List<Flight> getFlightsByAirline(Long airlineId) {
        Airline airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new RuntimeException("Airline not found with id: " + airlineId));
        return flightRepository.findByAirline(airline);
    }
    
    public List<Flight> searchFlights(Long originId, Long destinationId, LocalDateTime startDate, LocalDateTime endDate) {
        return flightRepository.findFlights(originId, destinationId, startDate, endDate);
    }
    
    public List<Flight> searchFlightsByAirportCodes(String originCode, String destinationCode, LocalDateTime startDate, LocalDateTime endDate) {
        Airport origin = airportRepository.findByCode(originCode)
                .orElseThrow(() -> new RuntimeException("Airport not found with code: " + originCode));
        
        Airport destination = airportRepository.findByCode(destinationCode)
                .orElseThrow(() -> new RuntimeException("Airport not found with code: " + destinationCode));
        
        return searchFlights(origin.getId(), destination.getId(), startDate, endDate);
    }
    
    public List<Flight> getFlightsByStatus(FlightStatus status) {
        return flightRepository.findByStatus(status.toString());
    }
    
    @Transactional
    public Flight createFlight(Flight flight) {
        Airline airline = airlineRepository.findById(flight.getAirline().getId())
                .orElseThrow(() -> new RuntimeException("Airline not found with id: " + flight.getAirline().getId()));
        
        Airport origin = airportRepository.findById(flight.getOriginAirport().getId())
                .orElseThrow(() -> new RuntimeException("Origin airport not found with id: " + flight.getOriginAirport().getId()));
        
        Airport destination = airportRepository.findById(flight.getDestinationAirport().getId())
                .orElseThrow(() -> new RuntimeException("Destination airport not found with id: " + flight.getDestinationAirport().getId()));
        
        flight.setAirline(airline);
        flight.setOriginAirport(origin);
        flight.setDestinationAirport(destination);
        
        if (flight.getStatus() == null) {
            flight.setStatus(FlightStatus.SCHEDULED);
        }
        
        return flightRepository.save(flight);
    }
    
    @Transactional
    public Flight updateFlight(Long id, Flight flightDetails) {
        Flight flight = getFlightById(id);
        
        flight.setFlightNumber(flightDetails.getFlightNumber());
        flight.setDepartureDate(flightDetails.getDepartureDate());
        flight.setArrivalDate(flightDetails.getArrivalDate());
        
        if (flightDetails.getStatus() != null) {
            flight.setStatus(flightDetails.getStatus());
        }
        
        if (flightDetails.getAirline() != null && flightDetails.getAirline().getId() != null) {
            Airline airline = airlineRepository.findById(flightDetails.getAirline().getId())
                    .orElseThrow(() -> new RuntimeException("Airline not found with id: " + flightDetails.getAirline().getId()));
            flight.setAirline(airline);
        }
        
        if (flightDetails.getOriginAirport() != null && flightDetails.getOriginAirport().getId() != null) {
            Airport origin = airportRepository.findById(flightDetails.getOriginAirport().getId())
                    .orElseThrow(() -> new RuntimeException("Origin airport not found with id: " + flightDetails.getOriginAirport().getId()));
            flight.setOriginAirport(origin);
        }
        
        if (flightDetails.getDestinationAirport() != null && flightDetails.getDestinationAirport().getId() != null) {
            Airport destination = airportRepository.findById(flightDetails.getDestinationAirport().getId())
                    .orElseThrow(() -> new RuntimeException("Destination airport not found with id: " + flightDetails.getDestinationAirport().getId()));
            flight.setDestinationAirport(destination);
        }
        
        return flightRepository.save(flight);
    }
    
    @Transactional
    public void deleteFlight(Long id) {
        Flight flight = getFlightById(id);
        flightRepository.delete(flight);
    }
    
    @Transactional
    public Flight updateFlightStatus(Long id, FlightStatus status) {
        Flight flight = getFlightById(id);
        flight.setStatus(status);
        return flightRepository.save(flight);
    }
}