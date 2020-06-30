package br.gov.es.participe.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.service.TwitterService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    
    @Autowired
    private TokenService tokenService;

    @Autowired
    private TwitterService twitterService;

    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and().csrf().disable()
                .authorizeRequests()
                //.antMatchers(HttpMethod.GET, "/plans").hasRole("Administrator") 
                .antMatchers("/plans").hasRole("Administrator")
                .antMatchers("/structures").hasRole("Administrator")
                .antMatchers("/signout").permitAll()
                .antMatchers("/files/**").permitAll()
                .antMatchers("/signin/acesso-cidadao")
                .authenticated().and().oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestResolver(new AuthorizationRequestResolver(clientRegistrationRepository,
                        "/oauth2/authorization", twitterService));
        httpSecurity.addFilterBefore(securityFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    
    @Bean
    public SecurityFilter securityFilter() {
    	return new SecurityFilter(tokenService);
    }
}
