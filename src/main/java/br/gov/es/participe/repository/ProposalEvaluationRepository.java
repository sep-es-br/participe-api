package br.gov.es.participe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.controller.dto.DomainConfigurationDto;
import br.gov.es.participe.controller.dto.ProposalEvaluationCommentResultDto;
import br.gov.es.participe.model.Evaluates;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.PlanItem;

public interface ProposalEvaluationRepository extends Neo4jRepository<Evaluates, Long> {

    static String SEARCH_FILTER = "WHERE "
        + "(eval.active = true OR eval.active IS NULL) "
        + "AND ((NOT eval.deleted OR eval.deleted IS NULL) AND exists((comment)<-[:EVALUATES]-()) = $evaluationStatus OR $evaluationStatus IS NULL) "
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
            "AND comment.type = 'prop' " +
            "AND comment.status = 'pub' " +
            "AND (comment.duplicated = false OR comment.duplicated is null) " +
            "OPTIONAL MATCH (comment)<-[eval:EVALUATES]-(person:Person) " +
            "WITH comment, locality, planItem, area, eval, person " +
            SEARCH_FILTER +
            "RETURN DISTINCT id(comment) AS commentId, " +
            "locality.name AS localityName, planItem.name AS planItemName, " +
            "area.name AS planItemAreaName, comment.text AS description, " +
            "CASE (NOT eval.deleted OR eval.deleted IS NULL) AND (exists((comment)<-[:EVALUATES]-())) " +
                  "WHEN true THEN true ELSE false END AS evaluationStatus, " +
            "CASE (NOT eval.deleted OR eval.deleted IS NULL) AND (exists((comment)<-[:EVALUATES]-())) " +
                  "WHEN true THEN collect(DISTINCT eval.representing) ELSE NULL END AS evaluatorOrgsNameList, " +
            "CASE (NOT eval.deleted OR eval.deleted IS NULL) AND (exists((comment)<-[:EVALUATES]-())) " +
                  "WHEN true THEN person.name ELSE NULL END AS evaluatorName, " +
            "CASE (NOT eval.deleted OR eval.deleted IS NULL) AND (exists((comment)<-[:EVALUATES]-())) " +
                  "AND eval.includedInNextYearLOA = true " +
                    "WHEN true THEN true ELSE false END AS loaIncluded",
        countQuery =
            "MATCH (locality:Locality)<-[:ABOUT]-(comment:Comment)-[:ABOUT]->(conference:Conference), " +
            "(comment)-[:ABOUT]->(planItem:PlanItem)-[:COMPOSES]->(area:PlanItem) " +
            "WHERE id(conference) = $conferenceId " +
            "AND comment.type = 'prop' " +
            "AND comment.status = 'pub' " +
            "OPTIONAL MATCH (comment)<-[eval:EVALUATES]-(person:Person) " +
            "WITH comment, locality, planItem, area, eval, person " +
            SEARCH_FILTER +
            "RETURN count(DISTINCT comment)"
    )
    Page<ProposalEvaluationCommentResultDto> findAllCommentsForEvaluation(
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
        "AND eval.active = true " +
        "AND (eval.deleted <> true OR eval.deleted IS NULL) " +
        "RETURN comment, eval, person"
    )
    Optional<Evaluates> findEvaluatesRelationshipByCommentId(@Param("proposalId") Long proposalId);

    @Query(
        "MATCH (person:Person)-[eval:EVALUATES]->(comment:Comment) " +
        "WHERE id(comment) = $proposalId " +
        "RETURN comment, collect(DISTINCT eval), person"
    )
    List<Evaluates> getEvaluatesRelationshipListByCommentId(@Param("proposalId") Long proposalId);

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
        "MATCH (comment:Comment) " +
        "WHERE id(comment) = $commentId " +
        "OPTIONAL MATCH (comment)<-[eval:EVALUATES]-(person:Person) " +
        "WHERE eval.active = true " +
        "AND eval.deleted <> true " +
        "RETURN exists((comment)<-[:EVALUATES]-())"
    )
    Boolean checkIsCommentEvaluated(@Param("commentId") Long commentId);

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