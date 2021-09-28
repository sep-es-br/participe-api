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
      + " WHERE id(p) = {0} "
      + " OPTIONAL MATCH (p)-[m:MADE]->(s:SelfDeclaration)"
      + " OPTIONAL MATCH (p)-[i:IS_AUTHENTICATED_BY]->(at:AuthService)"
      + " RETURN p, m, s, i, at "
      + " , ["
      + " 		[(s)-[a:AS_BEING_FROM]-(l:Locality) | [a,l] ],"
      + " 		[(s)-[t:TO]-(c:Conference) | [t,c] ]"
      + " ]")
  Person findRelationships(Long id);

  @Query("MATCH (p:Person) "
      + " WHERE p.contactEmail={0} "
      + " RETURN p ")
  Optional<Person> findByContactEmail(String email);

  @Query("MATCH (person:Person)-[authBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
      "WHERE authBy.email={0} " +
      "RETURN person"
  )
  Optional<Person> findByLoginEmail(String email);

  @Query(" MATCH (p:Person) "
      + " WHERE id(p)={0} "
      + " RETURN p "
      + " ,[ "
      + " 		[(p)<-[lk:LIKED_BY]-(c:Comment) | [lk,c]] "
      + " ] ")
  Person likesComments(Long idPerson);

  Person findByCpfIgnoreCase(String cpf);

  @Query("MATCH (c:Comment)-[m:MADE_BY]->(p:Person) "
      + " WHERE id(c)={0} "
      + " RETURN p ")
  Person findPersonMadeByIdComment(Long idComment);

  @Query(" MATCH (c:Comment)-[m:LIKED_BY]->(p:Person) "
      + " WHERE id(c)={0} "
      + " RETURN p ")
  List<Person> findPersonLikedByIdComment(Long idComment);

  @Query("MATCH (p:Person)-[i:IS_AUTHENTICATED_BY]->(a:AuthService) WHERE p.contactEmail={0} AND a.server={1} RETURN p")
  Person validate(String email, String server);

  @Query("MATCH (person:Person)-[is_auth_by:IS_AUTHENTICATED_BY]->(auth:AuthService) " +
      "WHERE (is_auth_by.email={0}) " +
      "AND (auth.server={1} OR {1} IS NULL) " +
      "AND (person.cpf={2} OR {2} IS NULL OR {2}='')" +
      "RETURN person"
  )
  Optional<Person> havePersonWithLoginEmail(String email, String server, String cpf);

  @Query(value = "MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
      "MATCH (s)-[:TO]->(c:Conference) " +
      //"WHERE id(c) = {5} " +
      "WHERE (p.name IS NULL OR  ext.translate(p.name) CONTAINS ext.translate({0})) " +
      "AND (p.contactEmail IS NULL OR ext.translate(p.contactEmail) CONTAINS ext.translate({1})) " +
      "AND ({3} IS NULL OR coalesce(p.active,true) = {3}) " +
      "AND (aut.name IS NULL OR ext.translate(aut.name) CONTAINS ext.translate({2})) " +
      "AND (id(loc) IN {4} OR NOT {4}) " +
      "RETURN DISTINCT id(p) AS id, lower(p.name) AS name, p.contactEmail AS email, coalesce(p.active,true) AS active"
      , countQuery =
      "MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
          "MATCH (s)-[:TO]->(c:Conference) " +
          //"WHERE id(c) = {5} " +
          "WHERE (p.name IS NULL OR  ext.translate(p.name) CONTAINS ext.translate({0})) " +
          "AND (p.contactEmail IS NULL OR ext.translate(p.contactEmail) CONTAINS ext.translate({1})) " +
          "AND ({3} IS NULL OR coalesce(p.active,true) = {3}) " +
          "AND (aut.name IS NULL OR ext.translate(aut.name) CONTAINS ext.translate({2})) " +
          "AND (id(loc) IN {4} OR NOT {4}) " +
          "WITH DISTINCT id(p) AS id, lower(p.name) AS name, p.contactEmail AS email, coalesce(p.active,true) AS active " +
          "RETURN COUNT(*)"
  )
  Page<PersonKeepCitizenDto> findPersonKeepCitizen(
      String name,
      String email,
      String authentication,
      Boolean active,
      List<Long> locality,
      Pageable page
  );

  @Query("MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person) " +
      "MATCH (au)<-[:USING]-(log:Login)-[:TO]->(c:Conference) " +
      "WHERE id(c) = {0} " +
      "AND id(p) = {1} " +
      "RETURN DISTINCT aut.name AS loginName, count(log) AS acesses")
  List<LoginAccessDto> findAccessByPerson(Long idConference, Long idPerson);

  @Query("MATCH (p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
      "WHERE id(p) = {1} " +
      "MATCH (c:Conference)<-[:TO]-(s) " +
      "WHERE id(c) = {0} " +
      "OPTIONAL MATCH (p)-[:MADE]->(log:Login)-[:TO]->(c) " +
      "WITH {time: log.time, locality: loc.name, localityId: id(loc)} AS tuple ORDER BY log.time DESC LIMIT 1 " +
      "RETURN tuple.locality AS localityName, tuple.localityId AS localityId")
  LocalityInfoDto findRecentLocalityByPerson(Long idConference, Long idPerson);

  @Query("MATCH (p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
      "WHERE id(p) = {1} " +
      "MATCH (c:Conference)<-[:TO]-(s) " +
      "WHERE id(c) = {0} " +
      "WITH {locality: loc.name, localityId: id(loc)} AS tuple " +
      "RETURN tuple.locality AS localityName, tuple.localityId AS localityId")
  LocalityInfoDto findLocalityByPersonAndConference(Long idConference, Long idPerson);

  @Query("MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
      ",(s)-[:TO]->(c:Conference) " +
      "WHERE id(c) = {0} " +
      "AND id(p) = {1} " +
      "RETURN loc.name AS localityName, id(loc) AS localityId")
  LocalityInfoDto findLocalityByPerson(Long idConference, Long idPerson);

  @Query(value = "WITH split({1},' ') AS search " +
      "UNWIND search AS s " +
      "WITH s AS s2 " +
      "WITH collect(s2) AS s3 " +
      "MATCH (p:Person)-[:MADE]->(sd:SelfDeclaration) " +
      "WHERE ALL(x IN s3 WHERE toLower(p.name) CONTAINS toLower(x)) OR ALL(x IN s3 WHERE p.contactEmail CONTAINS x) " +
      "OPTIONAL MATCH (p)-[cia:CHECKED_IN_AT]->(m:Meeting) " +
      "WHERE id(m) = {0} " +
      "RETURN DISTINCT id(p) AS personId, toLower(p.name) AS name, p.contactEmail AS email, p.telephone AS telephone, " +
      "cia IS NOT NULL AS checkedIn, cia.time AS checkedInDate, p.cpf AS cpf"
      , countQuery = "WITH split({1},' ') AS search " +
      "UNWIND search AS s " +
      "WITH s AS s2 " +
      "WITH collect(s2) AS s3 " +
      "MATCH (p:Person)-[:MADE]->(sd:SelfDeclaration) " +
      "WHERE ALL(x IN s3 WHERE toLower(p.name) CONTAINS toLower(x)) OR ALL(x IN s3 WHERE p.contactEmail CONTAINS x) " +
      "OPTIONAL MATCH (p)-[cia:CHECKED_IN_AT]->(m:Meeting) " +
      "WHERE id(m) = {0} " +
      "WITH DISTINCT id(p) AS personId, toLower(p.name) AS name, p.contactEmail AS email, p.telephone AS telephone, " +
      "cia IS NOT NULL AS checkedIn, cia.time AS checkedInDate, p.cpf AS cpf " +
      "RETURN COUNT(*)")
  Page<PersonMeetingDto> findPersonForMeeting(Long idMeeting, String name, Pageable pageable);

  @Query(
      "MATCH (m:Meeting)-[:OCCURS_IN]->(c:Conference) " +
          "WHERE id(m)={1} " +
          "MATCH (p:Person)-[:MADE]->(log:Login)-[:TO]->(c)<-[:TO]-(sd:SelfDeclaration)<-[:MADE]-(p) " +
          "WHERE id(p) = {0} " +
          "MATCH (sd)-[abf:AS_BEING_FROM]->(l:Locality)-[ot:OF_TYPE]-(lt:LocalityType) " +
          "OPTIONAL MATCH (l)-[ili2:IS_LOCATED_IN]->(loc2:Locality)-[ot2:OF_TYPE]->(lt2:LocalityType) " +
          "RETURN l.name AS locality, loc2.name AS superLocality, id(loc2) AS superLocalityId, " +
          "lt.name AS regionalizable " +
          "ORDER BY log.time DESC LIMIT 1")
  LocalityRegionalizableDto findMostRecentLocality(Long idPerson, Long idMeeting);

  @Query(
      "MATCH (m:Meeting)-[:OCCURS_IN]->(c:Conference) " +
          "WHERE id(m)={1} " +
          "MATCH (c)<-[:TO]-(sd:SelfDeclaration)<-[:MADE]-(p) " +
          "WHERE id(p) = {0} " +
          "MATCH (sd)-[abf:AS_BEING_FROM]->(l:Locality)-[ot:OF_TYPE]-(lt:LocalityType) " +
          "OPTIONAL MATCH (l)-[ili2:IS_LOCATED_IN]->(loc2:Locality)-[ot2:OF_TYPE]->(lt2:LocalityType) " +
          "RETURN l.name AS locality, loc2.name AS superLocality, id(loc2) AS superLocalityId, " +
          "lt.name AS regionalizable " +
          "ORDER BY p.name DESC LIMIT 1")
  LocalityRegionalizableDto findLocalityIfThereIsNoLogin(Long idPerson, Long idMeeting);

  @Query(
      value = "MATCH (m:Meeting) " +
          "WHERE id(m) = {0} " +
          "MATCH (p:Person)-[cia:CHECKED_IN_AT]->(m) " +
          "WHERE ({2} IS NULL OR lower(p.name) CONTAINS {2}) " +
          "OPTIONAL MATCH (p)-[md:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
          "WITH p,m,cia,loc " +
          "WHERE ((loc IS NOT NULL AND id(loc) IN {1}) OR NOT {1}) " +
          "RETURN DISTINCT id(p) AS personId, lower(p.name) AS name, p.contactEmail AS email, " +
          "p.telephone AS telephone, cia.time AS checkedInDate",
      countQuery = "MATCH (m:Meeting) " +
          "WHERE id(m) = {0} " +
          "MATCH (p:Person)-[cia:CHECKED_IN_AT]->(m) " +
          "WHERE ({2} IS NULL OR lower(p.name) CONTAINS {2}) " +
          "OPTIONAL MATCH (p)-[md:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
          "WITH p,m,cia,loc " +
          "WHERE ((loc IS NOT NULL AND id(loc) IN {1}) OR NOT {1}) " +
          "WITH DISTINCT id(p) AS personId, lower(p.name) AS name, p.contactEmail AS email, " +
          "p.telephone AS telephone, cia.time AS checkedInDate " +
          "RETURN COUNT(*)"
  )
  Page<PersonMeetingDto> findPersonsCheckedInOnMeeting(Long idMeeting, List<Long> localities, String name,
                                                       Pageable pageable);

  @Query(
      "MATCH (m:Meeting) " +
          "WHERE id(m) = {0} " +
          "MATCH (p:Person)-[cia:CHECKED_IN_AT]->(m) " +
          "WITH DISTINCT id(p) AS personId, lower(p.name) AS name, p.contactEmail AS email, " +
          "p.telephone AS telephone, cia.time AS checkedInDate " +
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

  @Query("MATCH(person:Person)-[authBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
      "WHERE id(person) = {0} " +
      "AND authBy.authType <> 'participeCpf'" +
      "RETURN DISTINCT authBy.email AS email")
  List<PersonProfileEmailsDto> findPersonEmails(Long personId);

  @Query("MATCH " +
      "(person:Person)-[is_auth_by:IS_AUTHENTICATED_BY]->(auth:AuthService), " +
      "(auth)<-[using:USING]-(login:Login), " +
      "(login)-[to:TO]->(conference:Conference) " +
      "WHERE (is_auth_by.email={0}) " +
      "AND (person.cpf={1} OR {1} IS NULL) " +
      "RETURN person, conference, auth, login"
  )
  Person findPersonByParticipeAuthServiceEmailOrCpf(String email, String cpf);
}
