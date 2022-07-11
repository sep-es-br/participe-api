package br.gov.es.participe.repository;

import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ConferenceRepository extends Neo4jRepository<Conference, Long> {

        @Query("MATCH (n:Conference)-[cp:TARGETS]->(p:Plan) "
                        + " WHERE ext.translate(n.name) CONTAINS ext.translate({0}) "
                        + " AND (id(p) = {1} OR {1} IS NULL) "
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
                        + " WHERE NOT {1} OR (datetime(n.beginDate) <= datetime({0}) "
                        + " AND datetime(n.endDate) >= datetime({0})) "
                        + " RETURN n, [(n)-[md:MODERATORS]->(p:Person) |[n, md, p] ] "
                        + " ORDER BY n.beginDate")
        Collection<Conference> findAllActives(Date date, Boolean active);

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

        @Query("MATCH (c:Conference)-[:TARGETS]->(p:Plan) WHERE id(p)={0} RETURN c;")
        List<Conference> findByPlan(Long id);

        @Query("MATCH (n:Conference)<-[oi:OCCURS_IN]-(m:Meeting) "
                        + "OPTIONAL MATCH (m)<-[rel]-(p:Person) "
                        + "OPTIONAL MATCH (m)-[tpa:TAKES_PLACE_AT]->(l:Locality) "
                        + "WHERE {0} IS NULL OR (n.beginDate <= {0} AND n.endDate >= {0}) AND "
                        + "({1} IS NULL) OR ({1} IS NOT NULL AND id(p)={1}) "
                        + "RETURN n,oi,m,tpa,l "
                        + "ORDER BY n.beginDate")
        Collection<Conference> findAllWithMeeting(Date date, Long idPerson);

        @Query("MATCH (conference:Conference)<-[occurs_in:OCCURS_IN]-(meeting:Meeting) " +
                        "WHERE conference.displayMode CONTAINS 'OPEN' " +
                        "AND (meeting.typeMeetingEnum IS NOT NULL AND meeting.typeMeetingEnum <> 'VIRTUAL') " +
                        "MATCH (meeting)-[tpa:TAKES_PLACE_AT]->(locality:Locality)  " +
                        "WHERE {0} IS NULL OR ( {0} >= meeting.beginDate AND {0} <= meeting.endDate) " +
                        "MATCH (person:Person)-[:IS_RECEPTIONIST_OF *0..]->(meeting) " +
                        "WHERE ({1} IS NULL) OR ({1} IS NOT NULL AND id(person)={1}) " +
                        "RETURN conference, occurs_in, meeting, tpa, locality, person " +
                        "ORDER BY conference.name ")
        Collection<Conference> findAllWithPresentialMeeting(Date date, Long idPerson);

        @Query("MATCH (n: Conference ) " +
                        "WHERE id(n) = {0} " +
                        "WITH n RETURN n," +
                        "[ " +
                        "[ (n)-[r_f1: FEATURES_PARTICIPATION_IMAGE ]->(f1: File ) | [ r_f1, f1 ] ]" +
                        ", [ (n)-[r_f1: FEATURES_AUTHENTICATION_IMAGE ]->(f1: File ) | [ r_f1, f1 ] ]" +
                        ", [ (n)-[r_f2: IS_BACKGROUND_IMAGE_OF ]->(f2: File ) | [ r_f2, f2 ] ]" +
                        ", [ (n)-[resea: APPLIES_TO ]-(research: Research) | [ resea, research ] ]" +
                        ", [ (n)<-[g_h_p: GUIDES_HOW_TO_PARTICIPE_IN ]-(topic: Topic) | [ g_h_p, topic] ]" +
                        ", [ (n)-[r_i1: IS_SEGMENTABLE_BY ]->(s1: StructureItem ) | [ r_i1, s1 ] ]" +
                        ", [ (n)-[r_i1: IS_DEFAULT ]-(p1: PortalServer ) | [ r_i1, p1 ] ]" +
                        ", [ (n)-[r_t1: TARGETS ]->(p1: Plan ) | [ r_t1, p1 ] ]" +
                        ", [ (p1)-[reg: APPLIES_TO]->(dom:Domain) | [reg, dom]]" +
                        ", [ (p1)-[ob: OBEYS]->(str:Structure) | [ob, str]]" +
                        ", [ (p1)-[reg: REGIONALIZABLE]->(locality:LocalityType) | [reg, locality] ] " +
                        ", [ (n)-[r_l1: LOCALIZES_CITIZEN_BY ]->(l1: LocalityType ) | [ r_l1, l1 ] ]" +
                        ", [ (n)<-[r_o1: OCCURS_IN ]-(m1: Meeting ) | [ r_o1, m1 ] ]" +
                        ", [ (n)-[r_m1: MODERATORS ]->(p1: Person ) | [ r_m1, p1 ] ]" +
                        ", [ (n)<-[r_m1: MADE ]-(s1: SelfDeclaration ) | [ r_m1, s1 ] ] " +
                        "]")
        Optional<Conference> findByIdFull(Long id);

        @Query("MATCH (c:Conference) WHERE c.displayMode STARTS WITH 'AUTOMATIC' RETURN c")
        List<Conference> findAllAutomatic();

        @Query("MATCH (c:Conference) WHERE id(c) = {0} RETURN c.postClosure")
        String findPostClosureByIdConference(Long id);

        @Query("MATCH (c:Conference) WHERE ({1} IS NULL OR id(c) = {1}) AND c.name = {0} RETURN c")
        Conference validateName(String name, Long id);
}
