package Fligh.Booking.service;

import Fligh.Booking.model.Airport;
import Fligh.Booking.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AirportService {
    @Autowired
    private AirportRepository airportRepository;

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public Airport getAirportById(Long id) {
        return airportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airport not found with id: " + id));
    }

    public Optional<Airport> getAirportByCode(String code) {
        return airportRepository.findByCode(code);
    }

    public List<Airport> getAirportsByCountry(String country) {
        return airportRepository.findByCountry(country);
    }

    public List<Airport> getAirportsByCity(String city) {
        return airportRepository.findByCity(city);
    }

    public boolean existsByCode(String code) {
        return airportRepository.existsByCode(code);
    }

    @Transactional
    public Airport createAirport(Airport airport) {
        if (existsByCode(airport.getCode())) {
            throw new RuntimeException("Airport code already exists: " + airport.getCode());
        }
        return airportRepository.save(airport);
    }

    @Transactional
    public Airport updateAirport(Long id, Airport airportDetails) {
        Airport airport = getAirportById(id);
        
        // If code is being changed, check if the new code already exists
        if (!airport.getCode().equals(airportDetails.getCode()) && existsByCode(airportDetails.getCode())) {
            throw new RuntimeException("Airport code already exists: " + airportDetails.getCode());
        }
        
        airport.setCode(airportDetails.getCode());
        airport.setName(airportDetails.getName());
        airport.setCity(airportDetails.getCity());
        airport.setCountry(airportDetails.getCountry());
        airport.setLatitude(airportDetails.getLatitude());
        airport.setLongitude(airportDetails.getLongitude());
        airport.setTimezone(airportDetails.getTimezone());
        
        return airportRepository.save(airport);
    }

    @Transactional
    public void deleteAirport(Long id) {
        Airport airport = getAirportById(id);
        airportRepository.delete(airport);
    }
}