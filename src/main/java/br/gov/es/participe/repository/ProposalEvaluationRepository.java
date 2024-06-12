package br.gov.es.participe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.controller.dto.DomainConfigurationDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationResultDto;
import br.gov.es.participe.model.Evaluates;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.PlanItem;

public interface ProposalEvaluationRepository extends Neo4jRepository<Evaluates, Long> {

    static String SEARCH_FILTER = "WHERE "
        + "($evaluationStatus = exists((comment)<-[:EVALUATES]-(person)) OR $evaluationStatus IS NULL) " // Funciona; Verificar melhorias
        + "AND (id(locality) = $localityId OR $localityId IS NULL) "
        + "AND (id(area) = $planItemAreaId OR $planItemAreaId IS NULL) "
        + "AND (id(planItem) = $planItemId OR $planItemId IS NULL) "
        + "AND ((eval.representing IS NOT NULL AND eval.representing = $organizationGuid) OR $organizationGuid = '') "
        + "AND ((eval.includedInNextYearLOA IS NOT NULL AND eval.includedInNextYearLOA = $loaIncluded) OR $loaIncluded IS NULL) "
        + "AND apoc.text.clean(comment.text) CONTAINS apoc.text.clean($commentText) ";

    @Query(
        value =
            "MATCH (locality:Locality)<-[:ABOUT]-(comment:Comment)-[:ABOUT]->(conference:Conference), " +
            "(comment)-[:ABOUT]->(planItem:PlanItem)-[:COMPOSES]->(area:PlanItem) " +
            "WHERE id(conference) = $conferenceId " +
            "AND comment.status IN ['pub'] " +
            "OPTIONAL MATCH (comment)<-[eval:EVALUATES]-(person:Person) " +
            "WITH comment, locality, planItem, area, eval, person " +
            SEARCH_FILTER +
            "RETURN DISTINCT id(comment) AS id, " +
            "exists((comment)<-[:EVALUATES]-(person)) AS evaluationStatus, " +
            "locality.name AS localityName, planItem.name AS planItemName, " +
            "area.name AS planItemAreaName, comment.text AS description, " +
            "collect(DISTINCT eval.representing) AS evaluatorOrgsNameList, " +
            "person.name AS evaluatorName",
        countQuery = 
            "MATCH (locality:Locality)<-[:ABOUT]-(comment:Comment)-[:ABOUT]->(conference:Conference), " +
            "(comment)-[:ABOUT]->(planItem:PlanItem)-[:COMPOSES]->(area:PlanItem) " +
            "WHERE id(conference) = $conferenceId " +
            "AND comment.status IN ['pub'] " +
            "OPTIONAL MATCH (comment)<-[eval:EVALUATES]-(person:Person) " +
            "WITH comment, locality, planItem, area, eval, person " +
            SEARCH_FILTER +
            "RETURN count(DISTINCT comment)"
    )
    Page<ProposalEvaluationResultDto> findAllCommentsForEvaluation(
        @Param("evaluationStatus") Boolean evaluationStatus, 
        @Param("localityId") Long localityId, 
        @Param("planItemAreaId") Long planItemAreaId, 
        @Param("planItemId") Long planItemId,
        @Param("organizationGuid") String organizationGuid, 
        @Param("loaIncluded") Boolean loaIncluded, 
        @Param("commentText") String commentText, 
        @Param("conferenceId") Long conferenceId, 
        @Param("pageable") Pageable pageable);

    @Query(
        "MATCH (person:Person)-[eval:EVALUATES]->(comment:Comment) " +
        "WHERE id(comment) = $proposalId " +
        "RETURN comment, eval, person"
    )
    Optional<Evaluates> getEvaluatesRelationshipDataByCommentId(@Param("proposalId") Long proposalId);

    @Query(
        "MATCH (conference:Conference)-[:TARGETS]->(plan:Plan)-[:REGIONALIZABLE]->(localityType:LocalityType)<-[:OF_TYPE]-(locality:Locality) " +
        "WHERE id(conference)= $conferenceId " +
        "RETURN locality"
    )
    List<Locality> getLocalityOptionsByConferenceId(@Param("conferenceId") Long conferenceId);

    @Query(
        "MATCH (conference:Conference)-[:TARGETS]->(plan:Plan)<-[:COMPOSES]-(planItemArea:PlanItem)<-[:COMPOSES]-(planItem:PlanItem) " +
        "WHERE id(conference) = $conferenceId " +
        "RETURN planItem"
    )
    List<PlanItem> getPlanItemOptionsByConferenceId(@Param("conferenceId") Long conferenceId);

    @Query(
        "MATCH (conference:Conference)-[:TARGETS]->(plan:Plan)<-[:COMPOSES]-(planItemArea:PlanItem) " +
        "WHERE id(conference) = $conferenceId " +
        "RETURN planItemArea"
    )
    List<PlanItem> getPlanItemAreaOptionsByConferenceId(@Param("conferenceId") Long conferenceId);

    @Query(
        "MATCH (conference:Conference)-[:TARGETS]->(plan:Plan)-[:REGIONALIZABLE]->(localityType:LocalityType), " +
        "(plan)-[:OBEYS]->(structure:Structure)<-[:COMPOSES]-(planItemAreaType:StructureItem), " +
        "(plan)<-[:COMPOSES]-(planItemArea:PlanItem)<-[:COMPOSES]-(planItem:PlanItem)-[:OBEYS]->(planItemType:StructureItem) " +
        "WHERE id(conference) = $conferenceId " +
        "RETURN DISTINCT localityType.name AS localityTypeName, " +
        "planItemAreaType.name AS planItemAreaTypeName, " +
        "planItemType.name AS planItemTypeName"
    )
    DomainConfigurationDto getDomainConfiguration(@Param("conferenceId") Long conferenceId);

}