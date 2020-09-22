package br.gov.es.participe.service;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import br.gov.es.participe.controller.dto.RelationshipAuthServiceAuxiliaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.util.ParticipeUtils;
import br.gov.es.participe.util.domain.TokenType;

@Service
@PropertySource(name = "social", value = "classpath:/social-cfg.properties")
public class FacebookService {

    private static final String SERVER = "Facebook";

    @Value("#{environment.getProperty('facebook.app.id')}")
    private String facebookId;

    @Value("#{environment.getProperty('facebook.app.secret')}")
    private String facebookSecret;

    @Value("#{environment.getProperty('facebook.scope')}")
    private String facebookScope;

    @Value("#{environment.getProperty('facebook.app.user.fields')}")
    private String[] facebookFields;

    @Autowired
    private ParticipeUtils participeUtils;

    @Autowired
    private PersonService personService;

    @Autowired
    private TokenService tokenService;

    public String facebookAcessToken(String authorizationCode, HttpServletRequest request) {
        return createFacebookConnection().getOAuthOperations().exchangeForAccess(
                authorizationCode,
                participeUtils.getServerBaseUrl(request).concat("/signin/facebook"),
                null
        ).getAccessToken();
    }

    public SigninDto authenticate(String accessToken, Long conferenceId) {
        Person person = findOrCreatePerson(accessToken, conferenceId);
        String authenticationToken = tokenService.generateToken(person, TokenType.AUTHENTICATION);
        String refreshToken = tokenService.generateToken(person, TokenType.REFRESH);

        return new SigninDto(person, authenticationToken, refreshToken);
    }

    private User facebookUserProfile(String accessToken) {
        Facebook facebook = new FacebookTemplate(accessToken);
        return facebook.fetchObject("me", User.class, facebookFields);
    }

    private FacebookConnectionFactory createFacebookConnection() {
        return new FacebookConnectionFactory(facebookId, facebookSecret);
    }

    private Person findOrCreatePerson(String accessToken, Long conferenceId) {
        User user = facebookUserProfile(accessToken);
        Optional<Person> findPerson = personService.findByContactEmail(user.getEmail());
        if (findPerson.isPresent()) {
            Person person = findPerson.get();
            person.setAccessToken(accessToken);
            return personService.createRelationshipWithAuthService(new RelationshipAuthServiceAuxiliaryDto
                    .RelationshipAuthServiceAuxiliaryDtoBuilder(person)
                    .server(SERVER)
                    .serverId(user.getId())
                    .conferenceId(conferenceId)
                    .resetPassword(false)
                    .makeLogin(true)
                    .build());
        }

        return createPerson(user, accessToken, conferenceId);
    }

    private Person createPerson(User user, String accessToken, Long conferenceId) {
        Person person = new Person();
        person.setAccessToken(accessToken);
        person.setName(user.getName());
        person.setContactEmail(user.getEmail());
        
        return personService.createRelationshipWithAuthService(new RelationshipAuthServiceAuxiliaryDto
                .RelationshipAuthServiceAuxiliaryDtoBuilder(person)
                .server(SERVER)
                .serverId(user.getId())
                .conferenceId(conferenceId)
                .resetPassword(false)
                .makeLogin(true)
                .build());
    }
}
