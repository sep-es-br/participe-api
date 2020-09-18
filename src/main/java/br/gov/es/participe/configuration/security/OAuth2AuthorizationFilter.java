package br.gov.es.participe.configuration.security;

import br.gov.es.participe.service.CookieService;
import br.gov.es.participe.util.ParticipeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@WebFilter(
        urlPatterns = {
                "/oauth2/authorization/facebook",
                "/oauth2/authorization/twitter",
                "/oauth2/authorization/google",
                "/oauth2/authorization/idsvr"
        }
)
@Order(-150)
public class OAuth2AuthorizationFilter implements Filter {

    @Autowired
    private CookieService cookieService;

    @Autowired
    private ParticipeUtils participeUtils;
    
    private static final String FRONT_CALLBACK_URL = "front_callback_url";
    private static final String FRONT_CONFERENCE_ID = "front_conference_id";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	//Método não implementado.
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getRequestURI().contains("/oauth2/authorization/")) {
            if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
                Map<String, String> params = participeUtils.convertQueryStringToHashMap(request.getQueryString());
                if (params.get(FRONT_CALLBACK_URL) != null) {
                	cookieService.createCookie(
                			response,
                			FRONT_CALLBACK_URL,
                			params.get(FRONT_CALLBACK_URL),
                			"/participe"
        			);
                }
                if (params.get(FRONT_CONFERENCE_ID) != null) {
                	cookieService.createCookie(
                			response,
                			FRONT_CONFERENCE_ID,
                			params.get(FRONT_CONFERENCE_ID),
                			"/participe"
        			);
                }
                response.sendRedirect(request.getRequestURI());
            } else {
                chain.doFilter(servletRequest, servletResponse);
            }
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    	//Método não implementado por não haver necessidade.
    }
}
