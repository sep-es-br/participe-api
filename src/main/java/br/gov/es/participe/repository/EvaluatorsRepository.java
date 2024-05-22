package br.gov.es.participe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.controller.dto.EvaluatorResponseDto;
import br.gov.es.participe.model.Evaluator;
import br.gov.es.participe.model.Organization;
import br.gov.es.participe.model.Role;
import br.gov.es.participe.model.Section;

public interface EvaluatorsRepository extends Neo4jRepository<Evaluator, Long> {


    @Query(
        "MATCH (org:Organization), " +
        "(section:Section)-[:BELONGS_TO]->(org), " +
        "(role:Role)-[:BELONGS_TO]->(section) " +
        "RETURN id(org) AS id, org.guid AS organizationGuid, " +
        "collect(DISTINCT section.guid) AS sectionsGuid, collect(DISTINCT role.guid) AS rolesGuid"
    )
    List<EvaluatorResponseDto> findAllEvaluators();

    @Query(
        value = 
            "MATCH (org:Organization) " +
            "RETURN org",
        countQuery = 
            "MATCH(org:Organization) " +
            "RETURN DISTINCT count(org)"
    )
    List<Organization> findAllOrganizations();


    @Query(
        "MATCH (org:Organization) " +
        "WHERE org.guid = $orgGuid " +
        "RETURN org"
    )
    Optional<Organization> findOrganizationByGuid(@Param("orgGuid") String orgGuid);

    @Query(
        value = 
            "MATCH (section:Section) " +
            "RETURN section",
        countQuery = 
            "MATCH(section:Section) " +
            "RETURN DISTINCT count(section)"
    )
    List<Section> findAllSections();


    @Query(
        "MATCH (section:Section) " +
        "WHERE section.guid = $sectionGuid " +
        "RETURN section"
    )
    Optional<Section> findSectionByGuid(@Param("sectionGuid") String sectionGuid);

    @Query(
        value = 
            "MATCH (role:Role) " +
            "RETURN role",
        countQuery = 
            "MATCH(role:Role) " +
            "RETURN DISTINCT count(role)"
    )
    List<Role> findAllRoles();


    @Query(
        "MATCH (role:Role) " +
        "WHERE role.guid = $roleGuid " +
        "RETURN role"
    )
    Optional<Role> findRoleByGuid(@Param("roleGuid") String roleGuid);
    
}
