package br.gov.es.participe.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
public class GoogleProperties {

    private String redirecturi;

	public String getRedirecturi() {
		return redirecturi;
	}

	public void setRedirecturi(String redirecturi) {
		this.redirecturi = redirecturi;
	}
    
}
