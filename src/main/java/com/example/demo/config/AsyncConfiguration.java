package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AsyncConfiguration implements WebMvcConfigurer {

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setTaskExecutor(taskExecutor());
  }

  @Bean
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setThreadGroupName("mvc-threadPoolTaskExecutor");
    taskExecutor.setCorePoolSize(14);
    taskExecutor.setMaxPoolSize(14);
    taskExecutor.setAllowCoreThreadTimeOut(true);
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    return taskExecutor;
  }

}