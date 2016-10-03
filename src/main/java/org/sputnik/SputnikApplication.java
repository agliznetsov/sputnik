package org.sputnik;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.sputnik.config.SputnikConfig;

@SpringBootApplication
@Import(SputnikConfig.class)
public class SputnikApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SputnikApplication.class)
				.bannerMode(Banner.Mode.OFF)
				.run(args);
	}

}
