package br.gov.es.participe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.model.Evaluator;
import br.gov.es.participe.model.Organization;
import br.gov.es.participe.model.Role;
import br.gov.es.participe.model.Section;

public interface EvaluatorsRepository extends Neo4jRepository<Evaluator, Long> {


    // @Query(
    //     "MATCH (eval:Evaluator) " +
    //     "WHERE eval.organization = $organizationGuid " +
    //     "RETURN eval"
    // )
    // Optional<Evaluator> findByOrganizationGuid(@Param("organizationGuid") String organizationGuid);

    
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
        "WHERE id(org) = $orgGuid " +
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
        "WHERE id(section) = $sectionGuid " +
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
        "WHERE id(role) = $roleGuid " +
        "RETURN role"
    )
    Optional<Role> findRoleByGuid(@Param("roleGuid") String roleGuid);
    
}
