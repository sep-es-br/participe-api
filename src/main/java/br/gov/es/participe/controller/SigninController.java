package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.PersonProfileSignInDto;
import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.service.AcessoCidadaoService;
import br.gov.es.participe.service.CookieService;
import br.gov.es.participe.service.FacebookService;
import br.gov.es.participe.service.GoogleService;
import br.gov.es.participe.service.PersonService;
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
import java.nio.charset.StandardCharsets;
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
  public ResponseEntity signIn(@RequestBody PersonParamDto user,
                               @RequestParam(name = "conference") Long conference) {
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
    SigninDto signinDto = acessoCidadaoService.authenticate(token,
                                                            getConferenceId(request, response)
    );
    String valor = encode(signinDto);
    return new RedirectView(buildHomeCallbackUrl(request, response, valor));
  }

  @GetMapping("/acesso-cidadao-profile-response")
  public RedirectView acessoCidadaoProfile(
    @RequestParam(name = "access_token") String token,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws IOException {
    PersonProfileSignInDto persoProfileSignInDto = acessoCidadaoService.authenticateProfile(
      token,
      getConferenceId(request, response)
    );
    String valor = encode(persoProfileSignInDto);
    return new RedirectView(buildProfileCallbackUrl(request, response, valor));
  }

  @GetMapping("/facebook")
  public RedirectView indexFacebook(
    @RequestParam("code") String authorizationCode,
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    String accessToken = facebookService.facebookAccessToken(
      authorizationCode,
      request,
      "/signin/facebook"
    );
    RedirectView redirectView = new RedirectView("facebook-response?access_token=" + accessToken);
    redirectView.setPropagateQueryParams(true);
    return redirectView;
  }

  @GetMapping("/facebook-profile")
  public RedirectView indexFacebookProfile(
    @RequestParam("code") String authorizationCode,
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    String accessToken = facebookService.facebookProfileAccessToken(
      authorizationCode
    );
    RedirectView redirectView = new RedirectView(
      "facebook-profile-response?access_token=" + accessToken);
    redirectView.setPropagateQueryParams(true);
    return redirectView;
  }

  @GetMapping("/facebook-profile-response")
  public RedirectView facebookProfileResponse(
    @RequestParam(name = "access_token") String accessToken,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws JsonProcessingException {
    PersonProfileSignInDto personProfileSignInDto = facebookService.authenticateProfile(
      accessToken,
      getConferenceId(request, response)
    );
    String valor = encode(personProfileSignInDto);
    return new RedirectView(buildProfileCallbackUrl(request, response, valor));
  }

  @GetMapping("/facebook-response")
  public RedirectView facebook(
    @RequestParam(name = "access_token") String accessToken,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws JsonProcessingException {
    SigninDto signinDto = facebookService.authenticate(accessToken,
                                                       getConferenceId(request, response)
    );
    String valor = encode(signinDto);
    return new RedirectView(buildHomeCallbackUrl(request, response, valor));
  }

  @GetMapping("/google")
  public RedirectView indexGoogle(
    @RequestParam("code") String authorizationCode,
    HttpServletRequest request
  ) {
    String accessToken = googleService.googleAccessToken(
      authorizationCode,
      request,
      "/signin/google"
    );
    return new RedirectView("google-response?access_token=" + accessToken);
  }

  @GetMapping("/google-profile")
  public RedirectView indexGoogleProfile(
    @RequestParam("code") String authorizationCode,
    HttpServletRequest request
  ) {
    String accessToken = googleService.googleProfileAccessToken(
      authorizationCode);
    return new RedirectView("google-profile-response?access_token=" + accessToken);
  }

  @GetMapping("/google-response")
  public RedirectView google(
    @RequestParam(name = "access_token") String accessToken,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws JsonProcessingException {
    SigninDto signinDto = googleService.authenticate(accessToken,
                                                     getConferenceId(request, response)
    );
    String valor = encode(signinDto);
    return new RedirectView(buildHomeCallbackUrl(request, response, valor));
  }

  @GetMapping("/google-profile-response")
  public RedirectView googleProfileResponse(
    @RequestParam(name = "access_token") String accessToken,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws JsonProcessingException {
    PersonProfileSignInDto personProfileSignInDto = googleService.authenticateProfile(
      accessToken,
      getConferenceId(request, response)
    );
    String encodedSignInDto = encode(personProfileSignInDto);
    return new RedirectView(buildProfileCallbackUrl(request, response, encodedSignInDto));
  }

  private String buildHomeCallbackUrl(HttpServletRequest request, HttpServletResponse response, String value) {
    Cookie cookie = cookieService.findCookie(request, FRONT_CALLBACK_URL);
    cookieService.deleteCookie(request, response, FRONT_CALLBACK_URL, "/participe");
    String url = cookie.getValue();
    if(url.contains("?")) {
      url = url.substring(0, url.indexOf('?'));
    }
    return url.concat("/#/home?signinDto=".concat(value));
  }

  private String buildProfileCallbackUrl(
    HttpServletRequest request,
    HttpServletResponse response,
    String value
  ) {
    Cookie cookie = cookieService.findCookie(request, FRONT_CALLBACK_URL);
    cookieService.deleteCookie(request, response, FRONT_CALLBACK_URL, "/participe");
    String url = cookie.getValue();
    if(url.contains("?")) {
      url = url.substring(0, url.indexOf('?'));
    }
    return url.concat("/#/profile?signinDto=".concat(value));
  }

  private Long getConferenceId(HttpServletRequest request, HttpServletResponse response) {
    if(request.getCookies() != null) {
      Optional<Cookie> filter = Arrays.stream(request.getCookies()).filter(
        c -> c.getName().equals(FRONT_CONFERENCE_ID)).findFirst();
      if(filter.isPresent()) {
        cookieService.deleteCookie(request, response, FRONT_CONFERENCE_ID, "/participe");
        return Long.valueOf(filter.get().getValue());
      }
    }
    return null;
  }

  private String encode(SigninDto signinDto) throws JsonProcessingException {
    return Base64.getEncoder().withoutPadding().encodeToString(
      new ObjectMapper().writeValueAsString(signinDto).getBytes(StandardCharsets.UTF_8));
  }

  private String encode(PersonProfileSignInDto personProfileSignInDto) throws JsonProcessingException {
    return Base64.getEncoder().withoutPadding().encodeToString(
      new ObjectMapper().writeValueAsString(personProfileSignInDto).getBytes(StandardCharsets.UTF_8));
  }
}
