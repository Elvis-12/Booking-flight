package Fligh.Booking.service;

import Fligh.Booking.model.Flight;
import Fligh.Booking.model.Schedule;
import Fligh.Booking.model.enums.ScheduleStatus;
import Fligh.Booking.repository.FlightRepository;
import Fligh.Booking.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private FlightRepository flightRepository;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));
    }

    public List<Schedule> getSchedulesByFlight(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
        return scheduleRepository.findByFlight(flight);
    }

    public List<Schedule> getSchedulesByStatus(ScheduleStatus status) {
        return scheduleRepository.findByStatus(status.toString());
    }

    public List<Schedule> getSchedulesInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleRepository.findSchedulesInTimeRange(startTime, endTime);
    }

    public List<Schedule> getSchedulesByRoute(String origin, String destination, LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleRepository.findSchedulesByRoute(origin, destination, startTime, endTime);
    }

    @Transactional
    public Schedule createSchedule(Schedule schedule) {
        // Validate flight exists
        Flight flight = flightRepository.findById(schedule.getFlight().getId())
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + schedule.getFlight().getId()));
        
        schedule.setFlight(flight);
        
        // Set default status if not provided
        if (schedule.getStatus() == null) {
            schedule.setStatus(ScheduleStatus.SCHEDULED);
        }
        
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public Schedule updateSchedule(Long id, Schedule scheduleDetails) {
        Schedule schedule = getScheduleById(id);
        
        schedule.setDepartureTime(scheduleDetails.getDepartureTime());
        schedule.setArrivalTime(scheduleDetails.getArrivalTime());
        
        if (scheduleDetails.getStatus() != null) {
            schedule.setStatus(scheduleDetails.getStatus());
        }
        
        if (scheduleDetails.getFlight() != null && scheduleDetails.getFlight().getId() != null) {
            Flight flight = flightRepository.findById(scheduleDetails.getFlight().getId())
                    .orElseThrow(() -> new RuntimeException("Flight not found with id: " + scheduleDetails.getFlight().getId()));
            schedule.setFlight(flight);
        }
        
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public Schedule updateScheduleStatus(Long id, ScheduleStatus status) {
        Schedule schedule = getScheduleById(id);
        schedule.setStatus(status);
        return scheduleRepository.save(schedule);
    }
    
    @Transactional
    public Schedule delaySchedule(Long id, LocalDateTime newDepartureTime, LocalDateTime newArrivalTime) {
        Schedule schedule = getScheduleById(id);
        
        // Update times
        schedule.setDepartureTime(newDepartureTime);
        schedule.setArrivalTime(newArrivalTime);
        
        // Update status
        schedule.setStatus(ScheduleStatus.DELAYED);
        
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = getScheduleById(id);
        scheduleRepository.delete(schedule);
    }
    
    @Transactional
    public List<Schedule> createBulkSchedules(List<Schedule> schedules) {
        // Validate and set references for each schedule
        for (Schedule schedule : schedules) {
            Flight flight = flightRepository.findById(schedule.getFlight().getId())
                    .orElseThrow(() -> new RuntimeException("Flight not found with id: " + schedule.getFlight().getId()));
            
            schedule.setFlight(flight);
            
            // Set default status if not provided
            if (schedule.getStatus() == null) {
                schedule.setStatus(ScheduleStatus.SCHEDULED);
            }
        }
        
        return scheduleRepository.saveAll(schedules);
    }
    
    @Transactional
    public void updateAllSchedulesForFlight(Long flightId, ScheduleStatus status) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
                
        List<Schedule> schedules = scheduleRepository.findByFlight(flight);
        
        for (Schedule schedule : schedules) {
            schedule.setStatus(status);
        }
        
        scheduleRepository.saveAll(schedules);
    }
}