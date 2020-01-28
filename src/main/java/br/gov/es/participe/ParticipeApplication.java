package br.gov.es.participe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import br.gov.es.participe.configuration.ApplicationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@EnableNeo4jRepositories("br.gov.es.participe.repository")
public class ParticipeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParticipeApplication.class, args);
	}

}
