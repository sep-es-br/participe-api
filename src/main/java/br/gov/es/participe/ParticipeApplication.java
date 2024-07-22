package br.gov.es.participe;

import br.gov.es.participe.configuration.ApplicationProperties;
import br.gov.es.participe.controller.dto.PublicAgentDto;
import br.gov.es.participe.service.AcessoCidadaoService;

import org.springframework.beans.factory.annotation.Autowired;
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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import java.util.List;
import java.util.TimeZone;

@EnableConfigurationProperties({ApplicationProperties.class})
@EnableNeo4jRepositories("br.gov.es.participe.repository")
@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		UserDetailsServiceAutoConfiguration.class
})
@EnableScheduling
@ComponentScan({"br.gov.es.participe"})
public class ParticipeApplication implements ServletContextListener, HttpSessionListener {

	@Value("${app.default-timezone}")
	private String timeZone;

	@Autowired
    private AcessoCidadaoService acessoCidadaoService;

	private static List<PublicAgentDto> publicAgentsData;

	@PostConstruct
	public void init(){
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
		publicAgentsData = acessoCidadaoService.findPublicAgentsFromAcessoCidadaoAPI();
	}

	public static void main(String[] args) {
		SpringApplication.run(ParticipeApplication.class, args);
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContextListener.super.contextInitialized(sce);
	}

	@Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        session.setAttribute("publicAgents", publicAgentsData);
    }

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		// TODO Auto-generated method stub
		HttpSessionListener.super.sessionDestroyed(se);
	}

}
