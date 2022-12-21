package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.PersonProfileSignInDto;
import br.gov.es.participe.controller.dto.RelationshipAuthServiceAuxiliaryDto;
import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.util.domain.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class FacebookService {

  private static final String SERVER = "Facebook";

  @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
  private String facebookId;

  @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
  private String facebookSecret;

  @Value("${spring.security.oauth2.client.registration.facebook.scope}")
  private String facebookScope;

  @Value("${spring.security.oauth2.client.registration.facebook.user-fields}")
  private String[] facebookFields;

  @Autowired
  private PersonService personService;

  @Autowired
  private TokenService tokenService;

  @Value("${spring.security.oauth2.client.registration.facebook.redirect-uri}")
  private String facebookRedirectUri;

  @Value("${spring.security.oauth2.client.registration.facebook-profile.redirect-uri}")
  private String facebookProfileRedirectUri;

  public String facebookAccessToken(
    String authorizationCode,
    HttpServletRequest request,
    String url
  ) {
    return createFacebookConnection().getOAuthOperations().exchangeForAccess(
      authorizationCode,
      facebookRedirectUri,
      null
    ).getAccessToken();
  }

  public String facebookProfileAccessToken(
      String authorizationCode
  ) {
    return createFacebookConnection().getOAuthOperations().exchangeForAccess(
        authorizationCode,
        facebookProfileRedirectUri,
        null
    ).getAccessToken();
  }

  public SigninDto authenticate(String accessToken, Long conferenceId) {
    Person person = findOrCreatePerson(accessToken, conferenceId, true);
    String authenticationToken = tokenService.generateToken(person, TokenType.AUTHENTICATION);
    String refreshToken = tokenService.generateToken(person, TokenType.REFRESH);

    return new SigninDto(person, SERVER, authenticationToken, refreshToken);
  }

  private User facebookUserProfile(String accessToken) {
    Facebook facebook = new FacebookTemplate(accessToken);
    return facebook.fetchObject("me", User.class, facebookFields);
  }

  private FacebookConnectionFactory createFacebookConnection() {
    return new FacebookConnectionFactory(facebookId, facebookSecret);
  }

  private Person findOrCreatePerson(String accessToken, Long conferenceId,
                                    boolean persistRelationship) {
    User user = facebookUserProfile(accessToken);

    Optional<Person> findPerson = personService.findByLoginEmail(user.getEmail());
    if(findPerson.isPresent()) {
      Person person = findPerson.get();
      person.setAccessToken(accessToken);
      return makeAuthServiceRelationship(user, conferenceId, persistRelationship, person);
    }

    return createPerson(user, accessToken, conferenceId, persistRelationship);
  }

  private Person createPerson(User user, String accessToken, Long conferenceId,
                              boolean persistRelationship) {
    Person person = new Person();
    person.setAccessToken(accessToken);
    person.setName(user.getName());
    person.setContactEmail(user.getEmail());

    return makeAuthServiceRelationship(user, conferenceId, persistRelationship, person);
  }

  private Person makeAuthServiceRelationship(User user, Long conferenceId,
                                             boolean persistRelationship, Person person) {
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

  public PersonProfileSignInDto authenticateProfile(String accessToken, Long conferenceId) {

    User user = this.facebookUserProfile(accessToken);

    Optional<Person> personAlreadyUsingSocialLogin = this.personService.havePersonWithLoginEmail(user.getEmail(), SERVER, null);

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

    Person person = findOrCreatePerson(accessToken, conferenceId, false);

    return new PersonProfileSignInDto(
      person,
      SERVER,
      user.getEmail(),
      user.getId(),
      "oauth"
    );
  }
}
