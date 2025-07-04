package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends Neo4jRepository<Person, Long> {


    @Query("MATCH (p:Person) "
        + " WHERE id(p) = $id "
        + " OPTIONAL MATCH (p)-[m:MADE]->(s:SelfDeclaration)"
        + " OPTIONAL MATCH (p)-[i:IS_AUTHENTICATED_BY]->(at:AuthService)"
        + " RETURN p, m, s, i, at "
        + " , ["
        + " 		[(s)-[a:AS_BEING_FROM]-(l:Locality) | [a,l] ], "
        + " 		[(s)-[t:TO]-(c:Conference) | [t,c] ]"
        + " ]")
    Person findRelationships(@Param("id")Long id);
  
  
    @Query("MATCH (p:Person) "
        + " WHERE p.contactEmail=$email "
        + " RETURN p ")
    Optional<Person> findByContactEmail(@Param("email")String email);
    
    @Query("MATCH (:AuthService{serverId:$sub})<-[:IS_AUTHENTICATED_BY]-(p:Person)\n" +
            "WHERE ($acEmail IS NULL OR p.contactEmail = $acEmail)\n" +
            "RETURN p")
    Optional<Person> findBySubEmail(final String sub, final String acEmail);
  
  
    @Query("MATCH (p:Person) "
        + " WHERE p.cpf=$cpf "
        + " RETURN p ")
    Optional<Person> findByCpf(@Param("cpf") String cpf);
    
  @Query(
        " MATCH (conf: Conference)<-[:OCCURS_IN]-(m:Meeting)<-[cia:CHECKED_IN_AT]-(p:Person) " +
        " WHERE id(p) = $idPerson AND id(conf) = $idConference " +
        " AND m.beginDate < $date AND m.endDate > $date " +
        " RETURN p"
    )
    Optional<Person> findPersonIfParticipatingOnMeetingPresentially(@Param("idPerson")Long idPerson, @Param("date")Date date, @Param("idConference") Long idConference);
  
    @Query("MATCH (person:Person)-[authBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
    "WHERE authBy.email=$email " +
    "RETURN person"
  )
  Optional<Person> findByLoginEmail(@Param("email")String email);

    @Query("MATCH (person:Person)-[authBy:IS_AUTHENTICATED_BY]->(authService:AuthService)\n" +
    "    WHERE authService.serverId=$sub\n" +
    "    OPTIONAL MATCH (l:Login)<-[:MADE]-(person)\n" +
    "    RETURN person\n" +
    "    ORDER BY l.time IS NOT NULL ASC, l.time DESC\n" +
    "    LIMIT 1"
        )
    Optional<Person> findByLoginSub(@Param("sub")String sub);
  
     
    @Query(" MATCH (p:Person) "
        + " WHERE id(p)=$idPerson "
        + " RETURN p "
        + " ,[ "
        + " 		[(p)<-[lk:LIKED_BY]-(c:Comment) | [lk,c]] "
        + " ] ")
    Person likesComments(@Param("idPerson")Long idPerson);
  
    Person findByCpfIgnoreCase(String cpf);
  
    @Query("MATCH (c:Comment)-[m:MADE_BY]->(p:Person) "
        + " WHERE id(c)=$idComment "
        + " RETURN p ")
    Person findPersonMadeByIdComment(@Param("idComment")Long idComment);
  
    @Query(" MATCH (c:Comment)-[m:LIKED_BY]->(p:Person) "
        + " WHERE id(c)=$idComment "
        + " RETURN p ")
    List<Person> findPersonLikedByIdComment( @Param("idComment")Long idComment);
  
    @Query("MATCH (p:Person)-[i:IS_AUTHENTICATED_BY]->(a:AuthService) WHERE p.contactEmail=$email AND a.server=$server RETURN p")
    Person validate(@Param("email")String email, @Param("server")String server);
  
    @Query("MATCH (person:Person)-[is_auth_by:IS_AUTHENTICATED_BY]->(auth:AuthService) " +
    "WHERE (is_auth_by.email=$email) " +
    "AND (auth.server=$server OR $server IS NULL) " +
    "AND (person.cpf=$cpf OR $cpf IS NULL OR $cpf='')" +
    "RETURN person"
  )
  Optional<Person> havePersonWithLoginEmail(@Param("email")String email,@Param("server") String server,@Param("cpf") String cpf);
  
  
    @Query(value = " MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
        " MATCH (s)-[:TO]->(c:Conference) " +
        " WHERE (p.name IS NULL OR  apoc.text.clean(p.name) CONTAINS apoc.text.clean($name)) " +
        " AND (p.contactEmail IS NULL OR p.contactEmail CONTAINS ($email)) " +
        " AND ($active IS NULL OR coalesce(p.active,true) = $active) " +
        " AND ($idConference IS NULL OR id(c) = $idConference) " +
        " AND (aut.name IS NULL OR apoc.text.clean(aut.name) CONTAINS apoc.text.clean($authentication)) " +
        " AND (id(loc) IN $locality OR NOT $locality) " +
        " WITH id(p) AS id, apoc.text.capitalizeAll(toLower(p.name)) AS name, p.contactEmail AS email, coalesce(p.active,true) AS active" +
        " RETURN DISTINCT id,  name,  email, active"  
        , countQuery =
        " MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
            "MATCH (s)-[:TO]->(c:Conference) " +
            " WHERE (p.name IS NULL OR apoc.text.clean(p.name) CONTAINS apoc.text.clean($name)) " +
            " AND (p.contactEmail IS NULL OR p.contactEmail CONTAINS ($email)) " +
            " AND ($active IS NULL OR coalesce(p.active,true) = $active) " +
            " AND ($idConference IS NULL OR id(c) = $idConference) " +
            " AND (aut.name IS NULL OR apoc.text.clean(aut.name) CONTAINS apoc.text.clean($authentication)) " +
            " AND (id(loc) IN $locality OR NOT $locality) " +
            " WITH DISTINCT id(p) AS id,  p.name AS name, p.contactEmail AS email, coalesce(p.active,true) AS active " +
            " RETURN COUNT(*) "
    )
    Page<PersonKeepCitizenDto> findPersonKeepCitizen(
        @Param("name") String name,
        @Param("idConference")Long idConference,
        @Param("email") String email,
        @Param("authentication") String authentication,
        @Param("active") Boolean active,
        @Param("locality") List<Long> locality,
        Pageable page
    );
  
  
    @Query("MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person) " +
        "MATCH (au)<-[:USING]-(log:Login)-[:TO]->(c:Conference) " +
        "WHERE ($idConference IS NULL OR id(c) = $idConference) " +
        "AND id(p) = $idPerson " +
        "AND ( $authName = '' OR  apoc.text.clean(aut.name) = $authName) " +
        "RETURN DISTINCT aut.name AS loginName, count(log) AS acesses")
    List<LoginAccessDto> findAccessByPerson(@Param("idConference")Long idConference, @Param("idPerson")Long idPerson, @Param("authName")String authName);
  
  
    @Query("MATCH (p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
        "WHERE id(p) = $idPerson " +
        "MATCH (c:Conference)<-[:TO]-(s) " +
        "WHERE id(c) = $idConference " +
        "OPTIONAL MATCH (p)-[:MADE]->(log:Login)-[:TO]->(c) " +
        "WITH {time: log.time, locality: loc.name, localityId: id(loc)} AS tuple ORDER BY log.time DESC LIMIT 1 " +
        "RETURN tuple.locality AS localityName, tuple.localityId AS localityId")
    LocalityInfoDto findRecentLocalityByPerson(@Param("idConference")Long idConference, @Param("idPerson")Long idPerson);
  
  
    @Query("MATCH (p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
        "WHERE id(p) = $idPerson " +
        "MATCH (c:Conference)<-[:TO]-(s) " +
        "WHERE id(c) = $idConference " +
        "WITH {locality: loc.name, localityId: id(loc)} AS tuple " +
        "RETURN tuple.locality AS localityName, tuple.localityId AS localityId")
    LocalityInfoDto findLocalityByPersonAndConference(@Param("idConference")Long idConference, @Param("idPerson")Long idPerson);

    @Query("MATCH (p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) , (s)-[:TO]->(c:Conference) " +
    "WHERE id(p) = $idPerson " +
    "RETURN loc.name AS localityName, id(loc) AS localityId order by c.endDate desc limit 1")
    LocalityInfoDto findLastLocalityByPerson(@Param("idPerson")Long idPerson);
  
  
    @Query("MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)-[m:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality) " +
    ",(s)-[:TO]->(c:Conference) " +
    "WHERE id(c) = $idConference " +
    "AND id(p) = $idPerson " +
    "RETURN loc.name AS localityName, id(loc) AS localityId")
  LocalityInfoDto findLocalityByPerson(@Param("idConference")Long idConference, @Param("idPerson")Long idPerson);
  
  //add a alteração para que esse cypher pesquisa apenas as autenticação Google e AcessoCidadao
  @Query(value = "WITH split($name,' ') AS search " +
  "UNWIND search AS s " +
  "WITH s AS s2 " +
  "WITH collect(s2) AS s3 " +
  "MATCH (p:Person)-[:MADE]->(sd:SelfDeclaration) " +
  "WHERE ALL(x IN s3 WHERE toLower(p.name) CONTAINS toLower(x)) OR ALL(x IN s3 WHERE p.contactEmail CONTAINS x) " +
  "OPTIONAL MATCH (p)-[cia:CHECKED_IN_AT]->(m:Meeting) " +
  "WHERE id(m) = $idMeeting " +
  "MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)" +
  "WHERE au.server='AcessoCidadao' OR au.server='Google'" +
  "RETURN DISTINCT id(p) AS personId, toLower(p.name) AS name, p.contactEmail AS email, p.telephone AS telephone, " +
  "cia IS NOT NULL AS checkedIn, cia.time AS checkedInDate, p.cpf AS cpf,  COLLECT(DISTINCT au.server) AS authName"
  , countQuery = "WITH split($name,' ') AS search " +
  "UNWIND search AS s " +
  "WITH s AS s2 " +
  "WITH collect(s2) AS s3 " +
  "MATCH (p:Person)-[:MADE]->(sd:SelfDeclaration) " +
  "WHERE ALL(x IN s3 WHERE toLower(p.name) CONTAINS toLower(x)) OR ALL(x IN s3 WHERE p.contactEmail CONTAINS x) " +
  "OPTIONAL MATCH (p)-[cia:CHECKED_IN_AT]->(m:Meeting) " +
  "WHERE id(m) = $idMeeting " +
  "MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person)" +
  "WHERE au.server='AcessoCidadao' OR au.server='Google'" +
  "WITH DISTINCT id(p) AS personId, toLower(p.name) AS name, p.contactEmail AS email, p.telephone AS telephone, " +
  "cia IS NOT NULL AS checkedIn, cia.time AS checkedInDate, p.cpf AS cpf,  COLLECT(DISTINCT au.server) AS authName " +
  "RETURN COUNT(*)")
  Page<PersonMeetingDto> findPersonForMeeting(@Param("idMeeting")Long idMeeting, @Param("name")String name, Pageable pageable);

  @Query(
    "WITH $idMeeting AS mId, split($name, ' ') AS search " +
               "UNWIND search AS s " +
               "WITH mId, collect(s) AS cs " +
               "MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person) " +
               "WHERE (au.server = 'AcessoCidadao' OR au.server = 'Google') AND (" +
               "ALL(x IN cs WHERE apoc.text.clean(p.name) CONTAINS apoc.text.clean(x)) " +
               "OR ALL(x IN cs WHERE apoc.text.clean(p.contactEmail) CONTAINS apoc.text.clean(x))) " +
               "AND ($sub IS NULL OR aut.idByAuth = $sub ) " +
               "AND ($cEmail IS NULL OR p.contactEmail = $cEmail) " +
               "OPTIONAL MATCH (p)-[cia:CHECKED_IN_AT]->(m:Meeting) " +
               "WHERE id(m) = mId " +
               "CALL { " +
               "    WITH p " +
               "    OPTIONAL MATCH (p)-[:MADE]->(sd:SelfDeclaration)-[:TO]->(c:Conference), " +
               "    (sd)-[ab:AS_BEING_FROM]->(l:Locality)-[ot:OF_TYPE]-(lt:LocalityType) " +
               "    OPTIONAL MATCH (l)-[:IS_LOCATED_IN]->(sl:Locality) " +
               "    RETURN l.name AS locality, sl.name AS superLocality, id(sl) AS superLocalityId, lt.name AS regionalizable " +
               "    ORDER BY c.beginDate DESC LIMIT 1 " +
               "} " +
               "RETURN DISTINCT " +
               "       id(p) AS personId, " +
               "       locality, " +
               "       superLocality, " +
               "       superLocalityId, " +
               "       regionalizable, " +
               "       toLower(p.name) AS name, " +
               "       p.contactEmail AS email, " +
               "       p.telephone AS telephone, " +
               "       cia.time IS NOT NULL AS checkedIn, " +
               "       cia.time AS checkedInDate, " +
               "       COLLECT(DISTINCT au.server) AS authName " +
               "ORDER BY name ASC")
    List<PersonMeetingDto> findPersonByNameForMeeting(@Param("idMeeting")Long idMeeting, @Param("name")String name, @Param("sub")String sub, @Param("cEmail")String cEmail);
      
  @Query(
    "MATCH (loc:Locality)<-[:AS_BEING_FROM]-(sfd:SelfDeclaration)<-[:MADE]- (p:Person)-[ci:CHECKED_IN_AT {isAuthority: true, toAnnounce: true}]->(m:Meeting)\n" +
    "WHERE id(m) = $idMeeting AND ci.time IS NOT NULL\n" +
    "RETURN DISTINCT\n" +
    "    id(p) AS idPerson,\n" +
    "    id(ci) AS idCheckIn,\n" +
    "    ci.time AS checkInTime,\n" +
    "    loc.name AS localityName,\n" +
    "    COALESCE(ci.isAnnounced, false) AS announced, \n" +
    "    p.name AS name, \n" +
    "    ci.role AS role,  \n" +
    "    ci.organization as organization,\n" +
    "    ci\n" +
    "ORDER BY \n" +
    "    apoc.coll.max([\n" +
    "        apoc.text.levenshteinSimilarity($name, p.name),\n" +
    "        apoc.text.levenshteinSimilarity($name, ci.role),\n" +
    "        apoc.text.levenshteinSimilarity($name, ci.organization)\n" +
    "    ]) DESC, \n" +
    "    (CASE WHEN NOT coalesce(ci.isAnnounced, false) THEN 0 ELSE 1 END) ASC,\n" +
    "    ci.time ASC")
    List<AuthorityMeetingDto> findAuthorityByNameForMeeting(Long idMeeting, String name);
      
    @Query(
      "MATCH (m:Meeting)-[:OCCURS_IN]->(c:Conference) " +
          "WHERE id(m)=$idMeeting " +
          "MATCH (p:Person)-[:MADE]->(log:Login)-[:TO]->(c)<-[:TO]-(sd:SelfDeclaration)<-[:MADE]-(p) " +
          "WHERE id(p) = $idPerson " +
          "MATCH (sd)-[abf:AS_BEING_FROM]->(l:Locality)-[ot:OF_TYPE]-(lt:LocalityType) " +
          "OPTIONAL MATCH (l)-[ili2:IS_LOCATED_IN]->(loc2:Locality)-[ot2:OF_TYPE]->(lt2:LocalityType) " +
          "RETURN l.name AS locality, loc2.name AS superLocality, id(loc2) AS superLocalityId, " +
          "lt.name AS regionalizable " +
          "ORDER BY log.time DESC LIMIT 1")
  LocalityRegionalizableDto findMostRecentLocality(@Param("idPerson")Long idPerson, @Param("idMeeting")Long idMeeting);
  
  @Query(
      "MATCH (m:Meeting)-[:OCCURS_IN]->(c:Conference) " +
          "WHERE id(m)=$idMeeting " +
          "MATCH (c)<-[:TO]-(sd:SelfDeclaration)<-[:MADE]-(p) " +
          "WHERE id(p) = $idPerson " +
          "MATCH (sd)-[abf:AS_BEING_FROM]->(l:Locality)-[ot:OF_TYPE]-(lt:LocalityType) " +
          "OPTIONAL MATCH (l)-[ili2:IS_LOCATED_IN]->(loc2:Locality)-[ot2:OF_TYPE]->(lt2:LocalityType) " +
          "RETURN l.name AS locality, loc2.name AS superLocality, id(loc2) AS superLocalityId, " +
          "lt.name AS regionalizable " +
          "ORDER BY p.name DESC LIMIT 1")
  LocalityRegionalizableDto findLocalityIfThereIsNoLogin(@Param("idPerson")Long idPerson, @Param("idMeeting")Long idMeeting);

    @Query(
        value = 
            " CALL {\r\n" + //
            "  MATCH (m:Meeting)-[:PRE_REGISTRATION|CHECKED_IN_AT*1..2]-(p:Person)\r\n" + //
            "  WHERE id(m) = $idMeeting \r\n" + //
            "  OPTIONAL MATCH (p)-[cia:CHECKED_IN_AT]->(m)\r\n" + //
            "  OPTIONAL MATCH (p)<-[prrel1:PRE_REGISTRATION]-(pr:PreRegistration)-[prrel2:PRE_REGISTRATION]->(m)\r\n" + //
            "  RETURN *\r\n" + //
            "} WITH *\r\n" + //
            "WHERE \r\n" + //
            "  (\r\n" + //
            "    $name IS NULL OR\r\n" + //
            "    apoc.text.clean(p.name) CONTAINS apoc.text.clean($name)  OR\r\n" + //
            "    apoc.text.clean(COALESCE(cia, pr).organization) CONTAINS apoc.text.clean($name) OR\r\n" + //
            "    apoc.text.clean(COALESCE(cia, pr).role) CONTAINS apoc.text.clean($name)\r\n" + //
            "  ) AND\r\n" + //
            "  (CASE\r\n" + //
            "    WHEN $filter = 'pres' THEN cia.time IS NOT NULL\r\n" + //
            "    WHEN $filter = 'prereg' THEN pr IS NOT NULL\r\n" + //
            "    WHEN $filter = 'prereg_pres' THEN cia.time IS NOT NULL AND pr IS NOT NULL\r\n" + //
            "    WHEN $filter = 'prereg_notpres' THEN cia.time IS NULL AND pr IS NOT NULL\r\n" + //
            "    WHEN $filter = 'notprereg_pres' THEN cia.time IS NOT NULL AND pr IS NULL\r\n" + //
            "    ELSE FALSE end) and\r\n" + //
            "  ( case\n" +
            "    when $status = 'screening' then (cia.isAuthority and not coalesce(cia.toAnnounce, false))\n" +
            "    when $status = 'toAnnounce' then (coalesce(cia.toAnnounce, false) and not coalesce(cia.isAnnounced, false))\n" +
            "    when $status = 'announced' then coalesce(cia.isAnnounced, false)\n" +
            "    else true end\n" +
            "  ) and\r\n" + //
            "    ($filterIsAuthotity is null or $filterIsAuthotity = coalesce(cia.isAuthority, pr.isAuthority, false))\r\n" + //
            "OPTIONAL MATCH (p)-[md:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality)\r\n" + //
            "WHERE ((loc IS NOT NULL AND id(loc) IN $localities) OR NOT $localities)\r\n" + //
            "RETURN DISTINCT \r\n" + //
            "  id(p) AS personId,\r\n" + //
            "  id(cia) AS checkInId,\r\n" + //
            "  toLower(p.name) AS name,\r\n" + //
            "  p.contactEmail AS email,\r\n" + //
            "  p.telehpone AS telephone,\r\n" + //
            "  cia.time AS checkedInDate,\r\n" + //
            "  coalesce(coalesce(cia, pr).isAuthority, false) AS isAuthority,\r\n" + //
            "  coalesce(coalesce(cia, pr).role, '') AS role,\r\n" + //
            "  coalesce(coalesce(cia, pr).organization, '') AS organization,\r\n" + //
            "  coalesce(coalesce(cia, pr).isAnnounced, false) AS isAnnounced,  \r\n" + //
            "  coalesce(coalesce(cia, pr).toAnnounce, false) AS toAnnounce,\r\n" + //
            "  pr.created AS preRegisteredDate\r\n" + //
            "order by (\r\n" + //
            "  case\r\n" + //
            "    when $sort = 'name' then apoc.text.clean(name)\r\n" + //
            "    when $sort = 'checkedInDate' then checkedInDate\r\n" + //
            "    when $sort = 'status' then (\r\n" + //
            "      case\r\n" + //
            "        when (isAuthority and not toAnnounce) then 0\r\n" + //
            "        when (toAnnounce and not isAnnounced) then 1\r\n" + //
            "        when (toAnnounce and isAnnounced) then 2\r\n" + //
            "        else 3 end\r\n" + //
            "    ) end \r\n" + //
            ") asc, cia.time ASC",
        countQuery = 
            "CALL {\r\n" + //
            "  MATCH (m:Meeting)-[:PRE_REGISTRATION|CHECKED_IN_AT*1..2]-(p:Person)\r\n" + //
            "  WHERE id(m) = $idMeeting AND\r\n" + //
            "        ($name IS NULL OR apoc.text.clean(p.name) CONTAINS apoc.text.clean($name))\r\n" + //
            "  OPTIONAL MATCH (p)-[cia:CHECKED_IN_AT]->(m)\r\n" + //
            "  OPTIONAL MATCH (p)<-[prrel1:PRE_REGISTRATION]-(pr:PreRegistration)-[prrel2:PRE_REGISTRATION]->(m)\r\n" + //
            "  RETURN *\r\n" + //
            "}\r\n" + //
            "WITH *\r\n" + //
            "WHERE \r\n" + //
            "  (\r\n" + //
            "    $name IS NULL OR\r\n" + //
            "    apoc.text.clean(p.name) CONTAINS apoc.text.clean($name)  OR\r\n" + //
            "    apoc.text.clean(COALESCE(cia, pr).organization) CONTAINS apoc.text.clean($name) OR\r\n" + //
            "    apoc.text.clean(COALESCE(cia, pr).role) CONTAINS apoc.text.clean($name)\r\n" + //
            "  ) AND\r\n" + //
            "  (CASE\r\n" + //
            "    WHEN $filter = 'pres' THEN cia IS NOT NULL\r\n" + //
            "    WHEN $filter = 'prereg' THEN pr IS NOT NULL\r\n" + //
            "    WHEN $filter = 'prereg_pres' THEN cia IS NOT NULL AND pr IS NOT NULL\r\n" + //
            "    WHEN $filter = 'prereg_notpres' THEN cia IS NULL AND pr IS NOT NULL\r\n" + //
            "    WHEN $filter = 'notprereg_pres' THEN cia IS NOT NULL AND pr IS NULL\r\n" + //
            "    ELSE FALSE end) and\r\n" + //
            "  ( case\n" +
            "    when $status = 'screening' then (cia.isAuthority and not coalesce(cia.toAnnounce, false))\n" +
            "    when $status = 'toAnnounce' then (coalesce(cia.toAnnounce, false) and not coalesce(cia.isAnnounced, false))\n" +
            "    when $status = 'announced' then coalesce(cia.isAnnounced, false)\n" +
            "    else true end\n" +
            "  ) and\r\n" + //
            "    ($filterIsAuthotity is null or $filterIsAuthotity = coalesce(cia.isAuthority, pr.isAuthority, false))\r\n" + //
            "OPTIONAL MATCH (p)-[md:MADE]->(s:SelfDeclaration)-[a:AS_BEING_FROM]->(loc:Locality)\r\n" + //
            "WHERE ((loc IS NOT NULL AND id(loc) IN $localities) OR NOT $localities)\r\n" + //
            "WITH DISTINCT id(p) AS personId, toLower(p.name) AS name, p.contactEmail AS email, p.telehpone AS telephone, " +
            "cia.time AS checkedInDate, pr.created AS preRegisteredDate " +
            "RETURN COUNT(*)"
    )
    List<PersonMeetingFilteredDto> findPersonsOnMeeting(
            Long idMeeting, 
            List<Long> localities, 
            String name,
            String sort,
            String filter,
            Boolean filterIsAuthotity,
            String status
    );
  
    @Query("MATCH(person:Person)-[authBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
           "WHERE id(person) = $idPerson " +
           "AND authBy.authType <> 'participeCpf' " +
           "RETURN DISTINCT authBy.email AS email")
    List<PersonProfileEmailsDto> findPersonEmails(@Param("idPerson")Long idPerson);
  
    @Query("MATCH " +
        "(person:Person)-[is_auth_by:IS_AUTHENTICATED_BY]->(auth:AuthService), " +
        "(auth)<-[using:USING]-(login:Login), " +
        "(login)-[to:TO]->(conference:Conference) " +
        "WHERE (is_auth_by.email=$email) " +
        "AND (person.cpf=$cpf OR $cpf IS NULL) " +
        "RETURN person, conference, auth, login"
    )
    Person findPersonByParticipeAuthServiceEmailOrCpf(@Param("email")String email, @Param("cpf")String cpf);
    
    @Query("MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person) " +
    "MATCH (au)<-[:USING]-(log:Login)-[:TO]->(c:Conference) " +
    "WHERE id(p)= $idPerson " +
    "RETURN DISTINCT c.name "
    )
    List<String> findPersonConferenceList(@Param("idPerson")Long idPerson);

    @Query("MATCH (au:AuthService)<-[aut:IS_AUTHENTICATED_BY]-(p:Person) " +
    "WHERE id(p)= $idPerson " +
    "return au.server AS authName " 
    )
    List<String> findPersonAutenticated(@Param("idPerson")Long idPerson);
  
  }
 