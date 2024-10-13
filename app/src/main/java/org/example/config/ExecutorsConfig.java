package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ExecutorsConfig {

    @Value("${spring.concurrency.max}")
    private int maxPoolSize;

    @Bean(name = "myFixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(maxPoolSize, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("InitializerWorker-" + thread.getName());
            System.out.println(thread.getName());
            return thread;
        });
    }

    @Bean(name = "myScheduledThreadPool")
    public ScheduledExecutorService scheduledThreadPool() {
        return Executors.newScheduledThreadPool(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("InitializerScheduler");
            System.out.println(thread.getName());
            return thread;
        });
    }
}
