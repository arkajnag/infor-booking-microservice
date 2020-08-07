package io.CarbookingService.service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import io.CarbookingService.model.Car;
import io.CarbookingService.model.CarDTO;
import io.CarbookingService.model.User;
import io.CarbookingService.model.UserDTO;

@Service
public class ProxyService {
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("${application.car.service.endPoint.getAll:'http://localhost:3002/rest/car/all'}")
	private String carServiceEndPoint; //http://localhost:3002/rest/car/all
	
	@Value("${application.user.service.endPoint.getAll:'http://localhost:3001/rest/user/all'}")
	private String userServiceEndPoint; //http://localhost:3001/rest/user/all
	
	@HystrixCommand(fallbackMethod = "getFallbackCarInfo",commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value ="2000" ),
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "5"),
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "50"),
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "5000")
	})
	public CarDTO getCarInfo() {
		CarDTO carDTOResponseObj=restTemplate.getForObject(carServiceEndPoint, CarDTO.class);
		return carDTOResponseObj;
	}
	
	public CarDTO getFallbackCarInfo() {
		CarDTO carDTOResponseObj=new CarDTO();
		carDTOResponseObj.setCars(Stream.of
				(new Car(000,"No Model Number",LocalDateTime.parse("0000-00-00T00:00:00"),LocalDateTime.parse("0000-00-00T00:00:00"),0.0))
				.collect(Collectors.toList()));
		return carDTOResponseObj;
	}

	@HystrixCommand(fallbackMethod = "getFallbackUserInfo",commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value ="2000" ),
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "5"),
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "50"),
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "5000")
	})
	public UserDTO getUserInfo() {
		UserDTO userDTOResponseObj=restTemplate.getForObject(userServiceEndPoint, UserDTO.class);
		return userDTOResponseObj;
	}

	public UserDTO getFallbackUserInfo() {
		UserDTO userDTOResponseObj=new UserDTO();
		userDTOResponseObj.setUsers(Stream.of(new User(000,"No User Information")).collect(Collectors.toList()));
		return userDTOResponseObj;
	}
	
}
