package id.ac.ui.cs.advprog.papikosbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    
    @Bean(name = "bookingTaskExecutor")
    public Executor bookingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Base number of threads
        executor.setMaxPoolSize(5);  // Maximum number of threads
        executor.setQueueCapacity(100); // How many tasks can queue up
        executor.setThreadNamePrefix("Booking-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "chatTaskExecutor")
    public Executor chatTaskExecutor(MeterRegistry registry) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Chat-");
        executor.initialize();

        registry.gauge("executor.chat.active", executor, e -> e.getActiveCount());
        registry.gauge("executor.chat.pool", executor, e -> e.getPoolSize());
        registry.gauge("executor.chat.queue", executor, e -> e.getThreadPoolExecutor().getQueue().size());

        return executor;
    }
}