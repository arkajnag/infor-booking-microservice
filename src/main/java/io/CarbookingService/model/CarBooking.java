package io.CarbookingService.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "tblCarbookings")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(value = "Car Booking Model")
public class CarBooking {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "bookingId", nullable = false, unique = true)
	@ApiModelProperty(value = "Booking ID", notes = "Unique Booking ID", allowableValues = "Alphanumeric", allowEmptyValue = false, required = true)
	private Integer bookingId;
	@Column(name = "carPlateNumber")
	@ApiModelProperty(value = "Car Plate Number", notes = "Unique Reference for Indentifying a Car", allowableValues = "Alphanumeric", allowEmptyValue = false, required = true)
	private Integer carPlateNumber;
	@Column(name = "userId")
	@ApiModelProperty(value = "User ID", notes = "Unique Reference for Indentifying a User", allowableValues = "Alphanumeric", allowEmptyValue = false, required = true)
	private Integer userId;
	@Column(name = "bookingStartDate", nullable = false)
	@ApiModelProperty(value = "Booking Start Date", notes = "Booking Start Date(yyyy-MM-ddThh:mm:ss)", allowableValues = "yyyy-MM-ddThh:mm:ss", allowEmptyValue = false, required = true)
	private LocalDateTime bookingStartDate;
	@Column(name = "bookingEndDate", nullable = false)
	@ApiModelProperty(value = "Booking End Date", notes = "Booking End Date(yyyy-MM-ddThh:mm:ss)", allowableValues = "yyyy-MM-ddThh:mm:ss", allowEmptyValue = false, required = true)
	private LocalDateTime bookingEndDate;
	@Column(name = "bookingTime")
	@ApiModelProperty(value = "Booking Time", notes = "Record the time when Booking is made(yyyy-MM-ddThh:mm:ss)", allowableValues = "yyyy-MM-ddThh:mm:ss", allowEmptyValue = false, required = true)
	private LocalDateTime bookingTime;
	@Column(name = "bookingAmount")
	@ApiModelProperty(value = "Total Payment for Booking", notes = "Record the Total Amount spent on Booking", allowableValues = "Alphanumeric", allowEmptyValue = false, required = true)
	private Double bookingAmount;

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getCarPlateNumber() {
		return carPlateNumber;
	}

	public void setCarPlateNumber(Integer carPlateNumber) {
		this.carPlateNumber = carPlateNumber;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public LocalDateTime getBookingStartDate() {
		return bookingStartDate;
	}

	public void setBookingStartDate(LocalDateTime bookingStartDate) {
		this.bookingStartDate = bookingStartDate;
	}

	public LocalDateTime getBookingEndDate() {
		return bookingEndDate;
	}

	public void setBookingEndDate(LocalDateTime bookingEndDate) {
		this.bookingEndDate = bookingEndDate;
	}

	public LocalDateTime getBookingTime() {
		return bookingTime;
	}

	public void setBookingTime(LocalDateTime bookingTime) {
		this.bookingTime = bookingTime;
	}

	public Double getBookingAmount() {
		return bookingAmount;
	}

	public void setBookingAmount(Double bookingAmount) {
		this.bookingAmount = bookingAmount;
	}

	public CarBooking(Integer bookingId, Integer carPlateNumber, Integer userId, LocalDateTime bookingStartDate,
			LocalDateTime bookingEndDate, LocalDateTime bookingTime, Double bookingAmount) {
		this.bookingId = bookingId;
		this.carPlateNumber = carPlateNumber;
		this.userId = userId;
		this.bookingStartDate = bookingStartDate;
		this.bookingEndDate = bookingEndDate;
		this.bookingTime = bookingTime;
		this.bookingAmount = bookingAmount;
	}

	public CarBooking() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "CarBooking [bookingId=" + bookingId + ", carPlateNumber=" + carPlateNumber + ", userId=" + userId
				+ ", bookingStartDate=" + bookingStartDate + ", bookingEndDate=" + bookingEndDate + ", bookingTime="
				+ bookingTime + ", bookingAmount=" + bookingAmount + "]";
	}

}
