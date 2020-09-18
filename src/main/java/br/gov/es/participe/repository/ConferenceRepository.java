package br.gov.es.participe.repository;

import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ConferenceRepository extends Neo4jRepository<Conference, Long> {
    
    @Query("MATCH (n:Conference)-[cp:TARGETS]->(p:Plan) "
            + " WHERE ext.translate(n.name) CONTAINS ext.translate({0}) "
            + " AND (ID(p) = {1} OR {1} IS NULL) "
            + " AND ((datetime(n.beginDate).year = {3} OR {3} IS NULL) OR (datetime(n.endDate).year = {3} OR {3} IS NULL))"
            + " AND ((datetime(n.beginDate).month = {2} OR {2} IS NULL) OR (datetime(n.endDate).month = {2} OR {2} IS NULL))"
            + " RETURN n,cp,p "
            + ", [ "
            + "		[(n)-[fep:FEATURES_PARTICIPATION_IMAGE]-(fp:File) | [fep, fp]], "
            + "		[(n)-[fea:FEATURES_AUTHENTICATION_IMAGE]-(fa:File) | [fea, fa]], "
            + "		[(p)-[r:REGIONALIZABLE]-(lt:LocalityType) | [r, lt]], "
            + "		[(n)-[lo:LOCALIZES_CITIZEN_BY]-(l:LocalityType) | [lo, l]] "
            + " ] ORDER BY n.beginDate")
    Collection<Conference> findAllByQuery(String name, Long plan, Integer month, Integer year);

    @Query("MATCH (n:Conference) "
            + " WHERE datetime(n.beginDate) <= datetime({0}) "
            + " AND datetime(n.endDate) >= datetime({0}) "
            + " RETURN n, [(n)-[md:MODERATORS]->(p:Person) |[n, md, p] ] "
            + " ORDER BY n.beginDate")
    Collection<Conference> findAllActives(Date date);

    Conference findByNameIgnoreCase(String name);

    @Query("MATCH (c:Conference)-[md:MODERATORS]->(p:Person) " +
            "WHERE id(c) = {0} " +
            "RETURN p")
    Collection<Person> findModeratorsById(Long id);

    @Query("MATCH (c:Conference) DETACH DELETE c")
    void deleteAll();

    @Query("MATCH (c:Conference)<-[t:TO]-(s:SelfDeclaration) "
            + "WHERE id(c) = {0} "
            + "RETURN count(s)")
    Integer countSelfDeclarationById(Long id);

    @Query("MATCH (c:Conference)-[:TARGETS]->(p:Plan) WHERE ID(p)={0} RETURN c;")
    List<Conference> findByPlan(Long id);

    @Query("MATCH (n:Conference)<-[oi:OCCURS_IN]-(m:Meeting) "
            + "OPTIONAL MATCH (m)<-[rel]-(p:Person) "
            + "OPTIONAL MATCH (m)-[tpa:TAKES_PLACE_AT]->(l:Locality) "
            + "WHERE {0} IS NULL OR (n.beginDate <= {0} AND n.endDate >= {0}) AND "
            + "({1} IS NULL) OR ({1} IS NOT NULL AND id(p)={1}) "
            + "RETURN n,oi,m,tpa,l "
            + "ORDER BY n.beginDate")
    Collection<Conference> findAllWithMeeting(Date date, Long idPerson);
}
