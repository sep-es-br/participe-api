
package br.gov.es.participe.service;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import br.gov.es.participe.controller.dto.RelationshipAuthServiceAuxiliaryDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.util.ParticipeUtils;
import br.gov.es.participe.util.domain.TokenType;

@Service
@PropertySource(name = "social", value = "classpath:/social-cfg.properties")
public class GoogleService {

    private static final String SERVER = "Google";

    @Value("#{environment.getProperty('google.client.id')}")
    private String googleId;

    @Value("#{environment.getProperty('google.client.secret')}")
    private String googleSecret;

    @Value("#{environment.getProperty('google.scope')}")
    private String googleScope;

    @Value("#{environment.getProperty('google.client.user.info.uri')}")
    private String googleUserInfoUri;

    @Autowired
    private ParticipeUtils participeUtils;

    @Autowired
    private PersonService personService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GoogleProperties googleProperties;

    public String googleAcessToken(String authorizationCode, HttpServletRequest request) {
  
       System.out.println("code:"+authorizationCode);
    	
       System.out.println("uri:"+ participeUtils.getServerBaseUrl(request).concat("/signin/google"));
       
       System.out.println("googleuri:"+ googleProperties.getRedirecturi());
       
    	return createGoogleConnectionFactory().getOAuthOperations().exchangeForAccess(
                authorizationCode,
                googleProperties.getRedirecturi()  /*"https://hom.orcamento.es.gov.br/participe/signin/google" participeUtils.getServerBaseUrl(request).concat("/signin/google")*/,
                null
        ).getAccessToken();
    	
    }

    public SigninDto authenticate(String accessToken, Long conferenceId) {
        Person person = findOrCreatePerson(accessToken, conferenceId);
        String authenticationToken = tokenService.generateToken(person, TokenType.AUTHENTICATION);
        String refreshToken = tokenService.generateToken(person, TokenType.REFRESH);

        return new SigninDto(person, authenticationToken, refreshToken);
    }

    private UserInfo googleUserProfile(String accessToken) {
        String token = "Bearer " + accessToken;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity entity = new HttpEntity(headers);

        RestTemplate restTemplate = new RestTemplate();
        String strUserInfo = restTemplate.exchange(googleUserInfoUri, HttpMethod.GET, entity, String.class).getBody();
        UserInfo userInfo = null;
        try {
            JsonNode usr = objectMapper.readTree(strUserInfo);
            userInfo = new UserInfo();
            userInfo.setId(usr.get("id").asText());
            userInfo.setEmail(usr.get("email").asText());
            userInfo.setName(usr.get("name").asText());
        }catch (Exception e) {
            e.printStackTrace();
        }

        return userInfo;
    }

    private GoogleConnectionFactory createGoogleConnectionFactory() {
        return new GoogleConnectionFactory(googleId, googleSecret);
    }

    private Person findOrCreatePerson(String accessToken, Long conferenceId) {
        UserInfo user = googleUserProfile(accessToken);
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

    private Person createPerson(UserInfo user, String accessToken, Long conferenceId) {
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
