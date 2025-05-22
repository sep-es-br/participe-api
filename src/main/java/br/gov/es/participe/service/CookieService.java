package br.gov.es.participe.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Service
public class CookieService {
	
	@Value("${app.domain.cookie}")
    private String domainCookie;


    public void createCookie(HttpServletResponse response, String name, String value, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);
        cookie.setDomain(domainCookie);
        response.addCookie(cookie);
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name, String path) {
        Cookie cookie = findCookie(request, name);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);
        cookie.setDomain(domainCookie);
        response.addCookie(cookie);
    }

    public Cookie findCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            Optional<Cookie> filter = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals(name)).findAny();
            if (!filter.isPresent()) {
                throw new IllegalArgumentException("Cookie ".concat(name).concat(" not found."));
            } else {
                return filter.get();
            }
        }
        throw new IllegalArgumentException("Cookie ".concat(name).concat(" not found."));
    }
    
    public boolean exists(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            Optional<Cookie> filter = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals(name)).findAny();
            return filter.isPresent();
        }
        return false;
    }

}
