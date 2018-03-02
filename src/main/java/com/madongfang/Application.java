package com.madongfang;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		// TODO Auto-generated method stub
		return application.sources(Application.class);
	}
	
	@Bean
	public FilterRegistrationBean registration(ApiFilter apiFilter)
	{
		FilterRegistrationBean registration = new FilterRegistrationBean(apiFilter);
		
		registration.setUrlPatterns(Arrays.asList("/api/*"));
		
		return registration;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
