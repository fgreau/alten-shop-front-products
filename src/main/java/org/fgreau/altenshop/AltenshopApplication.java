package org.fgreau.altenshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
public class AltenshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(AltenshopApplication.class, args);
	}

}
