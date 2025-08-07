package br.gov.es.participe.configuration.security;

import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;
import br.gov.es.participe.util.dto.MessageDto;
import org.apache.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class SecurityFilter extends OncePerRequestFilter {

    private TokenService tokenService;
    
    private Environment env;
    
    public SecurityFilter(TokenService tokenService, Environment env) {
        this.tokenService = tokenService;
        this.env = env;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String url = request.getRequestURI();

            boolean isPublicUrl = url.endsWith("/signin/refresh")
                    
                    || url.matches(".*/files/\\d+")
                    || url.matches(".*/localities/complement/\\d+")
                    || url.matches(".*/participation/portal-header/\\d+")
                    || url.matches(".*/participation/web-header/\\d+")
                    || url.matches(".*/participation/portal-header/\\d+/selfdeclarations/decline")
                    || url.matches(".*/conferences/AuthenticationScreen/\\d+")
                    || url.matches(".*/conferences/AuthenticationScreen/\\d+/pre-opening")
                    || url.matches(".*/conferences/AuthenticationScreen/\\d+/post-closure")
                    || url.endsWith("/person")
                    || url.endsWith("/person/validate")
                    || url.endsWith("/person/complement")
                    || url.endsWith("/person/forgot-password")
                    || url.matches(".*/color/\\d+")
                    || url.matches(".*/meetings")
                    || url.matches(".*/meetings/\\d+")
                    || url.matches(".*/meetings/\\d+/page-number")
                    || url.matches(".*/meetings/dashboard/\\d+")
                    || url.matches(".*/meetings/checkIn/\\d+")
                    || url.matches(".*/meetings/\\d+/pre-registration")
                    || url.matches(".*/meetings/\\d+/self-check-in")
                    || url.matches(".*/meetings/\\d+/targeted-by/plan-items")
                    || url.matches(".*/meetings/\\d+/participants")
                    || url.matches(".*/meetings/\\d+/participants/total")
                    || url.matches(".*/meetings/\\d+/generate-link-pre-registration")
                    || url.matches(".*/meetings/\\d+/generate-qr-code-check-in")
                    || url.endsWith("/meetings/selfcheckIn")
                    || url.endsWith("/person/forgot-password")
                    || url.endsWith("/login")
                    || url.endsWith("/portal")
                    || url.endsWith("/signin/acesso-cidadao")
                    || url.endsWith("/signin/acesso-cidadao-profile-response")
                    || url.endsWith("/acesso-cidadao-profile-response.html")
                    || url.endsWith("/signin/acesso-cidadao-response")
                    || url.endsWith("/acesso-cidadao-response.html")
                    || url.endsWith("/signin")
                    || url.endsWith("/signin/participe")
                    || url.endsWith("/signin/facebook")
                    || url.endsWith("/signin/facebook-response")
                    || url.endsWith("/signin/facebook-profile")
                    || url.endsWith("/signin/facebook-profile-response")
                    || url.endsWith("/signin/google")
                    || url.endsWith("/signin/google-response")
                    || url.endsWith("/signin/google-profile")
                    || url.endsWith("/signin/google-profile-response")
                    || url.endsWith("/swagger-ui.html")
                    || url.endsWith("/v2/api-docs")
                    || url.contains("/swagger-resources")
                    || url.contains("/webjars/")
                    || url.matches(".*/conferences/validateDefaultConference")
                    || url.matches(".*/conferences/validate")
                    || url.matches(".*/conferences/\\d+")
                    || url.matches(".*/conferences/moderators")
                    || url.matches(".*/conferences/\\d+/moderators")
                    || url.matches(".*/conferences/receptionists")
                    || url.matches(".*/conferences/\\d+/comments")
                    || url.matches(".*/conferences/\\d+/highlights")
                    || url.matches(".*/conferences/\\d+/selfdeclarations")
                    || url.matches(".*/conferences/with-meetings")
                    || url.matches(".*/conferences/\\d+/regionalization")
                    || url.matches(".*/conferences/portal")
                    || url.matches(".*/authorityCredential/.*");
            
            if(
                   (isPublicUrl)
                || (url.matches(".*/integration/.*") && validateIntegration(request))
            ) {
                filterChain.doFilter(request, response);
            } else {
                String token = getToken(request);
                Authentication auth = token != null ? tokenService.getAuthentication(token) : null;
                SecurityContextHolder.getContext().setAuthentication(auth);
                if (tokenService.isValidToken(token, TokenType.AUTHENTICATION)) {
                    filterChain.doFilter(request, response);
                } else {
                    throw new IllegalArgumentException();
                }
            }   

        } catch (IllegalArgumentException e) {
        	if ("Captcha inválido".equals(e.getMessage())) {
        		response.setStatus(HttpStatus.SC_BAD_REQUEST);
        		MessageDto messageDto = new MessageDto(400, e.getMessage());
        		response.getWriter().write(messageDto.getJSON());
        	} else {
        		response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        		MessageDto messageDto = new MessageDto(401, "Invalid token");
        		response.getWriter().write(messageDto.getJSON());
        	}
        } catch (Exception e) {
        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
    		MessageDto messageDto = new MessageDto(401, "Invalid token");
    		response.getWriter().write(messageDto.getJSON());        	
        }
    }
    
    private boolean validateIntegration(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return false;
        }

        try {
            // Remove "Basic " e decodifica
            String base64Credentials = authHeader.substring("Basic ".length());
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes);

            String[] parts = credentials.split(":", 2);
            if (parts.length != 2) return false;

            String client = parts[0];
            String secret = parts[1];

            // Lê o valor esperado do application.properties
            String expectedSecret = env.getProperty("clientCredential." + client + ".secret");

            return expectedSecret != null && expectedSecret.equals(secret);

        } catch (IllegalArgumentException e) {
            // Base64 inválido
            return false;
        }
    }

    private String getToken(HttpServletRequest request) {
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
