package com.multitenant.demo;

import com.multitenant.demo.filter.TenantFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<TenantFilter> loggingFilter(){
		FilterRegistrationBean<TenantFilter> registrationBean
				= new FilterRegistrationBean<>();

		registrationBean.setFilter(new TenantFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(2);

		return registrationBean;
	}

}
