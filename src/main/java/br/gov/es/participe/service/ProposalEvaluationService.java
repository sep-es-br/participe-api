package br.gov.es.participe.service;

import java.nio.charset.Charset;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.util.Base64;

import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.gov.es.participe.controller.dto.BudgetOptionsDto;
import br.gov.es.participe.controller.dto.DomainConfigurationDto;
import br.gov.es.participe.controller.dto.EvaluatorRoleDto;
import br.gov.es.participe.controller.dto.LocalityInfoDto;
import br.gov.es.participe.controller.dto.PlanItemComboDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationRequestDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationResponseDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationCommentResultDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationJasperParamDto;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.Evaluates;
import br.gov.es.participe.model.IsAuthenticatedBy;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.repository.ProposalEvaluationRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class ProposalEvaluationService {
    
    @Autowired
    private ProposalEvaluationRepository proposalEvaluationRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private AcessoCidadaoService acessoCidadaoService;

    @Autowired
    private EvaluatorsService evaluatorsService;

    @Value("${pentahoBI.baseURL}")
    private String baseURL;

    @Value("${pentahoBI.path}")
    private String path;

    @Value("${pentahoBI.targetDataSource}")
    private String targetDataSource;

    @Value("${pentahoBI.userId}")
    private String userId;

    @Value("${pentahoBI.password}")
    private String password;

    @Value("${spring.data.neo4j.uri}")
    private String urlConnection;

    @Value("${spring.data.neo4j.username}")
    private String userName;

    @Value("${spring.data.neo4j.password}")
    private String passwordNeo4j;

    private static final String charset = "UTF-8";

    private static final Logger log = LoggerFactory.getLogger(EvaluatorsService.class);

    public String checkIsPersonEvaluator(Long personId) {

        log.info("Buscando usuario com autenticacao pelo Acesso Cidadao de id={}", personId);
        IsAuthenticatedBy authRelationship = personService.getIsAuthenticatedBy(personId, "AcessoCidadao");

        log.info("Buscando papel na API do Acesso Cidadao");
        List<EvaluatorRoleDto> evaluatorRoleDto = acessoCidadaoService.findRoleFromAcessoCidadaoAPIByAgentePublicoSub(authRelationship.getIdByAuth());

        return evaluatorsService.findOrganizationGuidBySectionOrRole(evaluatorRoleDto);

    }

    public Page<ProposalEvaluationCommentResultDto> findAllCommentsForEvaluation(
        Boolean evaluationStatus, 
        Long localityId, 
        Long planItemAreaId, 
        Long planItemId,
        String organizationGuid, 
        Boolean loaIncluded, 
        String commentText, 
        Long conferenceId, 
        Pageable pageable
    ) {

        Page<ProposalEvaluationCommentResultDto> commentsForEvaluation = proposalEvaluationRepository.findAllCommentsForEvaluation(
            evaluationStatus, 
            localityId, 
            planItemAreaId, 
            planItemId,
            organizationGuid, 
            loaIncluded, 
            commentText, 
            conferenceId, 
            pageable);

        return commentsForEvaluation;
        
    }

    public ProposalEvaluationResponseDto getProposalEvaluationData(Long proposalId, String guid) {

        log.info("Buscando dados de avaliacao de proposta por comentario com id={}", proposalId);
        Optional<List<Evaluates>> evaluatesRelationship = proposalEvaluationRepository.findEvaluatesRelationshipByCommentId(proposalId);

        if(evaluatesRelationship.isPresent()){
            for (Evaluates evaluete : evaluatesRelationship.get()) {
                if(evaluete.getRepresenting().equals(guid)){
                    log.info("Dados encontrados, retornando avaliacao de proposta com id={}", evaluete.getId());
                    return new ProposalEvaluationResponseDto(evaluete);
                }
            }
        } 
        log.info("Dados nao encontrados, retornando DTO de resposta vazio");
        return new ProposalEvaluationResponseDto();

    }

    public ProposalEvaluationResponseDto createProposalEvaluation(ProposalEvaluationRequestDto proposalEvaluationRequestDto) {

        log.info("Buscando pessoa com id={}", proposalEvaluationRequestDto.getPersonId());
        Person person = personService.find(proposalEvaluationRequestDto.getPersonId());
        
        log.info("Buscando comentario com id={}", proposalEvaluationRequestDto.getProposalId());
        Comment proposal = commentService.find(proposalEvaluationRequestDto.getProposalId());

        log.info("Criando novo relacionamento");
        Evaluates newEvaluatesRelationship = new Evaluates(proposalEvaluationRequestDto);

        newEvaluatesRelationship.setPerson(person);
        newEvaluatesRelationship.setComment(proposal);
        newEvaluatesRelationship.setActive(true);
        newEvaluatesRelationship.setDeleted(false);
        newEvaluatesRelationship.setDate(new Date());

        setOtherEvaluatesRelationshipsActiveAsFalse(proposalEvaluationRequestDto.getProposalId(), proposalEvaluationRequestDto.getRepresenting());

        proposalEvaluationRepository.save(newEvaluatesRelationship);

        log.info("Avaliacao de proposta criada com sucesso, id={}", newEvaluatesRelationship.getId());
        return new ProposalEvaluationResponseDto(newEvaluatesRelationship);

    }

    public void deleteProposalEvaluation(ProposalEvaluationRequestDto proposalEvaluationRequestDto) {

        setOtherEvaluatesRelationshipsActiveAsFalse(proposalEvaluationRequestDto.getProposalId(), proposalEvaluationRequestDto.getRepresenting());

        log.info("Buscando pessoa com id={}", proposalEvaluationRequestDto.getPersonId());
        Person person = personService.find(proposalEvaluationRequestDto.getPersonId());
        
        log.info("Buscando comentario com id={}", proposalEvaluationRequestDto.getProposalId());
        Comment proposal = commentService.find(proposalEvaluationRequestDto.getProposalId());

        log.info("Criando novo relacionamento");
        Evaluates newEvaluatesRelationship = new Evaluates(proposalEvaluationRequestDto);

        newEvaluatesRelationship.setPerson(person);
        newEvaluatesRelationship.setComment(proposal);
        newEvaluatesRelationship.setActive(true);
        newEvaluatesRelationship.setDeleted(true);
        newEvaluatesRelationship.setDate(new Date());

        proposalEvaluationRepository.save(newEvaluatesRelationship);

        log.info("Avaliacao de proposta excluída com sucesso, id={}", newEvaluatesRelationship.getId());

    }

    private void setOtherEvaluatesRelationshipsActiveAsFalse(Long proposalId, String guid) {

        log.info("Buscando lista de avaliacoes de proposta relacionadas ao comentario com id={}", proposalId);
        List<Evaluates> evaluatesRelationshipList = proposalEvaluationRepository.getEvaluatesRelationshipListByCommentId(proposalId, guid);

        if(!evaluatesRelationshipList.isEmpty()){
            log.info("Atribuindo estado ativo falso aos relacionamentos");
            evaluatesRelationshipList.forEach((evaluatesRelationship) ->evaluatesRelationship.setActive(false));

            proposalEvaluationRepository.saveAll(evaluatesRelationshipList);
        }

    }

    public List<LocalityInfoDto> getLocalityOptionsByConferenceId(Long conferenceId) {
        
        List<LocalityInfoDto> localityInfoDtoList = new ArrayList<LocalityInfoDto>();

        List<Locality> localitiesList = proposalEvaluationRepository.getLocalityOptionsByConferenceId(conferenceId);

        localitiesList.iterator().forEachRemaining((locality) -> {
            localityInfoDtoList.add(new LocalityInfoDto(locality));
        });

        return localityInfoDtoList;

    }

    public List<PlanItemComboDto> getPlanItemOptionsByConferenceId(Long conferenceId) {

        List<PlanItemComboDto> planItemComboDtoList = new ArrayList<PlanItemComboDto>();
        
        List<PlanItem> planItemsList = proposalEvaluationRepository.getPlanItemOptionsByConferenceId(conferenceId);

        planItemsList.iterator().forEachRemaining((planItem) -> {
            planItemComboDtoList.add(new PlanItemComboDto(planItem.getId(), planItem.getName()));
        });

        return planItemComboDtoList;
    
    }

    public List<PlanItemComboDto> getPlanItemAreaOptionsByConferenceId(Long conferenceId) {

        List<PlanItemComboDto> planItemComboDtoList = new ArrayList<PlanItemComboDto>();

        List<PlanItem> planItemsAreaList = proposalEvaluationRepository.getPlanItemAreaOptionsByConferenceId(conferenceId);

        planItemsAreaList.iterator().forEachRemaining((planItemArea) -> {
            planItemComboDtoList.add(new PlanItemComboDto(planItemArea.getId(), planItemArea.getName()));
        });

        return planItemComboDtoList;

    }

    public Boolean checkIsCommentEvaluated(Long commentId) {

        return proposalEvaluationRepository.checkIsCommentEvaluated(commentId);

    }

    public List<BudgetOptionsDto> fetchDataFromPentahoAPI() {

        String uri = baseURL + path + targetDataSource;

        String notEncoded = userId + ":" + password;
        String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodedAuth);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName(charset)));

        List<BudgetOptionsDto> result = new ArrayList<BudgetOptionsDto>();

        try {
            ResponseEntity<String> response = restTemplate.exchange(RequestEntity.get(new URI(uri)).headers(headers).build(), String.class);
            result = mapResultToBudgetOptionsList(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }   

        return result;

    }

    private List<BudgetOptionsDto> mapResultToBudgetOptionsList(String result) throws ParseException {

        JSONObject jsonObj = new JSONObject(result);
        JSONArray resultSet = new JSONArray().put(jsonObj.get("resultset")).getJSONArray(0);

        Map<String, BudgetOptionsDto> budgetOptionsMap = new HashMap<String, BudgetOptionsDto>();

        for (int i = 0; i < resultSet.length(); i++) {
            
            List<Object> data = resultSet.getJSONArray(i).toList();

            String budgetUnitId = data.get(0).toString();
            String budgetUnitName = data.get(1).toString();
            String budgetActionId = data.get(2).toString();
            String budgetActionName = data.get(3).toString();
            String mapKey = budgetUnitId + budgetUnitName;
            BudgetOptionsDto targetDto = budgetOptionsMap.get(mapKey);

            if( targetDto == null ) {
                targetDto = new BudgetOptionsDto(budgetUnitId, budgetUnitName);
                budgetOptionsMap.put(mapKey, targetDto);
            }

            targetDto.buildBudgetActionDto(budgetActionId, budgetActionName);

        }

        return new ArrayList<BudgetOptionsDto>(budgetOptionsMap.values());

    }

    public DomainConfigurationDto getDomainConfiguration(Long conferenceId) {
        return proposalEvaluationRepository.getDomainConfiguration(conferenceId);
    }

    public ByteArrayInputStream jasperXlsx(Boolean evaluationStatus,
            Long localityId,
            Long planItemAreaId,
            Long planItemId,
            String organizationGuid,
            Boolean loaIncluded,
            String commentText,
            Long conferenceId) {

        ProposalEvaluationJasperParamDto proposalEvaluationJasperParamDto = new ProposalEvaluationJasperParamDto(
                evaluationStatus, localityId, planItemAreaId, planItemId, organizationGuid, loaIncluded, commentText,
                conferenceId);
        Map<String, Object> proposalEvaluationMap = proposalEvaluationJasperParamDto.getProposalEvaluationJasperMap();
        
        
        try {
            Connection connection = DriverManager.getConnection(
            "jdbc:neo4j:" + this.urlConnection,
            this.userName,
            this.passwordNeo4j);
            JasperReport report = JasperCompileManager.compileReport(ProposalEvaluationService.class.getResourceAsStream("/jasper/proposalEvaluation.jrxml"));
            JasperPrint print = JasperFillManager.fillReport(report, proposalEvaluationMap, connection);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setWhitePageBackground(false);
            
            exporter.setConfiguration(configuration);

            exporter.exportReport();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            return inputStream;

        } catch (JRException e) {
            throw new RuntimeException(e);
        } catch (SQLException e){
            System.err.println(e);
            throw new RuntimeException(e);
        }
    }

}
