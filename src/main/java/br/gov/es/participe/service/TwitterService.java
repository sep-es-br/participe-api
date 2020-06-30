package br.gov.es.participe.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.util.ParticipeUtils;
import br.gov.es.participe.util.domain.TokenType;

@Service
@PropertySource(name = "social", value = "classpath:/social-cfg.properties")
public class TwitterService {

    private static final String SERVER = "Twitter";

    @Value("#{environment.getProperty('twitter.consumer.key')}")
    private String twitterKey;

    @Value("#{environment.getProperty('twitter.consumer.secret')}")
    private String twitterSecret;

    @Value("#{environment.getProperty('twitter.token.uri')}")
    private String tokenUri;

    @Value("#{environment.getProperty('twitter.user.email.uri')}")
    private String userEmailUri;

    @Value("#{environment.getProperty('twitter.access.token.uri')}")
    private String accessTokenUri;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PersonService personService;

    @Autowired
    private ParticipeUtils participeUtils;

    public Map<String, Object> oauthAuthorizeParams(String redicrectUri) {
        Map<String, Object> params = new HashMap<>();
        OAuthToken oAuthToken = generateOauthToken(redicrectUri);

        params.put("oauth_token", oAuthToken.getValue());
        params.put("oauth_token_secret", oAuthToken.getSecret());

        return params;
    }

    public String oauthTokenAndSecret(HttpServletRequest request, String oauthToken, String oauthVerifier) throws MalformedURLException {
        RestTemplate restTemplate = participeUtils.htmlRestTemplate();
        URI targetUrl = UriComponentsBuilder.fromUriString(tokenUri)
                .queryParam("oauth_token", oauthToken)
                .queryParam("oauth_verifier", oauthVerifier)
                .build()
                .toUri();
        ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl.toURL().toString(), HttpMethod.POST, participeUtils.htmlEntityResponse(), String.class);
        HashMap<String, String> result = participeUtils.convertQueryStringToHashMap(responseEntity.getBody());
        String url = participeUtils.getServerBaseUrl(request).concat("/signin/twitter-response?").concat(responseEntity.getBody());

        return url;
    }

    public SigninDto authenticate(String oauthToken, String oauthTokenSecret, String userId, String screenName, Long conferenceId) {
        Person person = findOrCreatePerson(oauthToken, oauthTokenSecret, conferenceId);
        String authenticationToken = tokenService.generateToken(person, TokenType.AUTHENTICATION);
        String refreshToken = tokenService.generateToken(person, TokenType.REFRESH);

        return new SigninDto(person, authenticationToken, refreshToken);
    }

    private TwitterConnectionFactory createTwitterConnection() {
        return new TwitterConnectionFactory(twitterKey, twitterSecret);
    }

    private OAuthToken generateOauthToken(String redicrectUri) {
        OAuth1Parameters parameters = new OAuth1Parameters();
        parameters.set("x_auth_access_type", "read/write");
        return createTwitterConnection().getOAuthOperations().fetchRequestToken(redicrectUri, parameters);
    }

    private Person findOrCreatePerson(String oauthToken, String oauthTokenSecret, Long conferenceId) {
        TwitterProfile profile = twitterProfile(oauthToken, oauthTokenSecret);
        Optional<Person> findPerson = personService.findByContactEmail(profile.getExtraData().get("email").toString());
        String accessToken = genereateAccessToken(oauthToken, oauthTokenSecret);

        if (findPerson.isPresent()) {
            Person person = findPerson.get();
            person.setAccessToken(accessToken);
            return personService.createRelationshipWithAthService(person, null, SERVER, profile.getExtraData().get("email").toString(), conferenceId);
        }

        return createPerson(profile, accessToken, conferenceId);
    }

    private Person createPerson(TwitterProfile profile, String accessTokenUri, Long conferenceId) {
        Person person = new Person();
        person.setAccessToken(accessTokenUri);
        person.setName(profile.getName());
        person.setContactEmail(profile.getExtraData().get("email").toString());
        return personService.createRelationshipWithAthService(person, null, SERVER, profile.getExtraData().get("email").toString(), conferenceId);
    }

    private TwitterProfile twitterProfile(String token, String secret) {
        TwitterTemplate template = new TwitterTemplate(twitterKey, twitterSecret, token, secret);
        RestTemplate restTemplate = template.getRestTemplate();

        return restTemplate.getForObject(userEmailUri, TwitterProfile.class);
    }

    private String genereateAccessToken(String token, String secret) {
        TwitterTemplate template = new TwitterTemplate(twitterKey, twitterSecret, token, secret);
        RestTemplate restTemplate = template.getRestTemplate();
        String code = twitterKey.concat(":").concat(twitterSecret);
        String authorization = Base64.getEncoder().encodeToString(code.getBytes());
        authorization = "Basic ".concat(authorization);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);

        JSONObject object = new JSONObject(restTemplate.exchange(accessTokenUri, HttpMethod.POST, entity, String.class).getBody());

        return object.get("access_token").toString();
    }

}
