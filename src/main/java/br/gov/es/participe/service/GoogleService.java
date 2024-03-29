package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.PersonProfileSignInDto;
import br.gov.es.participe.controller.dto.RelationshipAuthServiceAuxiliaryDto;
import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.util.domain.TokenType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class GoogleService {

  private static final String SERVER = "Google";

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String googleId;

  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String googleSecret;

  @Value("${spring.security.oauth2.client.registration.google.scope}")
  private String googleScope;

  @Value("${spring.security.oauth2.client.registration.google.user-info-uri}")
  private String googleUserInfoUri;

  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  private String googleRedirectUri;

  @Value("${spring.security.oauth2.client.registration.google-profile.redirect-uri}")
  private String googleProfileRedirectUri;

  @Autowired
  private PersonService personService;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private ObjectMapper objectMapper;

  public String googleAccessToken(String authorizationCode, HttpServletRequest request, String url) {
    return createGoogleConnectionFactory().getOAuthOperations().exchangeForAccess(
      authorizationCode,
      googleRedirectUri,
      null
    ).getAccessToken();
  }

  public String googleProfileAccessToken(String authorizationCode) {
    return createGoogleConnectionFactory().getOAuthOperations().exchangeForAccess(
        authorizationCode,
        googleProfileRedirectUri,
        null
    ).getAccessToken();
  }

  public PersonProfileSignInDto authenticateProfile(
    String accessToken,
    Long conferenceId
  ) {

    UserInfo user = googleUserProfile(accessToken);

    Optional<Person> personAlreadyUsingSocialLogin = this.personService.havePersonWithLoginEmail(user.email, SERVER, null);

    if(personAlreadyUsingSocialLogin.isPresent()) {
      return new PersonProfileSignInDto(
        personAlreadyUsingSocialLogin.get(),
        SERVER,
        user.getEmail(),
        user.getId(),
        "oauth",
         true,
        personAlreadyUsingSocialLogin.get().getId()
      );
    }

    Person person = findOrCreatePerson(user, accessToken, conferenceId, false);

    return new PersonProfileSignInDto(
      person,
      SERVER,
      user.getEmail(),
      user.getId(),
      "oauth"
    );
  }

  public SigninDto authenticate(
    String accessToken,
    Long conferenceId
  ) {
    UserInfo user = googleUserProfile(accessToken);
    Person person = findOrCreatePerson(user, accessToken, conferenceId, true);
    String authenticationToken = tokenService.generateToken(person, TokenType.AUTHENTICATION);
    String refreshToken = tokenService.generateToken(person, TokenType.REFRESH);

    return new SigninDto(person, SERVER, authenticationToken, refreshToken);
  }

  private UserInfo googleUserProfile(String accessToken) {
    String token = "Bearer " + accessToken;
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", token);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    RestTemplate restTemplate = new RestTemplate();
    String strUserInfo = restTemplate.exchange(googleUserInfoUri, HttpMethod.GET, entity,
                                               String.class
    ).getBody();
    UserInfo userInfo = null;
    try {
      JsonNode usr = objectMapper.readTree(strUserInfo);
      userInfo = new UserInfo();
      userInfo.setId(usr.get("id").asText());
      userInfo.setEmail(usr.get("email").asText());
      userInfo.setName(usr.get("name").asText());
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    return userInfo;
  }

  private GoogleConnectionFactory createGoogleConnectionFactory() {
    return new GoogleConnectionFactory(googleId, googleSecret);
  }

  private Person findOrCreatePerson(
    UserInfo user,
    String accessToken,
    Long conferenceId,
    boolean persistRelationship
  ) {
    Optional<Person> findPerson = personService.findByLoginEmail(user.getEmail());

    if(findPerson.isPresent()) {
      Person person = findPerson.get();
      person.setAccessToken(accessToken);
      return makeAuthServiceRelationship(user, conferenceId, persistRelationship, person);
    }
    return createPerson(user, accessToken, conferenceId, persistRelationship);
  }

  private Person createPerson(UserInfo user, String accessToken, Long conferenceId, boolean persistRelationship) {
    Person person = new Person();
    person.setAccessToken(accessToken);
    person.setName(user.getName());
    person.setContactEmail(user.getEmail());
    return makeAuthServiceRelationship(user, conferenceId, persistRelationship, person);
  }

  private Person makeAuthServiceRelationship(UserInfo user, Long conferenceId, boolean persistRelationship, Person person) {
    if(persistRelationship) {
      return personService.createRelationshipWithAuthService(
        new RelationshipAuthServiceAuxiliaryDto
          .RelationshipAuthServiceAuxiliaryDtoBuilder(person)
          .server(SERVER)
          .serverId(user.getId())
          .conferenceId(conferenceId)
          .resetPassword(false)
          .makeLogin(true)
          .build()
      );
    }
    else {
      return personService.createRelationshipWithAuthServiceWithoutPersist(
        new RelationshipAuthServiceAuxiliaryDto
          .RelationshipAuthServiceAuxiliaryDtoBuilder(person)
          .server(SERVER)
          .serverId(user.getId())
          .conferenceId(conferenceId)
          .resetPassword(false)
          .makeLogin(true)
          .build()
      );
    }
  }

  class UserInfo {
    private String name;
    private String email;
    private String id;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }
}
