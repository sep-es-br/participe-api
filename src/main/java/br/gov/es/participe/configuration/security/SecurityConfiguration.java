package br.gov.es.participe.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import br.gov.es.participe.service.TokenService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private TokenService tokenService;





    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and().csrf().disable()
                .authorizeRequests()

                // plans
                .antMatchers(HttpMethod.GET, "/plans").permitAll()
                // .antMatchers("/plans/**").hasAuthority("Administrator")

                // structures
                .antMatchers(HttpMethod.GET, "/structures").permitAll()
                // .antMatchers("/structures/**").hasRole("Administrator")

                // citizen
                // .antMatchers(HttpMethod.GET, "/citizen/**").hasRole("Administrator")

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
                // .antMatchers(HttpMethod.POST, "/conferences/**").hasRole("Administrator")
                // .antMatchers(HttpMethod.PUT, "/conferences/**").hasRole("Administrator")
                // .antMatchers(HttpMethod.DELETE, "/conferences/**").hasRole("Administrator")

                // domain
                .antMatchers(HttpMethod.GET, "/domain/**").permitAll()
                // .antMatchers("/domain/**").hasRole("Administrator")

                // comments
                // .antMatchers(HttpMethod.DELETE, "/comments/**").hasRole("Administrator")

                // signout
                .antMatchers("/signout").permitAll()

                // files
                .antMatchers(HttpMethod.GET, "/files/**").permitAll()
                // .antMatchers("/files/**").hasRole("Administrator")

                // signin
                .antMatchers("/signin/acesso-cidadao")
                .authenticated().and().oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestResolver(new AuthorizationRequestResolver(clientRegistrationRepository,
                        "/oauth2/authorization"));
        httpSecurity.addFilterBefore(securityFilter(), UsernamePasswordAuthenticationFilter.class);
    }

   

    @Bean
    public SecurityFilter securityFilter() {
        return new SecurityFilter(tokenService);
    }

}
