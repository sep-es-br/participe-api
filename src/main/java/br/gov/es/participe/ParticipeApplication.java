package br.gov.es.participe;

import br.gov.es.participe.configuration.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@EnableConfigurationProperties({ApplicationProperties.class})
@EnableNeo4jRepositories("br.gov.es.participe.repository")
@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		UserDetailsServiceAutoConfiguration.class
})
public class ParticipeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParticipeApplication.class, args);
	}

}
