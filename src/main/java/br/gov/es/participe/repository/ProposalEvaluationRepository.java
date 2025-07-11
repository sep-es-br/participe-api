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
import br.gov.es.participe.controller.dto.ProposalEvaluationDto;
import br.gov.es.participe.controller.dto.StructureItemAndLocalityTypeDto;
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
        + "AND ((NOT eval.deleted OR eval.deleted IS NULL) AND (eval.representing IS NOT NULL AND eval.representing IN $organizationGuid) OR $organizationGuid = []) "
        + "AND ((eval.approved IS NOT NULL AND eval.approved = $approved) OR $approved IS NULL) "
        + "AND apoc.text.clean(comment.text) CONTAINS apoc.text.clean($commentText) ";

    @Query(
        "MATCH (comment:Comment) " +
        "WHERE id(comment)=$commentId " +
        "RETURN exists((comment)<-[:EVALUATES]-())"
    )
    Boolean existsRelationshipByCommentId(@Param("commentId") Long commentId);

    @Query(
        "MATCH (person:Person)-[eval:EVALUATES]->(comment:Comment)-[:ABOUT]->(conference:Conference) " +
        "WHERE id(conference)=$conferenceId " +
        "RETURN person.name AS personName, comment.text AS description " +
        "LIMIT 20"
    )
    List<ProposalEvaluationDto> findAllByConferenceId(@Param("conferenceId") Long conferenceId);

    @Query(value = "MATCH (microLoc:Locality)<-[:IS_LOCATED_IN]-(locality:Locality)<-[:ABOUT]-(comment:Comment)-[:ABOUT]->(conference:Conference),\n" +
                "      (comment)-[:ABOUT]->(planItem:PlanItem)-[:COMPOSES]->(area:PlanItem)\n" +
                "WHERE id(conference) = $conferenceId\n" +
                "  AND comment.type = 'prop'\n" +
                "  AND comment.status = 'pub'\n" +
                "  AND (comment.duplicated = false OR comment.duplicated IS NULL)\n" +
                "OPTIONAL MATCH (comment)<-[eval:EVALUATES]-(person:Person)\n" +
                "WITH comment, locality, planItem, area, eval, person, microLoc \r\n" + //
                 SEARCH_FILTER + //
                "WITH comment, locality, planItem, area, microLoc, \n" +
                "  CASE\n" +
                "    WHEN (NOT eval.deleted OR eval.deleted IS NULL) AND exists((comment)<-[:EVALUATES]-()) THEN true\n" +
                "    ELSE false\n" +
                "  END AS evaluationStatus,\n" +
                "  CASE\n" +
                "    WHEN (NOT eval.deleted OR eval.deleted IS NULL) AND exists((comment)<-[:EVALUATES]-()) THEN collect(DISTINCT {\n" +
                "      evaluatorOrgsName: eval.representing,\n" +
                "      approved: eval.approved,\n" +
                "      evaluatorName: person.name\n" +
                "    })\n" +
                "    ELSE NULL\n" +
                "  END AS evaluatorOrgsNameAndApprovedList\n" +
                "WITH comment, locality, planItem, area, microLoc, \n" +
                "  [result IN collect(DISTINCT {\n" +
                "    evaluationStatus: evaluationStatus,\n" +
                "    evaluatorOrgsNameAndApprovedList: evaluatorOrgsNameAndApprovedList\n" +
                "  }) WHERE result.evaluationStatus = true] AS trueResults,\n" +
                "  [result IN collect(DISTINCT {\n" +
                "    evaluationStatus: evaluationStatus,\n" +
                "    evaluatorOrgsNameAndApprovedList: evaluatorOrgsNameAndApprovedList\n" +
                "  }) WHERE result.evaluationStatus = false] AS falseResults\n" +
                "WITH comment, locality, planItem, area, microLoc, \n" +
                "  CASE\n" +
                "    WHEN SIZE(trueResults) > 0 THEN HEAD(trueResults)\n" +
                "    ELSE HEAD(falseResults)\n" +
                "  END AS finalResult\n" +
                "RETURN DISTINCT\n" +
                "  id(comment) AS commentId,\n" +
                "  locality.name AS localityName,\n" +
                "  microLoc.name AS microrregionName,\n" +
                "  planItem.name AS planItemName,\n" +
                "  area.name AS planItemAreaName,\n" +
                "  comment.text AS description,\n" +
                "  finalResult.evaluationStatus AS evaluationStatus,\n" +
                "  finalResult.evaluatorOrgsNameAndApprovedList AS evaluatorOrgsNameAndApprovedList", 
            countQuery = "MATCH (locality:Locality)<-[:ABOUT]-(comment:Comment)-[:ABOUT]->(conference:Conference), " +
                    "(comment)-[:ABOUT]->(planItem:PlanItem)-[:COMPOSES]->(area:PlanItem) " +
                    "WHERE id(conference) = $conferenceId " +
                    "AND comment.type = 'prop' " +
                    "AND comment.status = 'pub' " +
                    "OPTIONAL MATCH (comment)<-[eval:EVALUATES]-(person:Person) " +
                    "WITH comment, locality, planItem, area, eval, person " +
                    SEARCH_FILTER +
                    "RETURN count(DISTINCT comment)")
    Page<ProposalEvaluationCommentResultDto> findAllCommentsForEvaluation(
        @Param("evaluationStatus") Boolean evaluationStatus, 
        @Param("localityId") Long localityId, 
        @Param("planItemAreaId") Long planItemAreaId, 
        @Param("planItemId") Long planItemId,
        @Param("organizationGuid") List<String> organizationGuid, 
        @Param("approved") Boolean approved, 
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
    Optional<List<Evaluates>> findEvaluatesRelationshipByCommentId(@Param("proposalId") Long proposalId);

    @Query(
        "MATCH (person:Person)-[eval:EVALUATES]->(comment:Comment) " +
        "WHERE id(comment) = $proposalId AND ($guid is null OR eval.representing = $guid) " +
        "RETURN comment, collect(DISTINCT eval), person"
    )
    List<Evaluates> getEvaluatesRelationshipListByCommentId(@Param("proposalId") Long proposalId, @Param("guid") String guid);

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
        "RETURN planItemArea " +
        "ORDER BY planItemArea.name asc "
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

    @Query(
        "MATCH (c:Conference)-[:TARGETS]->(p:Plan)-[:OBEYS]->(s:Structure)<-[:COMPOSES]-(si:StructureItem), (p)-[:REGIONALIZABLE]->(lt:LocalityType) " +
        "WHERE id(c)= $conferenceId RETURN si.name AS structureItemName , lt.name AS localityTypeName"
    )
    StructureItemAndLocalityTypeDto getStructureItemAndLocalityType(@Param("conferenceId") Long conferenceId);
}
