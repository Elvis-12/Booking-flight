package Fligh.Booking.repository;

import Fligh.Booking.model.Flight;
import Fligh.Booking.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByFlight(Flight flight);
    
    List<Schedule> findByStatus(String status);
    
    @Query("SELECT s FROM Schedule s WHERE s.departureTime BETWEEN :startTime AND :endTime")
    List<Schedule> findSchedulesInTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT s FROM Schedule s JOIN s.flight f WHERE f.originAirport.code = :origin AND f.destinationAirport.code = :destination AND s.departureTime BETWEEN :startTime AND :endTime")
    List<Schedule> findSchedulesByRoute(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}