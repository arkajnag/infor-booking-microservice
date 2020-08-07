package io.CarbookingService.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import io.CarbookingService.model.CarBooking;

@Repository
public interface BookingRepository extends JpaRepository<CarBooking, Integer> {
	
	@Query(value="Select * from TBL_CARBOOKINGS CB where CB.car_Plate_Number=:carPlateNumber",nativeQuery = true)
	List<CarBooking> getListOfBooking(@Param("carPlateNumber")Integer carPlateNumber);

	@Query(value="Select * from TBL_CARBOOKINGS CB where CB.BOOKING_START_DATE>=:requestedBookingStartDate and CB.BOOKING_END_DATE<=:requestedBookingEndDate",nativeQuery = true)
	List<CarBooking> findByCarBookingsByBookingDates(@Param("requestedBookingStartDate")LocalDateTime requestedBookingStartDate,
			@Param("requestedBookingEndDate")LocalDateTime requestedBookingEndDate);

	@Query(value="Select * from TBL_CARBOOKINGS CB where CB.BOOKING_TIME>=:searchBookingTimeStart and CB.BOOKING_TIME<=:searchBookingTimeEnd",nativeQuery = true)
	List<CarBooking> findByCarBookingsByBookingTime(@Param("searchBookingTimeStart")LocalDateTime searchBookingTimeStart,
			@Param("searchBookingTimeEnd")LocalDateTime searchBookingTimeEnd);

	
}
