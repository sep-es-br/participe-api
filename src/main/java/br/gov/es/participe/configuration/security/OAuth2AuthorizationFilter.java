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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

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

    @Autowired
    private HttpSession httpSession;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getRequestURI().contains("/oauth2/authorization/")) {
            if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
                HashMap<String, String> params = participeUtils.convertQueryStringToHashMap(request.getQueryString());
                if (params.get("front_callback_url") != null) {
                	cookieService.createCookie(
                			response,
                			"front_callback_url",
                			params.get("front_callback_url"),
                			"/participe"
        			);
                }
                if (params.get("front_conference_id") != null) {
                	cookieService.createCookie(
                			response,
                			"front_conference_id",
                			params.get("front_conference_id"),
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

    }
}
