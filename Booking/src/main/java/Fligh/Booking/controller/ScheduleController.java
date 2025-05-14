package Fligh.Booking.controller;

import Fligh.Booking.model.Schedule;
import Fligh.Booking.model.enums.ScheduleStatus;
import Fligh.Booking.dto.response.MessageResponse;
import Fligh.Booking.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Long id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(schedule);
    }
    
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<Schedule>> getSchedulesByFlight(@PathVariable Long flightId) {
        List<Schedule> schedules = scheduleService.getSchedulesByFlight(flightId);
        return ResponseEntity.ok(schedules);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Schedule>> getSchedulesByStatus(@PathVariable ScheduleStatus status) {
        List<Schedule> schedules = scheduleService.getSchedulesByStatus(status);
        return ResponseEntity.ok(schedules);
    }
    
    @GetMapping("/range")
    public ResponseEntity<List<Schedule>> getSchedulesInTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<Schedule> schedules = scheduleService.getSchedulesInTimeRange(startTime, endTime);
        return ResponseEntity.ok(schedules);
    }
    
    @GetMapping("/route")
    public ResponseEntity<List<Schedule>> getSchedulesByRoute(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<Schedule> schedules = scheduleService.getSchedulesByRoute(origin, destination, startTime, endTime);
        return ResponseEntity.ok(schedules);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSchedule(@RequestBody Schedule schedule) {
        try {
            Schedule savedSchedule = scheduleService.createSchedule(schedule);
            return ResponseEntity.ok(savedSchedule);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createBulkSchedules(@RequestBody List<Schedule> schedules) {
        try {
            List<Schedule> savedSchedules = scheduleService.createBulkSchedules(schedules);
            return ResponseEntity.ok(savedSchedules);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody Schedule scheduleDetails) {
        try {
            Schedule updatedSchedule = scheduleService.updateSchedule(id, scheduleDetails);
            return ResponseEntity.ok(updatedSchedule);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Schedule> updateScheduleStatus(
            @PathVariable Long id,
            @RequestParam ScheduleStatus status) {
        Schedule updatedSchedule = scheduleService.updateScheduleStatus(id, status);
        return ResponseEntity.ok(updatedSchedule);
    }
    
    @PostMapping("/{id}/delay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delaySchedule(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDepartureTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newArrivalTime) {
        try {
            Schedule updatedSchedule = scheduleService.delaySchedule(id, newDepartureTime, newArrivalTime);
            return ResponseEntity.ok(updatedSchedule);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/flight/{flightId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAllSchedulesForFlight(
            @PathVariable Long flightId,
            @RequestParam ScheduleStatus status) {
        scheduleService.updateAllSchedulesForFlight(flightId, status);
        return ResponseEntity.ok(new MessageResponse("All schedules for the flight updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(new MessageResponse("Schedule deleted successfully"));
    }
}