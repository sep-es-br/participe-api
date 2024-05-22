package br.gov.es.participe.repository;

import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ConferenceRepository extends Neo4jRepository<Conference, Long> {

        @Query("MATCH (n:Conference)-[cp:TARGETS]->(p:Plan) "
                        + " WHERE n.name CONTAINS ($name) "
                        + " AND (id(p) = $plan OR $plan IS NULL) "
                        + " AND ((datetime(n.beginDate).year = $year OR $year IS NULL) OR (datetime(n.endDate).year = $year OR $year IS NULL))"
                        + " AND ((datetime(n.beginDate).month = $month OR $month IS NULL) OR (datetime(n.endDate).month = $month OR $month IS NULL))"
                        + " RETURN n,cp,p "
                        + ", [ "
                        + "		[(n)-[fep:FEATURES_PARTICIPATION_IMAGE]-(fp:File) | [fep, fp]], "
                        + "		[(n)-[fea:FEATURES_AUTHENTICATION_IMAGE]-(fa:File) | [fea, fa]], "
                        + "		[(p)-[r:REGIONALIZABLE]-(lt:LocalityType) | [r, lt]], "
                        + "		[(n)-[lo:LOCALIZES_CITIZEN_BY]-(l:LocalityType) | [lo, l]] "
                        + " ] ORDER BY n.beginDate")
        Collection<Conference> findAllByQuery( @Param("name") String name, @Param("plan") Long plan, @Param("month") Integer month, @Param("year") Integer year);

        @Query("MATCH (n:Conference) "
                        + " WHERE NOT $active OR (datetime(n.beginDate) <= datetime($date) "
                        + " AND datetime(n.endDate) >= datetime($date)) "
                        + " RETURN n, [(n)-[md:MODERATORS]->(p:Person) |[n, md, p] ] "
                        + " ORDER BY n.beginDate")
        Collection<Conference> findAllActives( @Param("date") Date date,  @Param("active") Boolean active);

        Conference findByNameIgnoreCase( @Param("name") String name);

        @Query("MATCH (c:Conference)-[md:MODERATORS]->(p:Person) " +
                        "WHERE id(c) = $id " +
                        "RETURN p")
        Collection<Person> findModeratorsById( @Param("id") Long id);

        @Query("MATCH (c:Conference) DETACH DELETE c")
        void deleteAll();

        @Query("MATCH (c:Conference)<-[t:TO]-(s:SelfDeclaration) "
                        + "WHERE id(c) = $id "
                        + "RETURN count(s)")
        Integer countSelfDeclarationById( @Param("id") Long id);

        @Query("MATCH (c:Conference)-[:TARGETS]->(p:Plan) WHERE id(p)=$id RETURN c;")
        List<Conference> findByPlan( @Param("id") Long id);

        @Query("MATCH (n:Conference)<-[oi:OCCURS_IN]-(m:Meeting) "
                        + "OPTIONAL MATCH (m)<-[rel]-(p:Person) "
                        + "OPTIONAL MATCH (m)-[tpa:TAKES_PLACE_AT]->(l:Locality) "
                        + "WHERE $date IS NULL OR (n.beginDate <= $date AND n.endDate >= $date) AND "
                        + "($idPerson IS NULL) OR ($idPerson IS NOT NULL AND id(p)=$idPerson) "
                        + "RETURN n,oi,m,tpa,l "
                        + "ORDER BY n.beginDate")
        Collection<Conference> findAllWithMeeting( @Param("date") Date date, @Param("idPerson") Long idPerson);

        @Query("MATCH (conference:Conference)<-[occurs_in:OCCURS_IN]-(meeting:Meeting)-[tpa:TAKES_PLACE_AT]->(locality:Locality) " +
                "WHERE (meeting.typeMeetingEnum IS NOT NULL AND meeting.typeMeetingEnum <> 'VIRTUAL') " +
                "RETURN conference, occurs_in, meeting, tpa, locality " +
                "ORDER BY conference.name, meeting.name ")
        Collection<Conference> findAllOpenWithPresentialMeeting4Admins();

        @Query("MATCH (conference:Conference)<-[occurs_in:OCCURS_IN]-(meeting:Meeting)-[tpa:TAKES_PLACE_AT]->(locality:Locality), " +
                     "(person:Person)-[:IS_RECEPTIONIST_OF]->(meeting) " +
                "WHERE conference.displayMode CONTAINS 'OPEN' " +
                "AND (meeting.typeMeetingEnum <> 'VIRTUAL') " +
                "AND ($date >= left(meeting.beginDate,10) AND $date <= meeting.endDate) " +
                "AND (id(person)=$idPerson) " +
                "RETURN conference, occurs_in, meeting, tpa, locality, person " +
                "ORDER BY conference.name ")
        Collection<Conference> findAllOpenWithPresentialMeeting4Receptionists( @Param("date") Date date, @Param("idPerson") Long idPerson);

        @Query("MATCH (n: Conference ) " +
                        "WHERE id(n) = $id " +
                        "WITH n RETURN n," +
                        "[ " +
                        "  [ (n)<-[r_c1: CONFERENCE_COLOR]-(c1: ConferenceColor ) | [ r_c1, c1 ] ]  "+
                        ", [(n)-[r_f1: FEATURES_PARTICIPATION_IMAGE ]->(f1: File ) | [ r_f1, f1 ] ]" +
                        ", [ (n)-[r_f1: FEATURES_AUTHENTICATION_IMAGE ]->(f1: File ) | [ r_f1, f1 ] ]" +
                        ", [(n)-[r_f1: FEATURES_FOOTER_IMAGE ]->(f1: File ) | [ r_f1, f1 ] ]" +
                        ", [ (n)<-[r_f2: IS_BACKGROUND_IMAGE_OF ]-(f2: File ) | [ r_f2, f2 ] ]" +
                        ", [ (n)<-[r_f3: IS_CALENDAR_IMAGE_OF ]-(f3: File ) | [ r_f3, f3 ] ]" +
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
        Optional<Conference> findByIdFull( @Param("id") Long id);

        @Query("MATCH (c:Conference) WHERE c.displayMode STARTS WITH 'AUTOMATIC' RETURN c")
        List<Conference> findAllAutomatic();

        @Query("MATCH (c:Conference) WHERE id(c) = $id RETURN c.postClosure")
        String findPostClosureByIdConference( @Param("id") Long id);

        @Query("MATCH (c:Conference) WHERE ($id IS NULL OR id(c) = $id) AND c.name = $name RETURN c")
        Conference validateName( @Param("name") String name, @Param("id")  Long id);
}
