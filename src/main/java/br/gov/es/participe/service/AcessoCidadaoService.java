package br.gov.es.participe.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.util.ParticipeUtils;
import br.gov.es.participe.util.domain.ProfileType;
import br.gov.es.participe.util.domain.TokenType;

@Service
public class AcessoCidadaoService {

    private static final String SERVER = "AcessoCidadao";

    @Value("${spring.security.oauth2.client.provider.idsvr.issuer-uri}")
    private String issuerUri;

    @Value("${api.acessocidadao.client-id}")
    private String clientId;

    @Value("${api.acessocidadao.client-secret}")
    private String clientSecret;

    @Value("${api.acessocidadao.uri.webapi}")
    private String acessocidadaoUriWebApi;

    @Value("${api.acessocidadao.uri.token}")
    private String acessocidadaoUriToken;

    @Value("${api.acessocidadao.administrator.profile.id}")
    private String administratorProfileId;

    @Value("${api.acessocidadao.moderator.profile.id}")
    private String moderatorProfileId;

    @Value("${api.acessocidadao.recepcionist.profile.id}")
    private String recepcionistProfileId;

    @Value("${api.acessocidadao.grant_type}")
    private String grantType;

    @Value("${api.acessocidadao.scope}")
    private String scopes;

    @Value("${spring.security.oauth2.client.registration.idsvr.client-name}")
    private String system;

    @Autowired
    private PersonService personService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ParticipeUtils participeUtils;

    public SigninDto authenticate(String token, Long conferenceId) throws Exception {
        Person person = findOrCreatePerson(token, conferenceId);
        String authenticationToken = tokenService.generateToken(person, TokenType.AUTHENTICATION);
        String refreshToken = tokenService.generateToken(person, TokenType.REFRESH);

        return new SigninDto(person, authenticationToken, refreshToken);
    }

    public SigninDto refresh(String refreshToken) {
        Long personId = tokenService.getPersonId(refreshToken, TokenType.REFRESH);
        Person person = personService.find(personId);

        String authenticationToken = tokenService.generateToken(person, TokenType.AUTHENTICATION);
        String newRefreshToken = tokenService.generateToken(person, TokenType.REFRESH);

        return new SigninDto(person, authenticationToken, newRefreshToken);
    }

    private Person findOrCreatePerson(String token, Long conferenceId) throws Exception {
        JSONObject userInfo = getUserInfo(token);
        String email = null;
        if (userInfo.isNull("email")) {
    		email = findUserEmailInAcessoCidadao(userInfo.getString("subNovo"));
    	}else {
    		email = userInfo.getString("email");
    	}
        Optional<Person> findPerson = personService.findByContactEmail(email);
        		
        if (findPerson.isPresent()) {
            Person person = findPerson.get();
            person.setAccessToken(token);
            if (person.getContactEmail() == null) {
            	person.setContactEmail(email);
            }
            person.setRoles(new HashSet<>());
            if(!userInfo.isNull("role")) {
            	if (userInfo.get("role").toString().contains("[")) {
            		userInfo.getJSONArray("role").forEach(role -> {
            			person.getRoles().add((String) role);
            		});            		
            	} else {
            		person.getRoles().add(userInfo.getString("role"));
            	}
            } else {
            	person.setRoles(findAllRoles(userInfo.getString("subNovo")));
            }
            return personService.createRelationshipWithAthService(person, null, SERVER, userInfo.getString("subNovo"), conferenceId);
        }
        userInfo.append("accessToken", token);

        return createPerson(userInfo, conferenceId);
    }

    private Person createPerson(JSONObject userInfo, Long conferenceId) throws Exception {
        Person person = new Person();
        person.setName(userInfo.getString("apelido"));
        person.setAccessToken(userInfo.get("accessToken").toString());
        if(!userInfo.isNull("role")) {
        	if (userInfo.get("role").toString().contains("[")) {
        		userInfo.getJSONArray("role").forEach(role -> {
        			person.getRoles().add((String) role);
        		});            		
        	} else {
        		person.getRoles().add(userInfo.getString("role"));
        	}
        } else {
        	person.setRoles(findAllRoles(userInfo.getString("subNovo")));
        }
        if (userInfo.isNull("email")) {
        	person.setContactEmail(findUserEmailInAcessoCidadao(userInfo.getString("subNovo")));
    	}else {
    		person.setContactEmail(userInfo.getString("email"));
    	}
        return personService.createRelationshipWithAthService(person, null, SERVER, userInfo.getString("subNovo"), conferenceId);
    }

    private JSONObject getUserInfo(String token) throws Exception {
        String userInfoUri = issuerUri + "/connect/userinfo";
        HttpPost postRequest = new HttpPost(userInfoUri);
        postRequest.addHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                return new JSONObject(EntityUtils.toString(response.getEntity()));
            }
        }

        throw new IllegalArgumentException();
    }

    public List<PersonDto> listPersonsByPerfil(ProfileType profileType, String name, String email) throws Exception {
        List<PersonDto> personList = new ArrayList<>();
        String token = getClientToken();
        if (token != null) {
            String uri = acessocidadaoUriWebApi.concat("sistema/").concat(system);
            if (profileType != null) {
                uri = uri.concat("/perfil/").concat(getProfileId(profileType));

            }
            uri = uri.concat("/usuarios");
            HttpGet get = new HttpGet(uri);
            get.addHeader("Authorization", "Bearer " + token);
            try (CloseableHttpResponse response = HttpClients.createDefault().execute(get)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    JSONArray result = new JSONArray(EntityUtils.toString(response.getEntity()));
                    System.out.println(result);
                    List<PersonDto> finalPersonList = personList;
                    result.forEach(element -> {
                        if (element instanceof JSONObject) {
                            JSONObject obj = (JSONObject) element;
                            PersonDto person = new PersonDto();
                            person.setName(obj.getString("Nome"));
                            person.setContactEmail(obj.getString("Email"));
                            finalPersonList.add(person);
                        }
                    });
                }
            }
        }

        if (!personList.isEmpty()) {
            personList.removeIf(
                    p -> !participeUtils.normalize(p.getName()).contains(participeUtils.normalize(name))
                            || !participeUtils.normalize(p.getContactEmail()).contains(participeUtils.normalize(email))
            );
        }

        return personList;
    }

    private Set<String> findAllRoles(String sub) throws ClientProtocolException, IOException {
    	String token = getClientToken();
    	Set<String> roles = new HashSet<>();
        if (token != null) {
        	for (ProfileType profileType : ProfileType.values()) {
        		String uri = acessocidadaoUriWebApi.concat("sistema/")
        				.concat(system)
        				.concat("/perfil/")
        				.concat(getProfileId(profileType))
        				.concat("/usuarios");
        		HttpGet get = new HttpGet(uri);
        		get.addHeader("Authorization", "Bearer " + token);
        		try (CloseableHttpResponse response = HttpClients.createDefault().execute(get)) {
        			if (response.getStatusLine().getStatusCode() == 200) {
        				JSONArray result = new JSONArray(EntityUtils.toString(response.getEntity()));
        				result.forEach(element -> {
                            if (element instanceof JSONObject) {
                            	if (((JSONObject) element).getString("Sub").equals(sub) ) {
                            		roles.add(getProfileName(profileType));                            		
                            	}
                            }
        				});
        				
        			}
        		}
        		
        	}
        	
        }

        return roles;
    }
    
    private String findUserEmailInAcessoCidadao(String sub) throws Exception {
        String token = getClientToken();
        if (token != null) {
            String uri = acessocidadaoUriWebApi.concat("cidadao/").concat(sub).concat("/email");
            HttpGet get = new HttpGet(uri);
            get.addHeader("Authorization", "Bearer " + token);
            try (CloseableHttpResponse response = HttpClients.createDefault().execute(get)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    JSONObject result = new JSONObject(EntityUtils.toString(response.getEntity()));
                    return result.getString("email");
                }
            }
        }

        return null;
    }

    private String getClientToken() throws ClientProtocolException, IOException {
        String basicToken = clientId + ":" + clientSecret;
        HttpPost postRequest = new HttpPost(acessocidadaoUriToken);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", grantType));
        urlParameters.add(new BasicNameValuePair("scope", scopes));
        postRequest.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(basicToken.getBytes()));

        postRequest.setEntity(new UrlEncodedFormEntity(urlParameters, Consts.UTF_8));
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                JSONObject result = new JSONObject(EntityUtils.toString(response.getEntity()));
                return result.getString("access_token");
            }
        }

        return null;
    }

    private String getProfileId(ProfileType profileType) {
        switch (profileType) {
            case ADMINISTRATOR:
                return administratorProfileId;
            case MODERATOR:
                return moderatorProfileId;
            case RECEPCIONIST:
                return recepcionistProfileId;
            default:
                return null;

        }
    }
    
    private String getProfileName(ProfileType profileType) {
        switch (profileType) {
            case ADMINISTRATOR:
                return "Administrator";
            case MODERATOR:
                return "Moderator";
            case RECEPCIONIST:
                return "Recepcionist";
            default:
                return null;

        }
    }

}
