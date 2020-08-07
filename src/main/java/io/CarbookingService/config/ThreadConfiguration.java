package io.CarbookingService.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadConfiguration {
	
	
	@Bean
	public Executor ThreadPoolConfiguration() {
		ThreadPoolTaskExecutor executorPool=new ThreadPoolTaskExecutor();
		executorPool.setCorePoolSize(5);
		executorPool.setMaxPoolSize(12);
		executorPool.setQueueCapacity(100);
		executorPool.setAllowCoreThreadTimeOut(true);
		executorPool.setThreadNamePrefix("booking-thread-");
		executorPool.initialize();	
		return executorPool;
	}

}
