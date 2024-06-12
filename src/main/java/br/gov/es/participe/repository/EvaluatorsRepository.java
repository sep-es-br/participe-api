package br.gov.es.participe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        value = 
            "MATCH (org:Organization), " +
            "(section:Section)-[:BELONGS_TO]->(org) " +
            "OPTIONAL MATCH (role:Role)-[:BELONGS_TO]->(section) " +
            "RETURN id(org) AS id, org.guid AS organizationGuid, " +
            "collect(DISTINCT section.guid) AS sectionsGuid, collect(DISTINCT role.guid) AS rolesGuid",
        countQuery = 
            "MATCH (org:Organization), " +
            "(section:Section)-[:BELONGS_TO]->(org) " +
            "OPTIONAL MATCH (role:Role)-[:BELONGS_TO]->(section) " +
            "RETURN count(DISTINCT org)"
    )
    Page<EvaluatorResponseDto> findAllEvaluators(@Param("pageable") Pageable pageable);

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
        "MATCH (org:Organization) " +
        "WHERE id(org) = $evaluatorId " +
        "RETURN org"
    )
    Optional<Organization> findOrganizationById(@Param("evaluatorId") Long evaluatorId);

    @Query(
        "MATCH (org:Organization)<-[:BELONGS_TO]-(section:Section) " +
        "WHERE section.guid = $sectionGuid " +
        "RETURN org"
    )
    Organization findOrganizationRelatedToSectionBySectionGuid(@Param("sectionGuid") String sectionGuid);

    @Query(
        "MATCH (org:Organization)<-[:BELONGS_TO]-(section:Section)<-[:BELONGS_TO]-(role:Role) " +
        "WHERE role.guid = $roleGuid " +
        "RETURN org"
    )
    Organization findOrganizationRelatedToRoleByRoleGuid(@Param("roleGuid") String roleGuid);

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
        "MATCH (section:Section) " +
        "WHERE section.guid = $sectionGuid " +
        "AND NOT ((section)<-[:BELONGS_TO]-(:Role)) " +
        "RETURN section"
    )
    Optional<Section> findSectionWithNoRoleByGuid(@Param("sectionGuid") String sectionGuid);

    @Query(
        "MATCH (section:Section)-[:BELONGS_TO]->(org:Organization) " +
        "WHERE id(org)=$orgId " +
        "RETURN collect(DISTINCT section)"
    )
    List<Section> findAllSectionsWithRelationshipToOrganization(@Param("orgId") Long orgId);

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

    @Query(
        "MATCH (role:Role) " +
        "WHERE NOT ((role)-[:BELONGS_TO]->(:Section)) " +
        "RETURN collect(DISTINCT role)"
    )
    List<Role> findAllRolesWithNoRelationships();

    @Query(
        "MATCH (role:Role)-[:BELONGS_TO]->(section:Section) " +
        "WHERE id(section)=$sectionId " +
        "RETURN collect(DISTINCT role)"
    )
    List<Role> findAllRolesWithRelationshipToSection(@Param("sectionId") Long sectionId);

    @Query(
        "MATCH (org:Organization) " +
        "WHERE id(org) = $evaluatorId " +
        "MATCH (section:Section)-[:BELONGS_TO]->(org) " +
        "OPTIONAL MATCH (role:Role)-[:BELONGS_TO]->(section) " +
        "DETACH DELETE org, section, role"
    )
    void deleteEvaluatorById(@Param("evaluatorId") Long evaluatorId);
    
}
