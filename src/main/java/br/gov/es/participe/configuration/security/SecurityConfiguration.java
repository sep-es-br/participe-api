package br.gov.es.participe.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.gov.es.participe.service.TokenService;
import org.springframework.core.env.Environment;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private Environment env;
    
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and().csrf().disable()
                .authorizeRequests()

                // plans
                .antMatchers(HttpMethod.GET, "/plans").permitAll()

                // structures
                .antMatchers(HttpMethod.GET, "/structures").permitAll()

                // citizen

                // conferences
                .antMatchers("/conferences/portal").permitAll()
                .antMatchers("/conferences/**/regionalization").permitAll()
                .antMatchers("/conferences/with-presential-meetings").permitAll()
                .antMatchers("/conferences/with-meetings").permitAll()
                .antMatchers("/conferences/**/selfdeclarations").permitAll()
                .antMatchers("/conferences/**/highlights").permitAll()
                .antMatchers("/conferences/**/comments").permitAll()
                .antMatchers("/conferences/AuthenticationScreen/**").permitAll()
                .antMatchers("/conferences/validateDefaultConference").permitAll()
                .antMatchers("/conferences/validate").permitAll()

                // domain
                .antMatchers(HttpMethod.GET, "/domain/**").permitAll()

                // comments

                // signout
                .antMatchers("/signout").permitAll()

                // files
                .antMatchers(HttpMethod.GET, "/files/**").permitAll()
                
                // signin
                .antMatchers("/signin/acesso-cidadao")
                .authenticated().and().oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestResolver(new AuthorizationRequestResolver(clientRegistrationRepository,
                        "/oauth2/authorization"));
        httpSecurity.addFilterBefore(new SecurityFilter(tokenService, env), UsernamePasswordAuthenticationFilter.class);
    }


}
