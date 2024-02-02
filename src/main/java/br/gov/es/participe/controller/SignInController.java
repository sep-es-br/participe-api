package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.PersonProfileSignInDto;
import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.service.AcessoCidadaoService;
import br.gov.es.participe.service.CookieService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.util.dto.MessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
public class SignInController {

  @Autowired
  private AcessoCidadaoService acessoCidadaoService;

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

  @Transactional
  @PostMapping
  @SuppressWarnings("rawtypes")
  public ResponseEntity signIn(@RequestBody PersonParamDto user,
      @RequestParam(name = "conference") Long conference) {
    SigninDto signinDto = personService.authenticate(user, "Participe", conference);

    if (signinDto != null) {
      if (signinDto.getPerson().getActive() != null && !signinDto.getPerson().getActive()) {
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
      HttpServletResponse response) throws IOException {
    SigninDto signinDto = acessoCidadaoService.authenticate(token,
        getConferenceId(request, response));
    String valor = encode(signinDto);
    return new RedirectView(buildHomeCallbackUrl(request, response, valor));
  }

  @GetMapping("/acesso-cidadao-profile-response")
  public RedirectView acessoCidadaoProfile(
      @RequestParam(name = "access_token") String token,
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    PersonProfileSignInDto persoProfileSignInDto = acessoCidadaoService.authenticateProfile(
        token,
        getConferenceId(request, response));
    String valor = encode(persoProfileSignInDto);
    return new RedirectView(buildProfileCallbackUrl(request, response, valor));
  }

  private String buildHomeCallbackUrl(HttpServletRequest request, HttpServletResponse response, String value) {
    Cookie cookie = cookieService.findCookie(request, FRONT_CALLBACK_URL);
    cookieService.deleteCookie(request, response, FRONT_CALLBACK_URL, "/participe");
    String url = cookie.getValue();
    if (url.contains("?")) {
      url = url.substring(0, url.indexOf('?'));
    }
    return url.concat("/#/home?signinDto=".concat(value));
  }

  private String buildProfileCallbackUrl(
      HttpServletRequest request,
      HttpServletResponse response,
      String value) {
    Cookie cookie = cookieService.findCookie(request, FRONT_CALLBACK_URL);
    cookieService.deleteCookie(request, response, FRONT_CALLBACK_URL, "/participe");
    String url = cookie.getValue();
    if (url.contains("?")) {
      url = url.substring(0, url.indexOf('?'));
    }
    return url.concat("/#/profile?signinDto=".concat(value));
  }

  private Long getConferenceId(HttpServletRequest request, HttpServletResponse response) {
    if (request.getCookies() != null) {
      Optional<Cookie> filter = Arrays.stream(request.getCookies()).filter(
          c -> c.getName().equals(FRONT_CONFERENCE_ID)).findFirst();
      if (filter.isPresent()) {
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
