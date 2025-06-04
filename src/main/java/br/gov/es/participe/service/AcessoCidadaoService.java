package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.ChildOrganizationsDto;
import br.gov.es.participe.controller.dto.EvaluatorOrganizationDto;
import br.gov.es.participe.controller.dto.EvaluatorSectionDto;
import br.gov.es.participe.controller.dto.EvaluatorRoleDto;
import br.gov.es.participe.controller.dto.OrganizationUnitsDto;
import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.PersonProfileSignInDto;
import br.gov.es.participe.controller.dto.PublicAgentDto;
import br.gov.es.participe.controller.dto.RelationshipAuthServiceAuxiliaryDto;
import br.gov.es.participe.controller.dto.SigninDto;
import br.gov.es.participe.controller.dto.UnitRolesDto;
import br.gov.es.participe.exception.ApiAcessoCidadaoException;
import br.gov.es.participe.exception.ApiOrganogramaException;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.util.ParticipeUtils;
import br.gov.es.participe.util.domain.ProfileType;
import br.gov.es.participe.util.domain.TokenType;
import br.gov.es.participe.util.dto.acessoCidadao.AcOrganizationInfoDto;
import br.gov.es.participe.util.dto.acessoCidadao.AcSectionInfoDto;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AcessoCidadaoService {

  public static final String SERVER = "AcessoCidadao";
  private static final String FIELD_EMAIL = "email";
  private static final String FIELD_SUB_NOVO = "subNovo";
  private static final String FIELD_ROLE = "role";
  private static final String AUTHORIZATION = "Authorization";
  private static final String BEARER = "Bearer ";
  private static final String GUID_GOVES = "fe88eb2a-a1f3-4cb1-a684-87317baf5a57";
  public static final String STATUS = "Status: ";

  @Value("${spring.security.oauth2.client.provider.idsvr.issuer-uri}")
  private String issuerUri;

  @Value("${api.acessocidadao.client-id}")
  private String clientId;

  @Value("${api.acessocidadao.client-secret}")
  private String clientSecret;

  @Value("${api.acessocidadao.uri.webapi}")
  private String acessocidadaoUriWebApi;

  @Value("${api.organograma.uri.webapi}")
  private String organogramaUriWebapi;

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

  private final Logger logger = LogManager.getLogger(AcessoCidadaoService.class);

  ObjectMapper mapper = new ObjectMapper();

  public SigninDto authenticate(String token, Long conferenceId) throws IOException {
    Person person = findOrCreatePerson(token, conferenceId, true);
    String authenticationToken = tokenService.generateToken(person, TokenType.AUTHENTICATION);
    String refreshToken = tokenService.generateToken(person, TokenType.REFRESH);

    return new SigninDto(person, SERVER, authenticationToken, refreshToken);
  }

  public SigninDto refresh(String refreshToken) {
    Long personId = tokenService.getPersonId(refreshToken, TokenType.REFRESH);
    Person person = personService.find(personId);

    String authenticationToken = tokenService.generateToken(person, TokenType.AUTHENTICATION);
    String newRefreshToken = tokenService.generateToken(person, TokenType.REFRESH);

    return new SigninDto(person, SERVER, authenticationToken, newRefreshToken);
  }

  private Person findOrCreatePerson(String token, Long conferenceId, boolean persistRelationship) throws IOException {
    JSONObject userInfo = getUserInfo(token);
    String email;
    if (userInfo.isNull(FIELD_EMAIL)) {
      email = findUserEmailInAcessoCidadao(userInfo.getString(FIELD_SUB_NOVO));
    } else {
      email = userInfo.getString(FIELD_EMAIL);
    }
    Optional<Person> findPerson = personService.findByLoginEmail(email);

    if (findPerson.isPresent()) {
      Person person = findPerson.get();
      person.setAccessToken(token);
      if (person.getContactEmail() == null) {
        person.setContactEmail(email);
      }
      person.setRoles(getRoles(userInfo));

      return makeAuthServiceRelationship(conferenceId, persistRelationship, userInfo, person);
    }
    userInfo.append("accessToken", token);

    return createPerson(userInfo, conferenceId, persistRelationship);
  }

  private Person makeAuthServiceRelationship(Long conferenceId, boolean persistRelationship, JSONObject userInfo,
      Person person) {
    if (persistRelationship) {
      return personService.createRelationshipWithAuthService(
          new RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder(person)
              .server(SERVER)
              .serverId(userInfo.getString(FIELD_SUB_NOVO))
              .conferenceId(conferenceId)
              .resetPassword(false)
              .makeLogin(true)
              .build());
    } else {
      return personService.createRelationshipWithAuthServiceWithoutPersist(
          new RelationshipAuthServiceAuxiliaryDto.RelationshipAuthServiceAuxiliaryDtoBuilder(person)
              .server(SERVER)
              .serverId(userInfo.getString(FIELD_SUB_NOVO))
              .conferenceId(conferenceId)
              .resetPassword(false)
              .makeLogin(true)
              .build());
    }
  }

  private Set<String> getRoles(JSONObject userInfo) throws IOException {
    Set<String> roles = new HashSet<>();
    if (!userInfo.isNull(FIELD_ROLE)) {
      if (userInfo.get(FIELD_ROLE).toString().contains("[")) {
        userInfo.getJSONArray(FIELD_ROLE).forEach(role -> roles.add((String) role));
      } else {
        roles.add(userInfo.getString(FIELD_ROLE));
      }
    } else {
      return findAllRoles(userInfo.getString(FIELD_SUB_NOVO));
    }
    return roles;
  }

  private Person createPerson(JSONObject userInfo, Long conferenceId, boolean persistRelationship) throws IOException {
    Person person = new Person();
    person.setName(userInfo.getString("apelido"));
    person.setAccessToken(userInfo.get("accessToken").toString());
    if (!userInfo.isNull(FIELD_ROLE)) {
      if (userInfo.get(FIELD_ROLE).toString().contains("[")) {
        userInfo.getJSONArray(FIELD_ROLE).forEach(role -> person.getRoles().add((String) role));
      } else {
        person.getRoles().add(userInfo.getString(FIELD_ROLE));
      }
    } else {
      person.setRoles(findAllRoles(userInfo.getString(FIELD_SUB_NOVO)));
    }
    if (userInfo.isNull(FIELD_EMAIL)) {
      person.setContactEmail(findUserEmailInAcessoCidadao(userInfo.getString(FIELD_SUB_NOVO)));
    } else {
      person.setContactEmail(userInfo.getString(FIELD_EMAIL));
    }
    return makeAuthServiceRelationship(conferenceId, persistRelationship, userInfo, person);
  }

  private JSONObject getUserInfo(String token) throws IOException {
    String userInfoUri = issuerUri + "/connect/userinfo";
    HttpPost postRequest = new HttpPost(userInfoUri);
    postRequest.addHeader(AUTHORIZATION, BEARER + token);

    try (CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
      if (response.getStatusLine().getStatusCode() == 200) {
        return new JSONObject(EntityUtils.toString(response.getEntity()));
      }
    }

    throw new IllegalArgumentException();
  }

  public List<PersonDto> listPersonsByPerfil(ProfileType profileType, String name, String email) throws IOException {
    List<PersonDto> personList = new ArrayList<>();
    String token = getClientToken();
    if (token != null) {
      String uri = acessocidadaoUriWebApi.concat("sistema/").concat(system);
      if (profileType != null) {
        uri = uri.concat("/perfil/").concat(getProfileId(profileType));

      }
      uri = uri.concat("/usuarios");
      HttpGet get = new HttpGet(uri);
      get.addHeader(AUTHORIZATION, BEARER + token);
      try (CloseableHttpResponse response = HttpClients.createDefault().execute(get)) {
        if (response.getStatusLine().getStatusCode() == 200) {
          JSONArray result = new JSONArray(EntityUtils.toString(response.getEntity()));
          result.forEach(element -> {
            if (element instanceof JSONObject) {
              JSONObject obj = (JSONObject) element;
              PersonDto person = new PersonDto();
              person.setSub(obj.getString("Sub"));
              person.setName(obj.getString("Nome"));
              person.setContactEmail(obj.getString("Email"));
              personList.add(person);
            }
          });
        }
      }
    }

    if (!personList.isEmpty()) {
      personList.removeIf(
          p -> !participeUtils.normalize(p.getName()).contains(participeUtils.normalize(name))
              || !participeUtils.normalize(p.getContactEmail()).contains(
                  participeUtils.normalize(email)));
    }

    return personList;
  }

  private Set<String> findAllRoles(String sub) throws IOException {
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
        get.addHeader(AUTHORIZATION, BEARER + token);
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(get)) {
          if (response.getStatusLine().getStatusCode() == 200) {
            JSONArray result = new JSONArray(EntityUtils.toString(response.getEntity()));
            result.forEach(element -> {
              if (element instanceof JSONObject && ((JSONObject) element).getString("Sub").equals(
                  sub)) {
                roles.add(getProfileName(profileType));
              }
            });

          }
        }

      }

    }

    return roles;
  }

  public String findUserEmailInAcessoCidadao(String sub) {
    String token = getClientToken();
    if (token != null) {
      String uri = acessocidadaoUriWebApi.concat("cidadao/").concat(sub).concat("/email");
      HttpGet get = new HttpGet(uri);
      get.addHeader(AUTHORIZATION, BEARER + token);
      try (CloseableHttpResponse response = HttpClients.createDefault().execute(get)) {
        if (response.getStatusLine().getStatusCode() == 200) {
          JSONObject result = new JSONObject(EntityUtils.toString(response.getEntity()));
          return result.getString(FIELD_EMAIL);
        }
      } catch (IOException ex) {
          throw new RuntimeException(ex);
      }
    }

    return null;
  }

  private String getClientToken() {
    String basicToken = clientId + ":" + clientSecret;
    HttpPost postRequest = new HttpPost(acessocidadaoUriToken);

    List<NameValuePair> urlParameters = new ArrayList<>();
    urlParameters.add(new BasicNameValuePair("grant_type", grantType));
    urlParameters.add(new BasicNameValuePair("scope", scopes));
    postRequest.addHeader(
        AUTHORIZATION,
        "Basic " + Base64.getEncoder().encodeToString(basicToken.getBytes()));

    postRequest.setEntity(new UrlEncodedFormEntity(urlParameters, Consts.UTF_8));
    try (CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
      if (response.getStatusLine().getStatusCode() == 200) {
        JSONObject result = new JSONObject(EntityUtils.toString(response.getEntity()));
        return result.getString("access_token");
      }
    } catch (IOException ex){
        throw new RuntimeException(ex);
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

  public PersonProfileSignInDto authenticateProfile(String token, Long conferenceId) throws IOException {

    JSONObject user = this.getUserInfo(token);

    Optional<Person> personAlreadyUsingSocialLogin = this.personService
        .havePersonWithLoginEmail(user.getString(FIELD_EMAIL), SERVER, null);

    if (personAlreadyUsingSocialLogin.isPresent()) {
      return new PersonProfileSignInDto(
          personAlreadyUsingSocialLogin.get(),
          SERVER,
          user.getString(FIELD_EMAIL),
          user.getString(FIELD_SUB_NOVO),
          "oauth",
          true,
          personAlreadyUsingSocialLogin.get().getId());
    }

    Person person = findOrCreatePerson(token, conferenceId, false);

    return new PersonProfileSignInDto(
        person,
        SERVER,
        user.getString(FIELD_EMAIL),
        user.getString(FIELD_SUB_NOVO),
        "oauth"

    );
  }

  public List<EvaluatorRoleDto> findRolesFromAcessoCidadaoAPI(String guid) throws IOException {
    String token = getClientToken();
    String url = acessocidadaoUriWebApi.concat("conjunto/" + guid + "/papeis");

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .header(AUTHORIZATION, BEARER + token)
        .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) { 
        List<EvaluatorRoleDto> evaluatorServerDtos = new ArrayList<>();
        List<UnitRolesDto> unitRolesDtos = mapper.readValue(response.body(), new TypeReference<List<UnitRolesDto>>() {
        });

        unitRolesDtos.iterator().forEachRemaining((role) -> {
          EvaluatorRoleDto newEvalServerDto = new EvaluatorRoleDto(role.getGuid(), (role.getAgentePublicoNome() + " - " + role.getNome()), guid);
          evaluatorServerDtos.add(newEvalServerDto);
        });

        return evaluatorServerDtos;
      } else {
        logger.error("Não foi possível buscar a lista de papéis da unidade.");
        throw new ApiAcessoCidadaoException(STATUS + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error(e.getMessage());
        throw new ApiAcessoCidadaoException("Erro ao buscar lista de papéis da unidade.");
    }
  }

  public List<EvaluatorSectionDto> findSectionsFromOrganogramaAPI(String guid) throws IOException {
    String token = getClientToken();
    String url = organogramaUriWebapi.concat("/unidades/organizacao/" + guid);

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .header(AUTHORIZATION, BEARER + token)
        .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        List<EvaluatorSectionDto> evaluatorSectionDtos = new ArrayList<>();
        List<OrganizationUnitsDto> organizationUnitsDtos = mapper.readValue(response.body(), new TypeReference<List<OrganizationUnitsDto>>() {
        });

        organizationUnitsDtos.iterator().forEachRemaining((unit) -> {
          EvaluatorSectionDto newEvalSectionDto = new EvaluatorSectionDto(unit.getGuid(), (unit.getNomeCurto() + " - " + unit.getNome()));
          evaluatorSectionDtos.add(newEvalSectionDto);
        });

        return evaluatorSectionDtos;

      } else {
        logger.error("Não foi possível buscar a lista de unidades da organização.");
        throw new ApiOrganogramaException(STATUS + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error(e.getMessage());
        throw new ApiOrganogramaException("Erro ao buscar lista de unidades da organização.");
    }
  }
  
  public AcSectionInfoDto findSectionInfoFromOrganogramaAPI(String guid) {
    if(guid == null) return null;
      
    String token = getClientToken();
    String url = String.format("%s/unidades/%s/info", organogramaUriWebapi, guid);
    

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .header(AUTHORIZATION, BEARER + token)
        .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        AcSectionInfoDto sectionDto = mapper.readValue(response.body(), new TypeReference<AcSectionInfoDto>() {});

        return sectionDto;

      } else {
        logger.error("Não foi possível buscar a lista de unidades da organização.");
        throw new ApiOrganogramaException(STATUS + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error(e.getMessage());
        throw new ApiOrganogramaException("Erro ao buscar lista de unidades da organização.");
    }
  }

  public AcOrganizationInfoDto findOrganizationInfoFromOrganogramaAPI(String guid) {
    String token = getClientToken();
    String url = String.format("%s/organizacoes/%s/info", organogramaUriWebapi, guid);
    
    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .header(AUTHORIZATION, BEARER + token)
        .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        AcOrganizationInfoDto organizationDto = mapper.readValue(response.body(), new TypeReference<AcOrganizationInfoDto>() {});

        return organizationDto;

      } else {
        logger.error("Não foi possível buscar a lista de unidades da organização.");
        throw new ApiOrganogramaException(STATUS + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error(e.getMessage());
        throw new ApiOrganogramaException("Erro ao buscar lista de unidades da organização.");
    }
  }

  public List<EvaluatorOrganizationDto> findOrganizationsFromOrganogramaAPI() throws IOException {
    String token = getClientToken();
    String url = organogramaUriWebapi.concat("/organizacoes/" + GUID_GOVES + "/filhas");

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .header(AUTHORIZATION, BEARER + token)
        .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        List<EvaluatorOrganizationDto> evaluatorOrganizationDto = new ArrayList<>();

        List<ChildOrganizationsDto> childOrganizationsDtos =  mapper.readValue(response.body(), new TypeReference<List<ChildOrganizationsDto>>() {
        });

        childOrganizationsDtos.iterator().forEachRemaining((childOrg) -> {
          EvaluatorOrganizationDto newEvalOrgDto = new EvaluatorOrganizationDto(childOrg.getGuid(), (childOrg.getNomeFantasia() + " - " + childOrg.getSigla()));
          evaluatorOrganizationDto.add(newEvalOrgDto);
        });

        return evaluatorOrganizationDto;
      } else {
        logger.error("Não foi possível buscar a lista de organizações (Filhas do GOVES).");
        throw new ApiOrganogramaException(STATUS + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error(e.getMessage());
        throw new ApiOrganogramaException("Erro ao buscar lista de organizações (Filhas do GOVES).");
    }
  }

  public List<PublicAgentDto> findPublicAgentsFromAcessoCidadaoAPI() {
    String token = null;

    try {
      token = getClientToken();
    } catch (RuntimeException e) {
      throw new ApiAcessoCidadaoException("Não foi possível resgatar o token.");
    }

    String url = acessocidadaoUriWebApi.concat("conjunto/" + GUID_GOVES + "/agentesPublicos");

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .header(AUTHORIZATION, BEARER + token)
        .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        List<PublicAgentDto> publicAgentDtos = mapper.readValue(response.body(),
            new TypeReference<List<PublicAgentDto>>() {
            });

        return publicAgentDtos;
      } else {
        logger.error("Não foi possível buscar os agentes publicos atrelado ao Guid GOVES.");
        throw new ApiAcessoCidadaoException(STATUS + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error(e.getMessage());
      throw new ApiAcessoCidadaoException("Não foi possível buscar os agentes publicos atrelado ao Guid GOVES.");
    }
  }

  public PublicAgentDto findTheAgentPublicSubByCpfInAcessoCidadaoAPI(String cpf) {
    String token = null;

    try {
      token = getClientToken();
    } catch (RuntimeException e) {
      throw new ApiAcessoCidadaoException("Não foi possível resgatar o token.");
    }

    String url = acessocidadaoUriWebApi.concat("agentepublico/" + cpf + "/sub");

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .header(AUTHORIZATION, BEARER + token)
        .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        PublicAgentDto publicAgentDtos = mapper.readValue(response.body(), new TypeReference<PublicAgentDto>() {
        });

        return publicAgentDtos;
      } else {
        return new PublicAgentDto();
      }
    } catch (IOException | InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error(e.getMessage());
      throw new ApiAcessoCidadaoException("Não foi possível buscar o sub do agente publicos atrelado ao CPF.");
    }
  }

  public PublicAgentDto findAgentPublicBySubInAcessoCidadaoAPI(String sub) {
    String token = null;

    try {
      token = getClientToken();
    } catch (RuntimeException e) {
      throw new ApiAcessoCidadaoException("Não foi possível resgatar o token.");
    }

    String url = acessocidadaoUriWebApi.concat("agentepublico/" + sub);

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .header(AUTHORIZATION, BEARER + token)
        .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        PublicAgentDto publicAgentDtos = mapper.readValue(response.body(), new TypeReference<PublicAgentDto>() {
        });

        return publicAgentDtos;
      } else {
        return null;
      }
    } catch (IOException | InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error(e.getMessage());
      throw new ApiAcessoCidadaoException("Não foi possível buscar o agente publicos atrelado a esse sub.");
    }
  }

  public PublicAgentDto findThePersonEmailBySubInAcessoCidadaoAPI(PublicAgentDto personDto) {
    String token = null;

    try {
      token = getClientToken();
    } catch (RuntimeException e) {
      throw new ApiAcessoCidadaoException("Não foi possível resgatar o token.");
    }

    String url = acessocidadaoUriWebApi.concat("cidadao/" + personDto.getSub() + "/email");

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .header(AUTHORIZATION, BEARER + token)
        .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        String responseBody = response.body();
        if (responseBody.contains("\"email\":null")) {
          return personDto;
        }
        PublicAgentDto PersonApiDto = mapper.readValue(response.body(), new TypeReference<PublicAgentDto>() {
        });
        personDto.setEmail(PersonApiDto.getEmail());
        return personDto;
      } else {
        logger.error("Não foi possível buscar o email do cidadão atrelado a esse sub.");
        throw new ApiAcessoCidadaoException("Não foi possível buscar o email do cidadão atrelado a esse sub.");
      }
    } catch (IOException | InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error(e.getMessage());
      throw new ApiAcessoCidadaoException("Não foi possível buscar o email do cidadão atrelado a esse sub.");
    }
  }

  public PublicAgentDto findSubFromPersonInAcessoCidadaoAPIByCpf(String cpf) {
    String token = null;

    try {
      token = getClientToken();
    } catch (RuntimeException e) {
      throw new ApiAcessoCidadaoException("Não foi possível resgatar o token.");
    }

    String urlString = acessocidadaoUriWebApi.concat("cidadao/" + cpf + "/pesquisaSub");

    try {
      URL url = new URL(urlString);

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("PUT");
      connection.setDoOutput(true);

      connection.setFixedLengthStreamingMode(0);
      connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("Authorization", "Bearer " + token);
      connection.connect();

      System.out.println("Response code: " + connection.getResponseCode());
      if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201 ) {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();

        String responseString = response.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        PublicAgentDto publicAgentDto = objectMapper.readValue(responseString, PublicAgentDto.class);

        return publicAgentDto;
      } else {
        logger.error("Não foi possível buscar o cidadão atrelado ao CPF.");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();

        String responseString = response.toString();
        throw new ApiAcessoCidadaoException(responseString);
      }
    } catch (IOException e) {
      Thread.currentThread().interrupt();
      logger.error(e.getMessage());
      throw new ApiAcessoCidadaoException("Não foi possível buscar o cidadão atrelado ao CPF.");
    }
  }


  public List<EvaluatorRoleDto> findRoleFromAcessoCidadaoAPIByAgentePublicoSub(String sub) {
    String token = null;

    try {
      token = getClientToken();
    } catch (RuntimeException e) {
      throw new ApiAcessoCidadaoException("Não foi possível resgatar o token.");
    }
    
    String url = acessocidadaoUriWebApi.concat("/agentepublico/" + sub + "/papeis");

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
      .header(AUTHORIZATION, BEARER + token)
      .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if(response.statusCode() == 200) {
        List<EvaluatorRoleDto> evaluatorRolesDto = new ArrayList<EvaluatorRoleDto>();

        List<UnitRolesDto> unitRolesDtos =  mapper.readValue(response.body(), new TypeReference<List<UnitRolesDto>>() {
        });

        unitRolesDtos.iterator().forEachRemaining((role) -> {
          EvaluatorRoleDto newEvalServerDto = new EvaluatorRoleDto(role.getGuid(), (role.getAgentePublicoNome() + " - " + role.getNome()), role.getLotacaoGuid().toLowerCase());
          evaluatorRolesDto.add(newEvalServerDto);
        });

        return evaluatorRolesDto;

      } else {
        logger.error("Não foi possível buscar o papel atrelado ao sub do agente.");
        throw new ApiAcessoCidadaoException(STATUS + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error(e.getMessage());
      throw new ApiAcessoCidadaoException("Erro ao buscar o papel atrelado ao sub do agente.");
    }
  }
  
  
  public UnitRolesDto findPriorityRoleFromAcessoCidadaoAPIBySub(String sub, boolean prioritario) {
    String token = null;

    try {
      token = getClientToken();
    } catch (RuntimeException e) {
      throw new ApiAcessoCidadaoException("Não foi possível resgatar o token.");
    }
    
    String url = acessocidadaoUriWebApi.concat("/agentepublico/" + sub + "/papeis");

    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
      .header(AUTHORIZATION, BEARER + token)
      .GET().build();

    HttpClient httpClient = HttpClient.newHttpClient();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if(response.statusCode() == 200) {

        List<UnitRolesDto> unitRolesDtos =  mapper.readValue(response.body(), new TypeReference<List<UnitRolesDto>>() {
        });
        
        return unitRolesDtos.stream().filter(role -> role.isPrioritario()).findFirst()
                .orElseGet(() -> {
                    if(prioritario) return null;
                    
                    return unitRolesDtos.stream().findFirst().orElse(null);
                });
        
        

//        

      } else {
        logger.error("Não foi possível buscar o papel atrelado ao sub do agente.");
        throw new ApiAcessoCidadaoException(STATUS + response.statusCode());
      }
    } catch (IOException | InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error(e.getMessage());
      throw new ApiAcessoCidadaoException("Erro ao buscar o papel atrelado ao sub do agente.");
    }
  }
}
