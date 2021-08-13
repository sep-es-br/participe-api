package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.LocalityTypeDto;
import br.gov.es.participe.controller.dto.controlPanel.MicroregionChartQueryDto;
import br.gov.es.participe.model.Conference;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface ControlPanelRepository extends Neo4jRepository<Conference, Long> {
       @Query("MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), "
                     + "(lt:LocalityType)<-[ot:OF_TYPE]-(nf:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p) "
                     + " WHERE id(lt)= {1} AND id(n)={0} " + "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
                     + "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in {2} OR {2} is null) "
                     + "OPTIONAL MATCH (n)<-[t1:TO]-(self1:SelfDeclaration)<-[ma1:MADE]-(p1:Person), (self1)-[bg1:AS_BEING_FROM]->(nf)  WHERE ((p1)-[:CHECKED_IN_AT]->(me) OR {2} is null)"
                     + "OPTIONAL MATCH (n)<-[t2:TO]-(self2:SelfDeclaration)<-[ma2:MADE]-(p2:Person), (self2)-[bg2:AS_BEING_FROM]->(l)   WHERE ((p2)-[:CHECKED_IN_AT]->(me) OR {2} is null)"
                     + " return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, nf.name as name, "
                     + " (COUNT(distinct self1) + COUNT(distinct self2)) as quantityParticipation")
       List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationAgroup(
                     Long idConference, Long microregionChartAgroup, List<Long> meetings);

       @Query("MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), "
                     + "(nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p) "
                     + "WHERE id(ld)= {1} AND id(n)= {0} " + "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
                     + "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in {2} OR {2} is null) "
                     + "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
                     + "OPTIONAL MATCH (n)<-[t1:TO]-(self1:SelfDeclaration)<-[ma1:MADE]-(p1:Person), (self1)-[bg1:AS_BEING_FROM]->(nf)  WHERE ((p1)-[:CHECKED_IN_AT]->(me) OR {2} is null)"
                     + "OPTIONAL MATCH (n)<-[t2:TO]-(self2:SelfDeclaration)<-[ma2:MADE]-(p2:Person), (self2)-[bg2:AS_BEING_FROM]->(l)   WHERE ((p2)-[:CHECKED_IN_AT]->(me) OR {2} is null)"
                     + "return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, nf.name as name, "
                     + "(COUNT(distinct self1) + COUNT(distinct self2)) as quantityParticipation")
       List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipation(Long idConference,
                     Long microregionLocalitySelected, List<Long> meetings);

       @Query("MATCH (lt:LocalityType)<-[ot:OF_TYPE]-(nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality)-[il:IS_LOCATED_IN]->(d:Domain) "
                     + " WHERE id(lt)= {1} AND id(d)= {0} " + " OPTIONAL MATCH (l:Locality)<-[ins:IS_LOCATED_IN*]-(nf) "
                     + " OPTIONAL MATCH (lt2:LocalityType)<-[ot2:OF_TYPE]-(l) " + "return distinct id(lt2) as id, "
                     + "lt2.name as name")
       List<LocalityTypeDto> findDataTypeLocality(Long idDomain, Long idTypeLocality);

       @Query(" MATCH (n:Conference)-[ta:TARGETS]->(p:Plan)<-[te:COMPOSES]-(pl:PlanItem)"
                     + ", (lt:LocalityType)<-[ot:OF_TYPE]-(nf:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p), "
                     + "(pl)-[ob:OBEYS]->(st:StructureItem) " + "WHERE id(n)={0} AND id(lt)= {1} AND id(st) = {2}"
                     + "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in {4} OR {4} is null) "
                     + "OPTIONAL MATCH (pl)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
                     + "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(nf), (co1)-[:ABOUT]->(pl) where NOT co1.status IN ['rem', 'pen'] AND ((co1.type = {3}) OR ({3} IS NULL)) AND ((co1)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co:Comment)-[:ABOUT]->(l), (co)-[:ABOUT]->(pl) where NOT co.status IN ['rem', 'pen'] AND ((co.type = {3}) OR ({3} IS NULL)) AND ((co)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(nf), (co2)-[:ABOUT]->(plChildren) where NOT co2.status IN ['rem', 'pen'] AND ((co2.type = {3}) OR ({3} IS NULL)) AND ((co2)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co3:Comment)-[:ABOUT]->(l),(co3)-[:ABOUT]->(plChildren) where NOT co3.status IN ['rem', 'pen'] AND ((co3.type = {3}) OR ({3} IS NULL)) AND ((co3)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(nf), (ho1)-[:ABOUT]->(pl) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho:Highlight)-[:ABOUT]->(l),(ho)-[:ABOUT]->(pl) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(nf),(ho2)-[:ABOUT]->(plChildren) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho3:Highlight)-[:ABOUT]->(l),(ho3)-[:ABOUT]->(plChildren) "
                     + "return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, "
                     + "nf.name as name, (count(DISTINCT co) + count(DISTINCT co1) + count(DISTINCT co2) + count(DISTINCT co3)) as quantityComment, "
                     + "(count(DISTINCT ho) + count(DISTINCT ho1) + count(DISTINCT ho2) + count(DISTINCT ho3)) as quantityHighlight, "
                     + "id(pl) as idPlanItem, pl.name as planItemName")
       List<MicroregionChartQueryDto> findDataCommentHighlightWitoutFilter(Long idConference,
                     Long microregionChartAgroup, Long structureItemSelected, String origin, List<Long> meetings);

       @Query(" MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), (pl:PlanItem)<-[:COMPOSES]-(plParent:PlanItem)"
                     + ", (lt:LocalityType)<-[ot:OF_TYPE]-(nf:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p) "
                     + "WHERE id(n)={0} AND id(lt)= {1} AND id(pl)= {2}"
                     + "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in {4} OR {4} is null) "
                     + "OPTIONAL MATCH (plParent)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
                     + "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(nf), (co1)-[:ABOUT]->(plParent) where NOT co1.status IN ['rem', 'pen'] AND ((co1.type = {3}) OR ({3} IS NULL)) AND ((co1)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co:Comment)-[:ABOUT]->(l), (co)-[:ABOUT]->(plParent) where NOT co.status IN ['rem', 'pen'] AND ((co.type = {3}) OR ({3} IS NULL)) AND ((co)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(nf), (co2)-[:ABOUT]->(plChildren) where NOT co2.status IN ['rem', 'pen'] AND ((co2.type = {3}) OR ({3} IS NULL)) AND ((co2)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co3:Comment)-[:ABOUT]->(l),(co3)-[:ABOUT]->(plChildren) where NOT co3.status IN ['rem', 'pen'] AND ((co3.type = {3}) OR ({3} IS NULL)) AND ((co3)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(nf), (ho1)-[:ABOUT]->(plParent) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho:Highlight)-[:ABOUT]->(l),(ho)-[:ABOUT]->(plParent) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(nf),(ho2)-[:ABOUT]->(plChildren) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho3:Highlight)-[:ABOUT]->(l),(ho3)-[:ABOUT]->(plChildren) "
                     + "return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, "
                     + "nf.name as name, (count(DISTINCT co) + count(DISTINCT co1) + count(DISTINCT co2) + count(DISTINCT co3)) as quantityComment, "
                     + "(count(DISTINCT ho) + count(DISTINCT ho1) + count(DISTINCT ho2) + count(DISTINCT ho3)) as quantityHighlight, "
                     + "id(plParent) as idPlanItem, plParent.name as planItemName")
       List<MicroregionChartQueryDto> findDataCommentHighlightStructureItemPlanSelected(Long idConference,
                     Long microregionChartAgroup, Long structureItemPlanSelected, String origin, List<Long> meetings);

       @Query(" MATCH (n:Conference)-[ta:TARGETS]->(p:Plan)<-[te:COMPOSES]-(pl:PlanItem), "
                     + " (nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p), (pl)-[ob:OBEYS]->(st:StructureItem) "
                     + "WHERE id(n)={0} AND id(ld)= {1} AND id(st) = {2}"
                     + "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in {4} OR {4} is null) "
                     + "OPTIONAL MATCH (pl)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
                     + "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(nf), (co1)-[:ABOUT]->(pl) where NOT co1.status IN ['rem', 'pen'] AND ((co1.type = {3}) OR ({3} IS NULL)) AND ((co1)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co:Comment)-[:ABOUT]->(l), (co)-[:ABOUT]->(pl) where NOT co.status IN ['rem', 'pen'] AND ((co.type = {3}) OR ({3} IS NULL)) AND ((co)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(nf), (co2)-[:ABOUT]->(plChildren) where NOT co2.status IN ['rem', 'pen'] AND ((co2.type = {3}) OR ({3} IS NULL)) AND ((co2)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co3:Comment)-[:ABOUT]->(l),(co3)-[:ABOUT]->(plChildren)  where NOT co3.status IN ['rem', 'pen'] AND ((co3.type = {3}) OR ({3} IS NULL)) AND ((co3)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(nf), (ho1)-[:ABOUT]->(pl) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho:Highlight)-[:ABOUT]->(l),(ho)-[:ABOUT]->(pl) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(nf),(ho2)-[:ABOUT]->(plChildren) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho3:Highlight)-[:ABOUT]->(l),(ho3)-[:ABOUT]->(plChildren) "
                     + "return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, "
                     + "nf.name as name, (count(DISTINCT co) + count(DISTINCT co1) + count(DISTINCT co2) + count(DISTINCT co3)) as quantityComment, "
                     + "(count(DISTINCT ho) + count(DISTINCT ho1) + count(DISTINCT ho2) + count(DISTINCT ho3)) as quantityHighlight, "
                     + "id(pl) as idPlanItem, pl.name as planItemName")
       List<MicroregionChartQueryDto> findDataCommentHighlightLocalitySelected(Long idConference,
                     Long microregionLocalitySelected, Long structureItemSelected, String origin, List<Long> meetings);

       @Query(" MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), (pl:PlanItem)<-[:COMPOSES]-(plParent:PlanItem), "
                     + " (nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p) "
                     + "WHERE id(n)={0} AND id(ld)= {1} AND id(pl) = {2}"
                     + "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in {4} OR {4} is null) "
                     + "OPTIONAL MATCH (plParent)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
                     + "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(nf), (co1)-[:ABOUT]->(plParent) where NOT co1.status IN ['rem', 'pen'] AND ((co1.type = {3}) OR ({3} IS NULL)) AND ((co1)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co:Comment)-[:ABOUT]->(l), (co)-[:ABOUT]->(plParent) where NOT co.status IN ['rem', 'pen'] AND ((co.type = {3}) OR ({3} IS NULL)) AND ((co)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(nf), (co2)-[:ABOUT]->(plChildren) where NOT co2.status IN ['rem', 'pen'] AND ((co2.type = {3}) OR ({3} IS NULL)) AND ((co2)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co3:Comment)-[:ABOUT]->(l),(co3)-[:ABOUT]->(plChildren)  where NOT co3.status IN ['rem', 'pen'] AND ((co3.type = {3}) OR ({3} IS NULL)) AND ((co3)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(nf), (ho1)-[:ABOUT]->(plParent) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho:Highlight)-[:ABOUT]->(l),(ho)-[:ABOUT]->(plParent) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(nf),(ho2)-[:ABOUT]->(plChildren) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho3:Highlight)-[:ABOUT]->(l),(ho3)-[:ABOUT]->(plChildren) "
                     + "return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, "
                     + "nf.name as name, (count(DISTINCT co) + count(DISTINCT co1) + count(DISTINCT co2) + count(DISTINCT co3)) as quantityComment, "
                     + "(count(DISTINCT ho) + count(DISTINCT ho1) + count(DISTINCT ho2) + count(DISTINCT ho3)) as quantityHighlight, "
                     + "id(plParent) as idPlanItem, plParent.name as planItemName")
       List<MicroregionChartQueryDto> findDataCommentHighlightAllFilter(Long idConference,
                     Long microregionLocalitySelected, Long structureItemPlanSelected, String origin,
                     List<Long> meetings);

       @Query("MATCH (n:Conference)-[ta:TARGETS]->(p:Plan)<-[te:COMPOSES]-(pl:PlanItem), "
                     + " (nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality), (d:Domain)<-[at:APPLIES_TO]-(p), (pl)-[ob:OBEYS]->(st:StructureItem)"
                     + " WHERE id(n)={0} AND id(ld)= {1} AND id(st)= {2} "
                     + "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in {4} OR {4} is null) "
                     + "OPTIONAL MATCH (pl)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(ld), (co1)-[:ABOUT]->(pl) where NOT co1.status IN ['rem', 'pen'] AND ((co1.type = {3}) OR ({3} IS NULL)) AND ((co1)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(ld),(co2)-[:ABOUT]->(plChildren)  where NOT co2.status IN ['rem', 'pen'] AND ((co2.type = {3}) OR ({3} IS NULL)) AND ((co2)-[:DURING]->(me) OR {4} is null)  "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(ld),(ho1)-[:ABOUT]->(pl) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(ld),(ho2)-[:ABOUT]->(plChildren) "
                     + "return id(ld) as id, ld.latitudeLongitude as latitudeLongitude, ld.name as name, "
                     + "(count(DISTINCT co1) + count(DISTINCT co2)) as quantityComment, (count(DISTINCT ho1) + count(DISTINCT ho2)) as quantityHighlight, "
                     + " id(pl) as idPlanItem, pl.name as planItemName")
       List<MicroregionChartQueryDto> findDataCommentHighlightLocalitySelectedLastLevel(Long idConference,
                     Long microregionLocalitySelected, Long structureItemPlanSelected, String origin,
                     List<Long> meetings);

       @Query(" MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), (pl:PlanItem)<-[:COMPOSES]-(plParent:PlanItem), "
                     + " (nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p) "
                     + "WHERE id(n)={0} AND id(ld)= {1} AND id(pl) = {2}"
                     + "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in {4} OR {4} is null) "
                     + "OPTIONAL MATCH (plParent)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(ld), (co1)-[:ABOUT]->(plParent) where NOT co1.status IN ['rem', 'pen'] AND ((co1.type = {3}) OR ({3} IS NULL)) AND ((co1)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(ld),(co2)-[:ABOUT]->(plChildren) where NOT co2.status IN ['rem', 'pen'] AND ((co2.type = {3}) OR ({3} IS NULL)) AND ((co2)-[:DURING]->(me) OR {4} is null) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(ld),(ho1)-[:ABOUT]->(plParent) "
                     + "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(ld),(ho2)-[:ABOUT]->(plChildren) "
                     + "return id(ld) as id, ld.latitudeLongitude as latitudeLongitude, "
                     + "ld.name as name, (count(DISTINCT co1) + count(DISTINCT co2)) as quantityComment, "
                     + "(count(DISTINCT ho1) + count(DISTINCT ho2)) as quantityHighlight, "
                     + "id(plParent) as idPlanItem, plParent.name as planItemName")
       List<MicroregionChartQueryDto> findDataCommentHighlightAllFilterLastLevel(Long idConference,
                     Long microregionLocalitySelected, Long structureItemPlanSelected, String origin,
                     List<Long> meetings);

       @Query("MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), (pl:PlanItem), (lt:LocalityType)<-[ot:OF_TYPE]-(nf:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p)"
                     + " WHERE id(n)={0} AND id(lt)= {1} AND id(pl) = {2}"
                     + " OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
                     + " OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in {4} OR {4} is null) "
                     + " OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(nf), (co1)-[:ABOUT]->(pl) where NOT co1.status IN ['rem', 'pen'] AND ((co1.type = 'pre') OR ('pre' IS NULL)) AND ((co1)-[:DURING]->(me) OR {4} is null) "
                     + " OPTIONAL MATCH (n)<-[:ABOUT]-(co:Comment)-[:ABOUT]->(l), (co)-[:ABOUT]->(pl) where NOT co.status IN ['rem', 'pen'] AND ((co.type = 'pre') OR ('pre' IS NULL)) AND ((co)-[:DURING]->(me) OR {4} is null) "
                     + " OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(nf), (ho1)-[:ABOUT]->(pl) "
                     + " OPTIONAL MATCH (n)<-[:ABOUT]-(ho:Highlight)-[:ABOUT]->(l),(ho)-[:ABOUT]->(pl) "
                     + " return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, nf.name as name, (count(DISTINCT co) + count(DISTINCT co1)) as quantityComment, (count(DISTINCT ho) + count(DISTINCT ho1)) as quantityHighlight, id(pl) as idPlanItem, pl.name as planItemName")
       List<MicroregionChartQueryDto> findDataCommentHighlightStructureItemPlanSelectedLastLevel(Long idConference,
                     Long microregionChartAgroup, Long structureItemPlanSelected, String origin, List<Long> meetings);

}
