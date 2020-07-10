package br.gov.es.participe.configuration.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;
import br.gov.es.participe.util.dto.MessageDto;

public class SecurityFilter extends OncePerRequestFilter {

    private TokenService tokenService;

    public SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String url = request.getRequestURI();

            boolean isPublicUrl = url.endsWith("/signin/refresh")
                    
                    || url.contains("/files/")
                    || url.contains("/localities/complement")
                    || url.contains("/participation/portal-header")
                    || url.contains("/conferences/AuthenticationScreen")
                    || url.contains("/person")
                    || url.endsWith("/person/forgot-password")
                    || url.endsWith("/login")
                    || url.endsWith("/signin/acesso-cidadao")
                    || url.endsWith("/signin/acesso-cidadao-response")
                    || url.endsWith("/acesso-cidadao-response.html")
                    || url.endsWith("/signin")
                    || url.endsWith("/signin/participe")
                    || url.endsWith("/signin/facebook")
                    || url.endsWith("/signin/facebook-response")
                    || url.endsWith("/signin/google")
                    || url.endsWith("/signin/google-response")
                    || url.endsWith("/signin/twitter")
                    || url.endsWith("/signin/twitter-response")
                    || url.endsWith("/swagger-ui.html")
                    || url.endsWith("/v2/api-docs")
                    || url.contains("/swagger-resources")
                    || url.endsWith("/acesso-cidadao-response.html")
                    || url.contains("/webjars/");

            if (!isPublicUrl) {
                String token = getToken(request);
                Authentication auth = token != null ? tokenService.getAuthentication(token) : null;
                SecurityContextHolder.getContext().setAuthentication(auth);
                if (tokenService.isValidToken(token, TokenType.AUTHENTICATION)) {
                    filterChain.doFilter(request, response);
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
            	filterChain.doFilter(request, response);            	
            }

        } catch (Exception e) {
        	if (e instanceof IllegalArgumentException && "Captcha inválido".equals(e.getMessage())) {
        		response.setStatus(HttpStatus.SC_BAD_REQUEST);
        		MessageDto messageDto = new MessageDto(400, e.getMessage());
        		response.getWriter().write(messageDto.getJSON());       		
        	} else {
        		response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        		MessageDto messageDto = new MessageDto(401, "Invalid token");
        		response.getWriter().write(messageDto.getJSON());        		
        	}
        }
    }

    private String getToken(HttpServletRequest request) throws IllegalArgumentException {
        String token = request.getHeader("Authorization");

        if (token == null) {
            throw new IllegalArgumentException();
        }

        if (!token.startsWith("Bearer ")) {
            throw new IllegalArgumentException();
        }

        return token.substring(7);
    }
}
