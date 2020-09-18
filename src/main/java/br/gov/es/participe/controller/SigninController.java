package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.service.*;
import br.gov.es.participe.util.dto.MessageDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping(value = "/signin")
public class SigninController {

    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    @Autowired
    private FacebookService facebookService;

    @Autowired
    private GoogleService googleService;

    @Autowired
    private TwitterService twitterService;

    @Autowired
    private CookieService cookieService;
    
    @Autowired
	private PersonService personService;

    private static final String FRONT_CALLBACK_URL = "front_callback_url";
    private static final String FRONT_CONFERENCE_ID = "front_conference_id";

    @GetMapping("/refresh")
    public ResponseEntity<SigninDto> refresh(@RequestParam(name = "refreshToken") String refreshToken) {
        SigninDto signinDto = acessoCidadaoService.refresh(refreshToken);

        return ResponseEntity.ok().body(signinDto);
    }
    
    @PostMapping
    public ResponseEntity indexparticipe(@RequestBody PersonParamDto user,
    									 @RequestParam(name = "conference",required = true ) Long conference) {
    	SigninDto signinDto = personService.authenticate(user, "Participe", conference);
    	
    	if(signinDto != null) {
    	    if(signinDto.getPerson().getActive() != null && !signinDto.getPerson().getActive()) {
                MessageDto msg = new MessageDto();
                msg.setMessage("Usuário inativo.");
                msg.setCode(403);
                return ResponseEntity.status(403).body(msg);
            }
    		return ResponseEntity.status(200).body(signinDto);
    	}
    	MessageDto msg = new MessageDto();
		msg.setMessage("E-mail ou Senha incorretos");
		msg.setCode(404);
		return ResponseEntity.status(404).body(msg);
    }

    @GetMapping("/acesso-cidadao")
    public ResponseEntity<Void> index() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/acesso-cidadao-response")
    public RedirectView acessoCidadao(
            @RequestParam(name = "access_token") String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        SigninDto signinDto = acessoCidadaoService.authenticate(token, getConferenceId(request, response));
        String valor = Base64.getEncoder().withoutPadding().encodeToString(
                new ObjectMapper().writeValueAsString(signinDto).getBytes());
        return new RedirectView(buildFronstCallBackUrl(request, response, valor));
    }

    @GetMapping("/facebook")
    public RedirectView indexFacebook(
            @RequestParam("code") String authorizationCode,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String accessToken = facebookService.facebookAcessToken(authorizationCode, request);
        RedirectView redirectView = new RedirectView("facebook-response?access_token=" + accessToken);
        redirectView.setPropagateQueryParams(true);
        return redirectView;
    }

    @GetMapping("/facebook-response")
    public RedirectView facebook(
            @RequestParam(name = "access_token") String accessToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws JsonProcessingException {
        SigninDto signinDto = facebookService.authenticate(accessToken, getConferenceId(request, response));
        String valor = Base64.getEncoder().withoutPadding().encodeToString(
                new ObjectMapper().writeValueAsString(signinDto).getBytes());
        return new RedirectView(buildFronstCallBackUrl(request, response, valor));
    }

    @GetMapping("/google")
    public RedirectView indexGoogle(
            @RequestParam("code") String authorizationCode,
            HttpServletRequest request
    ) {
        String accessToken = googleService.googleAcessToken(authorizationCode, request);
        return  new RedirectView("google-response?access_token=" + accessToken);
    }

    @GetMapping("/google-response")
    public RedirectView google(
            @RequestParam(name = "access_token") String accessToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws JsonProcessingException {
        SigninDto signinDto = googleService.authenticate(accessToken, getConferenceId(request, response));
        String valor = Base64.getEncoder().withoutPadding().encodeToString(
                new ObjectMapper().writeValueAsString(signinDto).getBytes());
        return new RedirectView(buildFronstCallBackUrl(request, response, valor));
    }


    @GetMapping("/twitter")
    public RedirectView indexTwitter(
            @RequestParam("oauth_token") String oauthToken,
            @RequestParam("oauth_verifier") String oauthVerifier,
            HttpServletRequest request
    ) throws MalformedURLException {
    	return new RedirectView(twitterService.oauthTokenAndSecret(request, oauthToken, oauthVerifier));
    }


    @GetMapping("/twitter-response")
    public RedirectView twitter(
            @RequestParam(name = "oauth_token") String oauthToken,
            @RequestParam(name = "oauth_token_secret") String oauthTokenSecret,
            @RequestParam(name = "user_id") String userId,
            @RequestParam(name = "screen_name") String screenName,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws JsonProcessingException {
        SigninDto signinDto = twitterService.authenticate(oauthToken, oauthTokenSecret, getConferenceId(request, response));
        String valor = Base64.getEncoder().withoutPadding().encodeToString(
                new ObjectMapper().writeValueAsString(signinDto).getBytes());
        return new RedirectView(buildFronstCallBackUrl(request, response, valor));
    }

    private String buildFronstCallBackUrl(HttpServletRequest request, HttpServletResponse response, String valor) {
        Cookie cookie = cookieService.findCookie(request, FRONT_CALLBACK_URL);
        cookieService.deleteCookie(request, response, FRONT_CALLBACK_URL, "/participe");
        String url = cookie.getValue();
        if (url.contains("?")) {
        	url = url.substring(0, url.indexOf('?'));
        }
        return url.concat("/#/home?signinDto=".concat(valor));
    }
    
    private Long getConferenceId(HttpServletRequest request,  HttpServletResponse response) {
    	if (request.getCookies() != null) {
    		Optional<Cookie> filter = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals(FRONT_CONFERENCE_ID)).findFirst();
            if (filter.isPresent()) {
            	cookieService.deleteCookie(request, response, FRONT_CONFERENCE_ID, "/participe");
            	return Long.valueOf(filter.get().getValue());
            }
    	}
    	return null;
    }

}