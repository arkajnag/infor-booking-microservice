package io.CarbookingService.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.CarbookingService.exceptionhandler.DataNotAllowedException;
import io.CarbookingService.exceptionhandler.DataNotFoundException;
import io.CarbookingService.exceptionhandler.NullFormatException;
import io.CarbookingService.model.Car;
import io.CarbookingService.model.CarBooking;
import io.CarbookingService.model.CarBookingDTO;
import io.CarbookingService.model.CarDTO;
import io.CarbookingService.model.UserDTO;
import io.CarbookingService.repository.BookingRepository;
import io.CarbookingService.utils.CommonUtils;

@Service
public class BookingService {
	
	private Logger logger=LoggerFactory.getLogger(BookingService.class);
	
	@Value("${BOOKING_PAYLOAD_NULL}")
	private String BOOKING_PAYLOAD_NULL;
	
	@Value("${CAR_USER_PROFILE_NULL}")
	private String CAR_USER_PROFILE_NULL;
	
	@Value("${BOOKING_START_AFTER_END_DATE}")
	private String BOOKING_START_AFTER_END_DATE;
	
	@Value("${BOOKING_START_SAME_END_DATE}")
	private String BOOKING_START_SAME_END_DATE;
	
	@Value("${BOOKING_START_AFTER_AVAILABLE_START}")
	private String BOOKING_START_AFTER_AVAILABLE_START;
	
	@Value("${BOOKING_END_BEOFRE_AVAILABLE_START}")
	private String BOOKING_END_BEOFRE_AVAILABLE_START;
	
	@Value("${BOOKING_START_AFTER_AVAILABLE_END}")
	private String BOOKING_START_AFTER_AVAILABLE_END;
	
	@Value("${BOOKING_END_AFTER_AVAILABLE_END}")
	private String BOOKING_END_AFTER_AVAILABLE_END;
	
	@Value("${BOOKING_NOT_ALLOWED}")
	private String BOOKING_NOT_ALLOWED;
	
	@Value("${NO_BOOKING_INFO}")
	private String NO_BOOKING_INFO;
	
	@Value("${BOOKING_START_END_NULL}")
	private String BOOKING_START_END_NULL;
	
	@Value("${NO_BOOKING_WITHIN_DATE}")
	private String NO_BOOKING_WITHIN_DATE;
	
	@Value("${DATE_FORMAT_NOT_ALLOWED}")
	private String DATE_FORMAT_NOT_ALLOWED;
	
	@Autowired
	private ProxyService proxyService;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	public CompletableFuture<CarBooking> createNewBooking(CarBooking carBookingObj) throws DataNotFoundException, NullFormatException, DataNotAllowedException {
		logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has started in Thread:" + Thread.currentThread().getName());
		try {
			if(carBookingObj==null)
				throw new NullFormatException("BOOKING_PAYLOAD_NULL");
			LocalDateTime requestedBookingStartDate= carBookingObj.getBookingStartDate();
            LocalDateTime requestedBookingEndDate= carBookingObj.getBookingEndDate();
            LocalDateTime availableStartDate=null;
            LocalDateTime availableEndDate=null;
            List<CarBooking> listOfPreviousBookings=new ArrayList<>();
            Car requestCarObj;
            Integer userid=carBookingObj.getUserId();
            Integer carPlateNumber=carBookingObj.getCarPlateNumber();
            if (carPlateNumber==null || userid==null) {
                logger.error(CAR_USER_PROFILE_NULL);
                throw new NullFormatException(CAR_USER_PROFILE_NULL);
            }
			//Verifying User Data present or not Using RestTemplate and calling User MicroService
			UserDTO userDTOResponseObj = proxyService.getUserInfo();
			if(!userDTOResponseObj.getUsers().parallelStream().anyMatch(user-> user.getUserid().intValue()==userid.intValue()))
				throw new DataNotFoundException("Requested User ID:"+userid+" doesn't exists. Please review your request!!");
			
			//Verifying User Data present or not Using RestTemplate and calling User MicroService
			CarDTO carDTOResponseObj = proxyService.getCarInfo();
			if(!carDTOResponseObj.getCars().stream().anyMatch(car->car.getCarPlateNumber().intValue()==carPlateNumber.intValue()))
				throw new DataNotFoundException("Requested Car with Plate Number:"+carPlateNumber+" doesn't exists. Please review your request!!");
			requestCarObj=carDTOResponseObj.getCars().parallelStream().filter(car->car.getCarPlateNumber().intValue()==carBookingObj.getCarPlateNumber().intValue()).findAny().get();
			availableStartDate=requestCarObj.getCarAvailableStartDate();
            availableEndDate=requestCarObj.getCarAvailableEndDate();
            if(CommonUtils.isDateAfter.apply(requestedBookingStartDate,requestedBookingEndDate)) {
                logger.error(BOOKING_START_AFTER_END_DATE);
                throw new DataNotAllowedException(BOOKING_START_AFTER_END_DATE);
            }
            if(CommonUtils.isDateEqual.apply(requestedBookingStartDate,requestedBookingEndDate)) {
                logger.error(BOOKING_START_SAME_END_DATE);
                throw new DataNotAllowedException(BOOKING_START_SAME_END_DATE);
            }
            if(CommonUtils.isDateBefore.apply(requestedBookingStartDate,availableStartDate)) {
                logger.error(BOOKING_START_AFTER_AVAILABLE_START);
                throw new DataNotAllowedException(BOOKING_START_AFTER_AVAILABLE_START);
            }
            if(CommonUtils.isDateBefore.apply(requestedBookingEndDate,availableStartDate)) {
                logger.error(BOOKING_END_BEOFRE_AVAILABLE_START);
                throw new DataNotAllowedException(BOOKING_END_BEOFRE_AVAILABLE_START);
            }
            if(CommonUtils.isDateAfter.apply(requestedBookingStartDate,availableEndDate)) {
                logger.error(BOOKING_START_AFTER_AVAILABLE_END);
                throw new DataNotAllowedException(BOOKING_START_AFTER_AVAILABLE_END);
            }
            if(CommonUtils.isDateAfter.apply(requestedBookingEndDate,availableEndDate)) {
                logger.error(BOOKING_END_AFTER_AVAILABLE_END);
                throw new DataNotAllowedException(BOOKING_END_AFTER_AVAILABLE_END);
            }
            listOfPreviousBookings=bookingRepository.getListOfBooking(carPlateNumber);
            long counting=listOfPreviousBookings.stream().filter(previousBookingObj->CommonUtils
                    .isDateOverlap(CommonUtils.getFormattedDateOfSpecificLocalDatetime.apply(carBookingObj.getBookingStartDate(),"yyyy-MM-dd'T'HH:mm:ss"),
                            CommonUtils.getFormattedDateOfSpecificLocalDatetime.apply(carBookingObj.getBookingEndDate(),"yyyy-MM-dd'T'HH:mm:ss"),
                            CommonUtils.getFormattedDateOfSpecificLocalDatetime.apply(previousBookingObj.getBookingStartDate(),"yyyy-MM-dd'T'HH:mm:ss"),
                            CommonUtils.getFormattedDateOfSpecificLocalDatetime.apply(previousBookingObj.getBookingEndDate(),"yyyy-MM-dd'T'HH:mm:ss")
                    )).count();
            if(counting!=0) {
                logger.error(BOOKING_NOT_ALLOWED);
                throw new DataNotAllowedException(BOOKING_NOT_ALLOWED);
            }
            carBookingObj.setBookingAmount(CommonUtils.getHours.apply(requestedBookingStartDate,requestedBookingEndDate)*requestCarObj.getCarRentalPricePerHour());
            return CompletableFuture.completedFuture(bookingRepository.save(carBookingObj));
		}finally {
			logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has ended in Thread:" + Thread.currentThread().getName());
		}
	}

	
	public CompletableFuture<CarBookingDTO> getAllCarBookingInformation() throws DataNotFoundException {
		logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has started in Thread:" + Thread.currentThread().getName());
        try{
            List<CarBooking> allBookings=bookingRepository.findAll();
            if(allBookings.size()==0) {
                logger.error(NO_BOOKING_INFO);
                throw new DataNotFoundException(NO_BOOKING_INFO);
            }
            return CompletableFuture.completedFuture(new CarBookingDTO(allBookings));
        }finally {
            logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has ended in Thread:" + Thread.currentThread().getName());
        }
	}

	public CompletableFuture<CarBookingDTO> searchBookingsByDate(String bookingStartDate,String bookingEndDate) 
			throws NullFormatException, DataNotAllowedException, DataNotFoundException {
		logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has started in Thread:" + Thread.currentThread().getName());
        try{
            LocalDateTime requestedBookingStartDate=null;
            LocalDateTime requestedBookingEndDate=null;
            if(bookingStartDate==null||bookingEndDate==null) {
                logger.error(BOOKING_START_END_NULL);
                throw new NullFormatException(BOOKING_START_END_NULL);
            }
            if(CommonUtils.getLocalDateTimeFromDateString.apply(bookingStartDate) instanceof Exception) {
                logger.error(DATE_FORMAT_NOT_ALLOWED);
                throw new DataNotAllowedException(DATE_FORMAT_NOT_ALLOWED);
            }
            else
                requestedBookingStartDate= (LocalDateTime) CommonUtils.getLocalDateTimeFromDateString.apply(bookingStartDate);
            if(CommonUtils.getLocalDateTimeFromDateString.apply(bookingEndDate) instanceof Exception) {
                logger.error(DATE_FORMAT_NOT_ALLOWED);
                throw new DataNotAllowedException(DATE_FORMAT_NOT_ALLOWED);
            }
            else
                requestedBookingEndDate= (LocalDateTime) CommonUtils.getLocalDateTimeFromDateString.apply(bookingEndDate);
            if(CommonUtils.isDateAfter.apply(requestedBookingStartDate,requestedBookingEndDate)) {
                logger.error(BOOKING_START_AFTER_END_DATE);
                throw new DataNotAllowedException(BOOKING_START_AFTER_END_DATE);
            }
            if(CommonUtils.isDateEqual.apply(requestedBookingStartDate,requestedBookingEndDate)) {
                logger.error(BOOKING_START_SAME_END_DATE);
                throw new DataNotAllowedException(BOOKING_START_SAME_END_DATE);
            }
            List<CarBooking> allBookingsResponse=bookingRepository.findByCarBookingsByBookingDates(requestedBookingStartDate,requestedBookingEndDate);
            if(allBookingsResponse.size()==0) {
                logger.error(NO_BOOKING_WITHIN_DATE);
                throw new DataNotFoundException(NO_BOOKING_WITHIN_DATE);
            }
            return CompletableFuture.completedFuture(new CarBookingDTO(allBookingsResponse));
        }finally {
            logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has ended in Thread:" + Thread.currentThread().getName());
        }
	}

	public CompletableFuture<Double> getTotalBookingPaymentsByDate(String bookingStartDate,
			String bookingEndDate) throws NullFormatException, DataNotAllowedException, DataNotFoundException {
		logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has started in Thread:" + Thread.currentThread().getName());
        try{
            LocalDateTime requestedBookingStartDate=null;
            LocalDateTime requestedBookingEndDate=null;
            if(bookingStartDate==null||bookingEndDate==null) {
                logger.error(BOOKING_START_END_NULL);
                throw new NullFormatException(BOOKING_START_END_NULL);
            }
            if(CommonUtils.getLocalDateTimeFromDateString.apply(bookingStartDate) instanceof Exception) {
                logger.error(DATE_FORMAT_NOT_ALLOWED);
                throw new DataNotAllowedException(DATE_FORMAT_NOT_ALLOWED);
            }
            else
                requestedBookingStartDate= (LocalDateTime) CommonUtils.getLocalDateTimeFromDateString.apply(bookingStartDate);
            if(CommonUtils.getLocalDateTimeFromDateString.apply(bookingEndDate) instanceof Exception) {
                logger.error(DATE_FORMAT_NOT_ALLOWED);
                throw new DataNotAllowedException(DATE_FORMAT_NOT_ALLOWED);
            }
            else
                requestedBookingEndDate= (LocalDateTime) CommonUtils.getLocalDateTimeFromDateString.apply(bookingEndDate);
            if(CommonUtils.isDateAfter.apply(requestedBookingStartDate,requestedBookingEndDate)) {
                logger.error(BOOKING_START_AFTER_END_DATE);
                throw new DataNotAllowedException(BOOKING_START_AFTER_END_DATE);
            }
            if(CommonUtils.isDateEqual.apply(requestedBookingStartDate,requestedBookingEndDate)) {
                logger.error(BOOKING_START_SAME_END_DATE);
                throw new DataNotAllowedException(BOOKING_START_SAME_END_DATE);
            }
            List<CarBooking> allBookingsResponse=bookingRepository.findByCarBookingsByBookingDates(requestedBookingStartDate,requestedBookingEndDate);
            if(allBookingsResponse.size()==0) {
                logger.error(NO_BOOKING_WITHIN_DATE);
                throw new DataNotFoundException(NO_BOOKING_WITHIN_DATE);
            }
            return CompletableFuture.completedFuture(allBookingsResponse.parallelStream().mapToDouble(CarBooking::getBookingAmount).sum());
        }finally {
            logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has ended in Thread:" + Thread.currentThread().getName());
        }
	}

	
	public CompletableFuture<Map<String, Long>> getNumberOfCarsBookedPerHourWithinDates(
			String bookingStartDate, String bookingEndDate) throws NullFormatException, DataNotAllowedException, DataNotFoundException {
		logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has started in Thread:" + Thread.currentThread().getName());
        try{
            long bookingCounter = 0;
            Map<String,Long> bookingCounterDetails=new HashMap<>();
            LocalDateTime requestedBookingStartDate=null;
            LocalDateTime requestedBookingEndDate=null;
            if(bookingStartDate==null||bookingEndDate==null) {
                logger.error(BOOKING_START_END_NULL);
                throw new NullFormatException(BOOKING_START_END_NULL);
            }
            if(CommonUtils.getLocalDateTimeFromDateString.apply(bookingStartDate) instanceof Exception) {
                logger.error(DATE_FORMAT_NOT_ALLOWED);
                throw new DataNotAllowedException(DATE_FORMAT_NOT_ALLOWED);
            }
            else
                requestedBookingStartDate= (LocalDateTime) CommonUtils.getLocalDateTimeFromDateString.apply(bookingStartDate);
            if(CommonUtils.getLocalDateTimeFromDateString.apply(bookingEndDate) instanceof Exception) {
                logger.error(DATE_FORMAT_NOT_ALLOWED);
                throw new DataNotAllowedException(DATE_FORMAT_NOT_ALLOWED);
            }
            else
                requestedBookingEndDate= (LocalDateTime) CommonUtils.getLocalDateTimeFromDateString.apply(bookingEndDate);
            if(CommonUtils.isDateAfter.apply(requestedBookingStartDate,requestedBookingEndDate)) {
                logger.error(BOOKING_START_AFTER_END_DATE);
                throw new DataNotAllowedException(BOOKING_START_AFTER_END_DATE);
            }
            if(CommonUtils.isDateEqual.apply(requestedBookingStartDate,requestedBookingEndDate)) {
                logger.error(BOOKING_START_SAME_END_DATE);
                throw new DataNotAllowedException(BOOKING_START_SAME_END_DATE);
            }
            List<CarBooking> allBookingsResponse=bookingRepository.findByCarBookingsByBookingTime(requestedBookingStartDate,requestedBookingEndDate);
            if(allBookingsResponse.size()==0) {
                logger.error(NO_BOOKING_WITHIN_DATE);
                throw new DataNotFoundException(NO_BOOKING_WITHIN_DATE);
            }
            for (LocalDateTime date = requestedBookingStartDate; date.isBefore(requestedBookingEndDate); date = date.plusMinutes(59)) {
                LocalDateTime startDateTimeHours = date;
                LocalDateTime endDateTimeHours = startDateTimeHours.plusMinutes(60);
                for(CarBooking eachCarBooking:allBookingsResponse) {
                    LocalDateTime bookedDateTime = eachCarBooking.getBookingTime();
                    if (startDateTimeHours.compareTo(bookedDateTime) * bookedDateTime.compareTo(endDateTimeHours) >= 0) {
                        bookingCounter = bookingCounter + 1;
                        bookingCounterDetails.put("Number of Bookings for Cars in between " + startDateTimeHours + " and " + endDateTimeHours, bookingCounter);
                    }
                }
                bookingCounter=0;
            }
            return CompletableFuture.completedFuture(bookingCounterDetails);
        }finally {
            logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has ended in Thread:" + Thread.currentThread().getName());
        }
    }
	
	
	public Object exportCarBookingsReport(String reportFormat) throws DataNotFoundException {
        logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has started in Thread:" + Thread.currentThread().getName());
        try{
            List<CarBooking> allbookingsInfo = bookingRepository.findAll();
            if(allbookingsInfo.size()==0) {
                logger.error(NO_BOOKING_INFO);
                throw new DataNotFoundException(NO_BOOKING_INFO);
            }
            String reportPath=System.getProperty("user.dir")+File.separator+"Reports"+ File.separator+"Car Bookings"+File.separator+CommonUtils.getFormattedCurrentDateTimeString.apply("yyyy-MM-dd-HH-mm-ss");
            String jasperReportTemplateSource=System.getProperty("user.dir")+"/src/main/resources/carBookings.jrxml";
            return CommonUtils.generateReport(jasperReportTemplateSource,reportFormat,reportPath,allbookingsInfo,"Car Bookings");
        }finally {
            logger.info("Method:" + Thread.currentThread().getStackTrace()[2].getMethodName() + " has ended in Thread:" + Thread.currentThread().getName());
        }
    }

}
