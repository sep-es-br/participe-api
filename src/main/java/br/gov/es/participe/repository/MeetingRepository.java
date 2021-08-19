package br.gov.es.participe.repository;

import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends Neo4jRepository<Meeting, Long> {

  String MEETING_FILTER = "ID(conference) = {0} " +
                          "AND (ext.translate(meeting.name) CONTAINS ext.translate({1}) OR {1} IS NULL) " +
                          "AND (ID(locality) IN {4} OR {4} IS NULL) " +
                          "AND ((datetime(meeting.beginDate) >= datetime({2}) OR {2} IS NULL) OR " +
                          "(datetime(meeting.endDate) >= datetime({2}) OR {2} IS NULL)) " +
                          "AND ( (datetime(meeting.beginDate) <= datetime({3}) OR {3} IS NULL) OR " +
                          "(datetime(meeting.endDate) <= datetime({3}) OR {3} IS NULL)) ";

  @Query(" MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) " + " WHERE id(c) = {0} " + " RETURN m, oc, c" + " , ["
         + " 		[(m)-[tp:TAKES_PLACE_AT]->(lp:Locality) | [tp, lp] ],"
         + "	 	[(m)-[co:COVERS]-(lc:Locality) | [co, lc] ],"
         + "		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]" + " ] " + "ORDER BY m.beginDate")
  Collection<Meeting> findAll(Long idConference);

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
    Long idConference,
    String name,
    Date beginDate,
    Date endDate,
    List<Long> localities,
    Pageable pageable
  );

  @Query(" MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) " + " WHERE id(c) = {0} " + " RETURN m " + " , ["
         + " 		[(m)-[tp:TAKES_PLACE_AT]->(lp:Locality) | [tp, lp] ],"
         + "	 	[(m)-[co:COVERS]-(lc:Locality) | [co, lc] ]" + " ] " + "ORDER BY m.beginDate")
  Collection<Meeting> findAllDashboard(Long idConference);

  @Query("MATCH (m:Meeting) WHERE id(m) = {0} WITH m RETURN m, ["
         + "		[(m)-[tp:TAKES_PLACE_AT]-(lp:Locality) | [tp,lp]],"
         + "		[(m)-[isChannelOf:IS_CHANNEL_OF]-(channel:Channel) | [isChannelOf, channel]],"
         + "		[(m)-[itemPlanOf:IS_PLAN_ITEM_OF]-(planItem:PlanItem) | [itemPlanOf, planItem]],"
         + "		[(m)-[co:COVERS]-(lc:Locality) | [co, lc]],"
         + "		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]"
         + " ]"
  )
  Optional<Meeting> findMeetingWithoutConference(Long id);

  @Query("MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) " + " WHERE id(m) = {0} " + " RETURN m, oc, c " + " , ["
         + "		[(m)-[tp:TAKES_PLACE_AT]-(lp:Locality) | [tp,lp]],"
         + "		[(m)-[co:COVERS]-(lc:Locality) | [co, lc]],"
         + "		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]" + " ]")
  Optional<Meeting> findMeetingWithRelationshipsById(Long id);

  @Query("MATCH (person:Person)-[checkedIn:CHECKED_IN_AT]->(meeting:Meeting) " +
         "WHERE id(person)={0} " +
         "RETURN person, checkedIn, meeting"
  )
  List<CheckedInAt> findAllPersonCheckedIn(Long personId);
}
