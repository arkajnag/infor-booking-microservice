package io.CarbookingService.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.CarbookingService.exceptionhandler.DataNotAllowedException;
import io.CarbookingService.exceptionhandler.DataNotFoundException;
import io.CarbookingService.exceptionhandler.NullFormatException;
import io.CarbookingService.model.CarBooking;
import io.CarbookingService.model.CarBookingDTO;
import io.CarbookingService.service.BookingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Car Booking API Service", consumes = "application/json",produces = "application/json")
@RestController(value = "Booking Controller")
@RequestMapping(value="/rest/booking")
public class BookingController {
	
	@Autowired
	private BookingService bookingService;
	
	@ApiOperation(value = "Create a new Car Booking", httpMethod = "POST", consumes = "application/json",produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200,message = "Successful"),
            @ApiResponse(code = 400,message = "BAD Request"),
            @ApiResponse(code = 401,message = "Unauthorized"),
            @ApiResponse(code = 404,message = "Page Not Found"),
            @ApiResponse(code = 500,message = "Server Error")
    })
	@PostMapping(value="/new/booking",consumes = "application/json",produces = "application/json")
	public CompletableFuture<ResponseEntity<CarBooking>> registerNewCarBooking(@RequestBody CarBooking carBookingObj) throws DataNotFoundException, NullFormatException, DataNotAllowedException {
		return bookingService.createNewBooking(carBookingObj).thenApplyAsync(carBookObj-> ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(carBookObj));
	}
	
	@ApiOperation(value = "GET All Booking Information", httpMethod = "GET", consumes = "application/json",produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200,message = "Successful"),
            @ApiResponse(code = 400,message = "BAD Request"),
            @ApiResponse(code = 401,message = "Unauthorized"),
            @ApiResponse(code = 404,message = "Page Not Found"),
            @ApiResponse(code = 500,message = "Server Error")
    })
	@GetMapping(value="/all",produces = "application/json")
    public CompletableFuture<ResponseEntity<CarBookingDTO>> getAllCarBookingInformation() throws DataNotFoundException {
        return bookingService.getAllCarBookingInformation().thenApply(carBookingObj-> ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(carBookingObj));
    }

    @ApiOperation(value = "GET All Booking Information in between Dates", httpMethod = "GET", consumes = "application/json",produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200,message = "Successful"),
            @ApiResponse(code = 400,message = "BAD Request"),
            @ApiResponse(code = 401,message = "Unauthorized"),
            @ApiResponse(code = 404,message = "Page Not Found"),
            @ApiResponse(code = 500,message = "Server Error")
    })
    @GetMapping(value="/search",produces = "application/json")
    public CompletableFuture<ResponseEntity<CarBookingDTO>> searchBookingsByDate(@RequestParam(value = "bookingStartDate", required = true)String bookingStartDate,
            @RequestParam(value = "bookingEndDate", required = true)String bookingEndDate) throws DataNotFoundException, NullFormatException, DataNotAllowedException {
    	return bookingService.searchBookingsByDate(bookingStartDate,bookingEndDate)
    			.thenApply(carBookingDTO->ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(carBookingDTO));
    }
    
    @ApiOperation(value = "GET Total Payments in between dates for Car Bookings", httpMethod = "GET", consumes = "application/json",produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200,message = "Successful"),
            @ApiResponse(code = 400,message = "BAD Request"),
            @ApiResponse(code = 401,message = "Unauthorized"),
            @ApiResponse(code = 404,message = "Page Not Found"),
            @ApiResponse(code = 500,message = "Server Error")
    })
    @GetMapping(value="/total/payment",produces = "application/json")
    public ResponseEntity<String> getTotalBookingPaymentsByDate(@RequestParam(value = "bookingStartDate", required = true)String bookingStartDate,
            @RequestParam(value = "bookingEndDate", required = true)String bookingEndDate) throws DataNotFoundException, NullFormatException, DataNotAllowedException, ExecutionException, InterruptedException {
    	Double totalAmountInResponse=bookingService.getTotalBookingPaymentsByDate(bookingStartDate,bookingEndDate).get();
    	return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("Total Payments in between :"+bookingStartDate+" and "+bookingEndDate+" is:"+totalAmountInResponse);
    }
    
    @ApiOperation(value = "GET Total Number of Car Booking Each Hours within given Dates", httpMethod = "GET", consumes = "application/json",produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200,message = "Successful"),
            @ApiResponse(code = 400,message = "BAD Request"),
            @ApiResponse(code = 401,message = "Unauthorized"),
            @ApiResponse(code = 404,message = "Page Not Found"),
            @ApiResponse(code = 500,message = "Server Error")
    })
    @GetMapping(value="/booked/perHour",produces = "application/json")
    public ResponseEntity<Map<String,Long>> getNumberOfCarsBookedPerHourWithinDates(@RequestParam(value = "bookingStartDate", required = true)String bookingStartDate,
            @RequestParam(value = "bookingEndDate", required = true)String bookingEndDate) throws DataNotFoundException, NullFormatException, DataNotAllowedException, ExecutionException, InterruptedException {
    	Map<String, Long> countOfBookings=bookingService.getNumberOfCarsBookedPerHourWithinDates(bookingStartDate,bookingEndDate).get();
    	return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(countOfBookings);
    }
    
    @GetMapping(value="/all/reports/{type}", produces = "application/json")
    public String getBookingsReport(@PathVariable(value = "type", required = true)String type) throws DataNotFoundException{
        Object response=bookingService.exportCarBookingsReport(type);
        return (response==null)?"Car Booking Report file is created!!":"Error in generating the Report";
    }
}
