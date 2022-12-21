package br.gov.es.participe;

import br.gov.es.participe.configuration.ApplicationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.*;


import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableConfigurationProperties({ApplicationProperties.class})
@EnableNeo4jRepositories("br.gov.es.participe.repository")
@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		UserDetailsServiceAutoConfiguration.class
})
@EnableScheduling
@ComponentScan({"br.gov.es.participe"})
public class ParticipeApplication {

	@Value("${app.default-timezone}")
	private String timeZone;

	@PostConstruct
	public void init(){
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	public static void main(String[] args) {
		SpringApplication.run(ParticipeApplication.class, args);
	}
}
