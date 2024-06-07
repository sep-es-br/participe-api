package br.gov.es.participe.repository;

import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends Neo4jRepository<Meeting, Long> {

       String MEETING_FILTER = "ID(conference) = $idConference " +
       "AND (meeting.name CONTAINS $name OR $name IS NULL) " +
       "AND (ID(locality) IN $localities OR $localities IS NULL) " +
       "AND ((datetime(meeting.beginDate) >= datetime($beginDate) OR $beginDate IS NULL) OR " +
       "(datetime(meeting.endDate) >= datetime($beginDate) OR $beginDate IS NULL)) " +
       "AND ( (datetime(meeting.beginDate) <= datetime($endDate) OR $endDate IS NULL) OR " +
       "(datetime(meeting.endDate) <= datetime($endDate) OR $endDate IS NULL)) ";

@Query(" MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) " + " WHERE id(c) = $idConference " + " RETURN m, oc, c" + " , ["
+ " 		[(m)-[tp:TAKES_PLACE_AT]->(lp:Locality) | [tp, lp] ],"
+ "	 	[(m)-[co:COVERS]-(lc:Locality) | [co, lc] ],"
+ "		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]" + " ] " + "ORDER BY m.beginDate")
Collection<Meeting> findAll( @Param("idConference") Long idConference);

@Query(
value =
"MATCH (meeting:Meeting)-[occurs_in:OCCURS_IN]->(conference:Conference), " +
"(meeting:Meeting)-[takes_place:TAKES_PLACE_AT]->(locality:Locality) " +
"WHERE " +
MEETING_FILTER +
"RETURN meeting, occurs_in, conference, takes_place, locality, " +
"[ " +
"  [(meeting)-[covers:COVERS]->(_locality:Locality) | [covers, _locality] ], " +
"  [(meeting)<-[is_receptionist_of:IS_RECEPTIONIST_OF]-(receptionist:Person) | [is_receptionist_of, receptionist]], " +
"  [(meeting)<-[is_channel_of:IS_CHANNEL_OF]-(channel:Channel) | [is_channel_of, channel]] " +
"] " +
"ORDER BY meeting.beginDate",
countQuery =
"MATCH (meeting:Meeting)-[occurs_in:OCCURS_IN]->(conference:Conference), " +
"(meeting:Meeting)-[takes_place:TAKES_PLACE_AT]->(locality:Locality) " +
"OPTIONAL MATCH (meeting)<-[is_channel_of:IS_CHANNEL_OF]-(channel:Channel) " +
"OPTIONAL MATCH (meeting)<-[is_receptionist_of:IS_RECEPTIONIST_OF]-(receptionist:Person) " +
"WITH meeting, occurs_in, conference, is_channel_of, takes_place, locality, channel, receptionist, is_receptionist_of " +
"WHERE " +
MEETING_FILTER +
"RETURN COUNT(DISTINCT meeting)"
)
Page<Meeting> findAll(
@Param("idConference") Long idConference,
@Param("name") String name,
@Param("beginDate") Date beginDate,
@Param("endDate") Date endDate,
@Param("localities") List<Long> localities,
@Param ("pageable") Pageable pageable
);

@Query(" MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) " + " WHERE id(c) = $idConference " + " RETURN m " + " , ["
+ " 		[(m)-[tp:TAKES_PLACE_AT]->(lp:Locality) | [tp, lp] ],"
+ "	 	[(m)-[co:COVERS]-(lc:Locality) | [co, lc] ]" + " ] " + "ORDER BY m.beginDate")
Collection<Meeting> findAllDashboard( @Param("idConference") Long idConference);

@Query("MATCH (m:Meeting) WHERE id(m) = $id WITH m RETURN m, ["
+ "		[(m)-[tp:TAKES_PLACE_AT]-(lp:Locality) | [tp,lp]],"
+ "		[(m)-[isChannelOf:IS_CHANNEL_OF]-(channel:Channel) | [isChannelOf, channel]],"
+ "		[(m)-[itemPlanOf:IS_PLAN_ITEM_OF]-(planItem:PlanItem) | [itemPlanOf, planItem]],"
+ "		[(m)-[co:COVERS]-(lc:Locality) | [co, lc]],"
+ "		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]"
+ " ]"
)
Optional<Meeting> findMeetingWithoutConference( @Param("id") Long id);

@Query("MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) " + " WHERE id(m) = $id " + " RETURN m, oc, c " + " , ["
+ "		[(m)-[tp:TAKES_PLACE_AT]-(lp:Locality) | [tp,lp]],"
+ "		[(m)-[co:COVERS]-(lc:Locality) | [co, lc]],"
+ "		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]" + " ]")
Optional<Meeting> findMeetingWithRelationshipsById( @Param("id") Long id);

@Query("MATCH (person:Person)-[checkedIn:CHECKED_IN_AT]->(meeting:Meeting) " +
"WHERE id(person)=$personId " +
"RETURN person, checkedIn, meeting"
)
List<CheckedInAt> findAllPersonCheckedIn( @Param("personId") Long personId);

@Query("MATCH (m:Meeting) " + 
"where id(m)=$id " + 
"RETURN datetime(m.endDate) + duration({hours: +1}) > datetime()"
)
Boolean selfCheckInIsOpen(@Param("id") Long id);

@Query("MATCH (m:Meeting) " + 
"where id(m)=$id " + 
"RETURN datetime(m.beginDate) > datetime()"
)
Boolean preRegistrationIsOpenAndMeetingStarted(@Param("id") Long id);

@Query("MATCH (m:Meeting) " + 
"where id(m)=$id " + 
"RETURN datetime(m.endDate) > datetime()"
)
Boolean preRegistrationIsOpenAndMeetingClosed(@Param("id") Long id);

}
