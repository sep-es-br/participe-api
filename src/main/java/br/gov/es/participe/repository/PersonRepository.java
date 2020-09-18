package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.Person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

    @Query("MATCH (p:Person) "
    		+" WHERE id(p) = {0} "
    		+" OPTIONAL MATCH (p)-[m:MADE]->(s:SelfDeclaration)"
    		+" OPTIONAL MATCH (p)-[i:IS_AUTHENTICATED_BY]->(at:AuthService)"
    		+" RETURN p, m, s, i, at "
    		+" , ["
    		+" 		[(s)-[a:AS_BEING_FROM]-(l:Locality) | [a,l] ],"
    		+" 		[(s)-[t:TO]-(c:Conference) | [t,c] ]"
    		+" ]")
    Person findRelationships(Long id);
    
    @Query("MATCH (p:Person) "
    		+" WHERE (p.contactEmail={0} AND p.cpf = {1}) "
    		+ "OR (p.contactEmail={0} AND  p.cpf IS NULL) "
    		+ "OR (p.cpf = {1} AND p.contactEmail IS NULL) "
    		+" RETURN p")
    Person findByEmailOrCpf(String email, String cpf);

    @Query("MATCH (p:Person) "
    		+" WHERE p.contactEmail={0} "
    		+" RETURN p ")
    Optional<Person> findByContactEmail(String email);
    
    @Query(" MATCH (p:Person) "
    		+" WHERE id(p)={0} "
    		+" RETURN p "
    		+" ,[ "
    		+" 		[(p)<-[lk:LIKED_BY]-(c:Comment) | [lk,c]] "
    		+" ] ")
    Person likescomments(Long idPerson);
    
    Person findByCpfIgnoreCase(String cpf);
    
    @Query("MATCH (c:Comment)-[m:MADE_BY]->(p:Person) "
    		+" WHERE id(c)={0} "
    		+" RETURN p ")
    Person findPersonMadeByIdComment(Long idComment);
    
    @Query(" MATCH (c:Comment)-[m:LIKED_BY]->(p:Person) "
    		+" WHERE id(c)={0} "
    		+" RETURN p ")
    List<Person> findPersonLikedByIdComment(Long idComment);
    
    @Query("MATCH (p:Person)-[i:IS_AUTHENTICATED_BY]->(a:AuthService) WHERE p.contactEmail={0} AND a.server={1} RETURN p")
    Person validate(String email, String server);

    @Query(value = "MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
    		",(s)-[:TO]->(c:Conference) " +
			"WHERE (p.name IS NULL OR  ext.translate(p.name) CONTAINS ext.translate({0})) " +
			"AND (p.contactEmail IS NULL OR ext.translate(p.contactEmail) CONTAINS ext.translate({1})) " +
			"AND ({3} IS NULL OR COALESCE(p.active,true) = {3}) " +
    		"AND (aut.name IS NULL OR ext.translate(aut.name) CONTAINS ext.translate({2})) " +
			"AND (id(loc) IN {4} OR NOT {4}) " +
    		"RETURN DISTINCT id(p) AS id, lower(p.name) AS name, p.contactEmail AS email, COALESCE(p.active,true) AS active"
	, countQuery =
			"MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
			",(s)-[:TO]->(c:Conference) " +
			"WHERE (p.name IS NULL OR  ext.translate(p.name) CONTAINS ext.translate({0})) " +
			"AND (p.contactEmail IS NULL OR ext.translate(p.contactEmail) CONTAINS ext.translate({1})) " +
			"AND ({3} IS NULL OR COALESCE(p.active,true) = {3}) " +
			"AND (aut.name IS NULL OR ext.translate(aut.name) CONTAINS ext.translate({2})) " +
			"AND (id(loc) IN {4} OR NOT {4}) " +
			"WITH DISTINCT id(p) AS id, lower(p.name) AS name, p.contactEmail AS email, COALESCE(p.active,true) AS active " +
			"RETURN COUNT(*)"
	)
	Page<PersonKeepCitizenDto> findPersonKeepCitizen(String name, String email, String autentication, Boolean active,
													 List<Long> locality, Pageable page);

	@Query("MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person) " +
			"MATCH (au)<-[:USING]-(log:Login)-[:TO]->(c:Conference) " +
			"WHERE id(c) = {0} " +
			"AND id(p) = {1} " +
			"RETURN DISTINCT aut.name AS loginName, COUNT(log) AS acesses")
	List<LoginAccessDto> findAccessByPerson(Long idConference, Long idPerson);

	@Query("MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
			"WHERE id(p) = {1} " +
			"OPTIONAL MATCH (au)<-[:USING]-(log:Login)-[:TO]->(c:Conference)<-[:TO]-(s) " +
			"WHERE id(c) = {0} " +
			"WITH {time: log.time, locality: loc.name, localityId: id(loc)} AS tuple ORDER BY log.time desc limit 1 " +
			"RETURN tuple.locality AS localityName, tuple.localityId AS localityId")
	LocalityInfoDto findRecentLocalityByPerson(Long idConference, Long idPerson);
	
	@Query("MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
			",(s)-[:TO]->(c:Conference) " +
			"WHERE id(c) = {0} " +
			"AND id(p) = {1} " +
			"RETURN loc.name AS localityName, id(loc) AS localityId")
	LocalityInfoDto findLocalityByPerson(Long idConference, Long idPerson);

	@Query(value =
		"WITH split({1},' ') AS search " +
		"UNWIND search AS s " +
		"WITH ext.translate(s) AS s2 " +
		"WITH COLLECT(s2) AS s3 " +
		"MATCH (p:Person) " +
		"WHERE all(x in s3 where ext.translate(p.name) CONTAINS x) OR all(x in s3 where ext.translate(p.contactEmail) contains x) " +
		"OPTIONAL MATCH (p)-[cia:CHECKED_IN_AT]->(m:Meeting) " +
		"WHERE id(m) = {0} " +
		"RETURN id(p) AS personId, lower(p.name) AS name, p.contactEmail AS email, p.telephone as telephone, " +
		"cia IS NOT NULL AS checkedIn, cia.time AS checkedInDate, p.cpf AS cpf"
	, countQuery =
		"WITH split({1},' ') AS search " +
		"UNWIND search AS s " +
		"WITH ext.translate(s) AS s2 " +
		"WITH COLLECT(s2) AS s3 " +
		"MATCH (p:Person) " +
		"WHERE all(x in s3 where ext.translate(p.name) CONTAINS x) OR all(x in s3 where ext.translate(p.contactEmail) contains x) " +
		"OPTIONAL MATCH (p)-[cia:CHECKED_IN_AT]->(m:Meeting) " +
		"WHERE id(m) = {0} " +
		"WITH id(p) AS personId, lower(p.name) AS name, p.contactEmail AS email, p.telephone as telephone, " +
		"cia IS NOT NULL AS checkedIn, cia.time AS checkedInDate, p.cpf AS cpf " +
		"RETURN COUNT(*)")
	Page<PersonMeetingDto> findPersonForMeeting(Long idMeeting, String name, Pageable pageable);

	@Query(
			"MATCH (p:Person)-[md:MADE]-(sd:SelfDeclaration)-[abf:AS_BEING_FROM]-(l:Locality)-[ot:OF_TYPE]-(lt:LocalityType) " +
			"WHERE id(p)={0} " +
			"MATCH (m:Meeting)-[oi:OCCURS_IN]->(c:Conference) " +
			"WHERE id(m)={1} " +
			"MATCH (l)-[ili2:IS_LOCATED_IN]->(loc2:Locality)-[ot2:OF_TYPE]->(lt2:LocalityType) " +
			"OPTIONAL MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p)-[md2:MADE]->(log:Login)-[:TO]->(c)<-[:TO]-(s) " +
			"WITH DISTINCT log AS log, l.name AS locality, loc2.name AS superLocality, id(loc2) AS superLocalityId, " +
			"lt.name AS regionalizable " +
			"RETURN locality AS locality, superLocality AS superLocality, superLocalityId AS superLocalityId, " +
			"regionalizable AS regionalizable " +
			"ORDER BY log.time desc limit 1")
	LocalityRegionalizableDto findMostRecentLocality(Long idPerson, Long idMeeting);

	@Query(
			value = "MATCH (m:Meeting) " +
			"WHERE id(m) = {0} " +
			"MATCH (p:Person)-[cia:CHECKED_IN_AT]->(m) " +
					"WHERE ({2} IS NULL OR lower(p.name) CONTAINS {2}) " +
			"OPTIONAL MATCH (p)-[md:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
					"WITH p,m,cia,loc " +
					"WHERE ((loc IS NOT NULL AND id(loc) IN {1}) OR NOT {1}) " +
			"RETURN DISTINCT id(p) AS personId, lower(p.name) AS name, p.contactEmail AS email, " +
			"p.telephone as telephone, cia.time AS checkedInDate",
			countQuery = "MATCH (m:Meeting) " +
			"WHERE id(m) = {0} " +
			"MATCH (p:Person)-[cia:CHECKED_IN_AT]->(m) " +
					"WHERE ({2} IS NULL OR lower(p.name) CONTAINS {2}) " +
			"OPTIONAL MATCH (p)-[md:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
					"WITH p,m,cia,loc " +
					"WHERE ((loc IS NOT NULL AND id(loc) IN {1}) OR NOT {1}) " +
			"WITH DISTINCT id(p) AS personId, lower(p.name) AS name, p.contactEmail AS email, " +
			"p.telephone as telephone, cia.time AS checkedInDate " +
			"RETURN COUNT(*)"
	)
	Page<PersonMeetingDto> findPersonsCheckedInOnMeeting(Long idMeeting, List<Long> localities, String name,
														 Pageable pageable);

	@Query(
		"MATCH (m:Meeting) " +
		"WHERE id(m) = {0} " +
		"MATCH (p:Person)-[cia:CHECKED_IN_AT]->(m) " +
		"WITH DISTINCT id(p) AS personId, lower(p.name) AS name, p.contactEmail AS email, " +
		"p.telephone as telephone, cia.time AS checkedInDate " +
		"RETURN COUNT(*)"
	)
	Long findPeopleQuantityOnMeeting(Long idMeeting);

	@Query(
		"MATCH (m:Meeting)<-[cia:CHECKED_IN_AT]-(p:Person) " +
		"WHERE id(p) = {0} AND " +
		"m.beginDate < {1} AND m.endDate > {1} " +
		"RETURN p"
	)
	Optional<Person> findPersonIfParticipatingOnMeetingPresentially(Long personId, Date date);
}
