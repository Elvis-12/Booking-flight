package Fligh.Booking.service;

import Fligh.Booking.model.Airline;
import Fligh.Booking.repository.AirlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AirlineService {
    @Autowired
    private AirlineRepository airlineRepository;

    public List<Airline> getAllAirlines() {
        return airlineRepository.findAll();
    }

    public Airline getAirlineById(Long id) {
        return airlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airline not found with id: " + id));
    }

    public Optional<Airline> getAirlineByCode(String code) {
        return airlineRepository.findByCode(code);
    }

    public boolean existsByCode(String code) {
        return airlineRepository.existsByCode(code);
    }

    @Transactional
    public Airline createAirline(Airline airline) {
        if (existsByCode(airline.getCode())) {
            throw new RuntimeException("Airline code already exists: " + airline.getCode());
        }
        return airlineRepository.save(airline);
    }

    @Transactional
    public Airline updateAirline(Long id, Airline airlineDetails) {
        Airline airline = getAirlineById(id);
        
        // If code is being changed, check if the new code already exists
        if (!airline.getCode().equals(airlineDetails.getCode()) && existsByCode(airlineDetails.getCode())) {
            throw new RuntimeException("Airline code already exists: " + airlineDetails.getCode());
        }
        
        airline.setName(airlineDetails.getName());
        airline.setCode(airlineDetails.getCode());
        airline.setCountry(airlineDetails.getCountry());
        
        return airlineRepository.save(airline);
    }

    @Transactional
    public void deleteAirline(Long id) {
        Airline airline = getAirlineById(id);
        airlineRepository.delete(airline);
    }
}