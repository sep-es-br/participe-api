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

public class SecurityFilter extends OncePerRequestFilter {

    private TokenService tokenService;

    public SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String url = request.getRequestURI();

            boolean isPublicUrl = url.endsWith("/signin/refresh")
                    
                    || url.matches(".*/files/\\d+")
                    || url.matches(".*/localities/complement/\\d+")
                    || url.matches(".*/participation/portal-header/\\d+")
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
                    || url.matches(".*/conferences/portal");

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
