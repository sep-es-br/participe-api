package br.gov.es.participe.configuration.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import br.gov.es.participe.util.dto.RecaptchaResponse;

@Component
public class CaptchaFilter extends OncePerRequestFilter {
	
	private final RestTemplate restTemplate;

	@Value("${google.recaptcha.key.secret}")
	public String recaptchaSecret;

	@Value("${google.recaptcha.verify.url}")
	public String recaptchaVerifyUrl;
	
	@Autowired
	public CaptchaFilter(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String url = request.getRequestURI();
		if (url.equals("/participe/person")) {
			if (request.getMethod().equals("OPTIONS")) {
				filterChain.doFilter(request, response);
				return;
			}
			if (request.getMethod().equals("POST") ) {
				String resp = request.getParameter("g-recaptcha-response");
				MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
				param.add("secret", recaptchaSecret);
				param.add("response", resp);
				
				RecaptchaResponse recaptchaResponse = null;
				try {
					recaptchaResponse = this.restTemplate.postForObject(recaptchaVerifyUrl, param, RecaptchaResponse.class);
				} catch (RestClientException e) {
					System.out.print(e.getMessage());
				}
				if (recaptchaResponse.isSuccess()) {
					filterChain.doFilter(request, response);
				} else {
					throw new IllegalArgumentException("Captcha inválido");
				}
			}else {
				filterChain.doFilter(request, response);
			}
			
		}else {
			filterChain.doFilter(request, response);
		}
		
	}

}
