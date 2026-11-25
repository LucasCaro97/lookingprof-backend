package com.lookingprof.lookingProf;

import com.lookingprof.lookingProf.seed.SeedProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SeedProperties.class)
public class LookingProfApplication {

	public static void main(String[] args) {
		SpringApplication.run(LookingProfApplication.class, args);

	}

}
