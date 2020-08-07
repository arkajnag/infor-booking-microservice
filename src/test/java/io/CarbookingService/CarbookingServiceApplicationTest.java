package io.CarbookingService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest
class CarbookingServiceApplicationTest extends AbstractTestNGSpringContextTests{

	private Logger logger = LoggerFactory.getLogger(CarbookingServiceApplicationTest.class);
	@Test
	void contextLoads() {
		logger.info("Basic Unit Test");
	}

}
