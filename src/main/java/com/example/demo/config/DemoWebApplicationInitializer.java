package com.example.demo.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class DemoWebApplicationInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    DispatcherServlet servlet = new DispatcherServlet();
    ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", servlet);
    registration.setAsyncSupported(true);
  }

}