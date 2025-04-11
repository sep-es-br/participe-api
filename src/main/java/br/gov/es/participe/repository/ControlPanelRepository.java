package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.LocalityTypeDto;
import br.gov.es.participe.controller.dto.controlPanel.MicroregionChartQueryDto;
import br.gov.es.participe.model.Conference;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ControlPanelRepository extends Neo4jRepository<Conference, Long> {

	@Query(

	" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionChartAgroup AS LocalityTypeGrouping_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +

			" MATCH" +
			" (p:Person)-[:MADE]->(sd:SelfDeclaration)-[:TO]->(conf:Conference)" +
			" ,(sd)-[:AS_BEING_FROM]->(cloc:Locality) " +
			" ,(p)-[:MADE|:CHECKED_IN_AT]->(n)-[:TO|:OCCURS_IN]->(conf)" +
			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND NOT n:SelfDeclaration" +
			" AND NOT id(sd)= 26903 "+

			" MATCH" +
			" (cloc)-[:IS_LOCATED_IN *0..]->(loc:Locality)-[:OF_TYPE]->(plt:LocalityType)" +
			" ,(cloc)-[:OF_TYPE]->(lt:LocalityType)" +

			" WHERE" +
			" id(plt) = LocalityTypeGrouping_Id OR id(lt) = LocalityTypeGrouping_Id" +
			" AND" +
			" (SelectedLocality_Id IS NULL" +
			" OR id(loc) = SelectedLocality_Id" +
			" OR id(cloc) = SelectedLocality_Id" +
			" )" +

			" OPTIONAL MATCH" +
			" (conf)<-[:ABOUT]-(a:Attend)-[:MADE_BY|LIKED_BY]->(pp:Person)-[:MADE]->(sd)" +
			" , 	(a)-[:ABOUT]->(planItem:PlanItem)-[:COMPOSES *0..]->(parentPlanItem:PlanItem)" +

			" WHERE" +
			" SelectedPlanItem_Id is null OR" +
			" (id(planItem) = SelectedPlanItem_Id)" +
			" OR" +
			" (id(parentPlanItem) = SelectedPlanItem_Id)" +

			" RETURN" +
			" CASE LocalityTypeGrouping_Id WHEN id(lt)" +
			" THEN id(cloc)" +
			" ELSE id(loc)" +
			" END as id," +

			" CASE LocalityTypeGrouping_Id WHEN id(lt)" +
			" THEN cloc.latitudeLongitude" +
			" ELSE loc.latitudeLongitude" +
			" END as latitudeLongitude," +

			" CASE LocalityTypeGrouping_Id WHEN id(lt)" +
			" THEN cloc.name" +
			" ELSE loc.name" +
			" END as name," +

			" CASE SelectedPlanItem_Id WHEN NULL" +
			" THEN count(distinct p)" +
			" ELSE count(distinct pp)" +
			" END as quantityParticipation")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationAllAgroup(
		@Param("idConference") Long idConference, 
		@Param("microregionChartAgroup") Long microregionChartAgroup,
		@Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected);

	@Query(" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +
			" MATCH" +
			" (p:Person)<-[:MADE_BY|:LIKED_BY]-(a:Attend)-[:ABOUT]->(cPI:PlanItem)," +
			" (cPI)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(conf:Conference)," +
			" (p)-[:MADE]->(sd:SelfDeclaration)-[:AS_BEING_FROM]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality) "
			+
			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND" +
			" (" +
			" SelectedPlanItem_Id IS NULL" +
			" OR" +
			" (id(planItem) = SelectedPlanItem_Id and id(cPI) <> SelectedPlanItem_Id)" +
			" )" +
			" AND" +
			" (id(parentLoc) = SelectedLocality_Id OR id(loc) = SelectedLocality_Id OR SelectedLocality_Id IS NULL)" +
			" RETURN" +
			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN id(planItem)" +
			" ELSE id(cPI)" +
			" END as idPlanItem," +
			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN planItem.name" +
			" ELSE cPI.name" +
			" END as planItemName," +
			" count(distinct p) as quantityParticipation")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationAllPlanItemAgroup(
		@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected);

	@Query(" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionChartAgroup AS LocalityTypeGrouping_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +

			" MATCH (conf:Conference) WHERE id(conf) = Conference_Id" +
			" OPTIONAL MATCH" +
			" (me:Meeting)-[:OCCURS_IN]->(conf)" +

			" with me, conf, LocalityTypeGrouping_Id, SelectedLocality_Id, SelectedPlanItem_Id" +

			" MATCH" +
			" (p:Person)-[:MADE]->(sd:SelfDeclaration)-[:TO]->(conf)" +
			" ,(sd)-[:AS_BEING_FROM]->(cloc:Locality)" +
			" ,(p)-[:MADE]->(ln:Login)-[:TO]->(conf)" +

			" WHERE" +
			" (NOT (p)-[:CHECKED_IN_AT]->(me))" +
			" OR" +
			" ln.time <= me.beginDate" +
			" OR" +
			" ln.time >= me.endDate" +

			" with p, conf, LocalityTypeGrouping_Id, SelectedLocality_Id, SelectedPlanItem_Id, sd, cloc" +

			" MATCH" +
			" (cloc)-[:IS_LOCATED_IN *0..]->(loc:Locality)-[:OF_TYPE]->(plt:LocalityType)" +
			" ,(cloc)-[:OF_TYPE]->(lt:LocalityType)" +
			" WHERE" +
			" id(plt) = LocalityTypeGrouping_Id OR id(lt) = LocalityTypeGrouping_Id" +
			" AND" +
			" (SelectedLocality_Id IS NULL" +
			" OR id(loc) = SelectedLocality_Id" +
			" OR id(cloc) = SelectedLocality_Id" +
			" )" +

			" OPTIONAL MATCH" +
			" (conf)<-[:ABOUT]-(a:Attend)-[:MADE_BY|LIKED_BY]->(pp:Person)-[:MADE]->(sd)" +
			" , 	(a)-[:ABOUT]->(planItem:PlanItem)-[:COMPOSES *0..]->(parentPlanItem:PlanItem)" +

			" WHERE" +
			" SelectedPlanItem_Id is null OR" +
			" (id(planItem) = SelectedPlanItem_Id)" +
			" OR" +
			" (id(parentPlanItem) = SelectedPlanItem_Id)" +

			" RETURN" +
			" CASE LocalityTypeGrouping_Id WHEN id(lt)" +
			" THEN id(cloc)" +
			" ELSE id(loc)" +
			" END as id," +

			" CASE LocalityTypeGrouping_Id WHEN id(lt)" +
			" THEN cloc.latitudeLongitude" +
			" ELSE loc.latitudeLongitude" +
			" END as latitudeLongitude," +

			" CASE LocalityTypeGrouping_Id WHEN id(lt)" +
			" THEN cloc.name" +
			" ELSE loc.name" +
			" END as name," +

			" CASE SelectedPlanItem_Id WHEN NULL" +
			" THEN count(distinct p)" +
			" ELSE count(distinct pp)" +
			" END as quantityParticipation")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationRemotoAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected);

	@Query(" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +

			" MATCH" +
			" (p:Person)<-[:MADE_BY|:LIKED_BY]-(a:Attend)-[:ABOUT]->(cPI:PlanItem)," +
			" (cPI)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(conf:Conference)," +
			" (p)-[:MADE]->(sd:SelfDeclaration)-[:AS_BEING_FROM]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)"
			+

			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND a.from = 'rem'" +
			" AND" +
			" (" +
			" SelectedPlanItem_Id IS NULL" +
			" OR" +
			" (id(planItem) = SelectedPlanItem_Id and id(cPI) <> SelectedPlanItem_Id)" +
			" )" +
			" AND" +
			" (id(parentLoc) = SelectedLocality_Id OR id(loc) = SelectedLocality_Id OR SelectedLocality_Id IS NULL)" +

			" RETURN" +

			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN id(planItem)" +
			" ELSE id(cPI)" +
			" END as idPlanItem," +

			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN planItem.name" +
			" ELSE cPI.name" +
			" END as planItemName," +

			" count(distinct p) as quantityParticipation")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationRemotoPlanItemAgroup(
		@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected);
/* 
	@Query(" WITH $idConference AS Conference_Id,$microregionChartAgroup AS LocalityTypeGrouping_Id,$microregionLocalitySelected AS SelectedLocality_Id,$structureItemPlanSelected AS SelectedPlanItem_Id,$meetings AS Meeting_List "
			+
			" MATCH(p:Person)-[:CHECKED_IN_AT]->(me:Meeting)-[:OCCURS_IN]->(conf:Conference),(p)-[:MADE]->(sd:SelfDeclaration)-[:TO]->(conf) "
			+
			" ,(sd)-[:AS_BEING_FROM]->(cLoc:Locality) " +
			" WHERE id(conf) = Conference_Id AND (Meeting_List IS NULL OR id(me) IN Meeting_List) " +
			" MATCH(plt:LocalityType)<-[:OF_TYPE]-(loc:Locality)<-[:IS_LOCATED_IN *0..]-(cLoc),(loc)-[:IS_LOCATED_IN]->(pLoc) "
			+
			" where 	id(plt) = LocalityTypeGrouping_Id AND (SelectedLocality_Id IS NULL OR id(pLoc) = SelectedLocality_Id) "
			+
			" OPTIONAL MATCH planned = (p)<-[:MADE_BY|LIKED_BY]-(a:Attend)-[:ABOUT]->(planItem:PlanItem)-[:COMPOSES *0..]->(parentPlanItem:PlanItem), "
			+
			" (a)-[:WHILE_IN]->(me) " +
			" WHERE id(parentPlanItem) = SelectedPlanItem_Id " +
			" OPTIONAL MATCH (pp:Person) " +
			" WHERE pp in nodes(planned) " +
			" WITH pp,p,loc,SelectedPlanItem_Id " +
			" RETURN id(loc) as id,loc.latitudeLongitude as latitudeLongitude,loc.name as name, " +
			" case SelectedPlanItem_Id " +
			" WHEN NULL THEN count(distinct p) " +
			" ELSE count(distinct pp) " +
			" END as quantityParticipation ")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationPresenteAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);
*/

@Query( 
		" MATCH (p:Person)-[:MADE]->(sd:SelfDeclaration)-[:TO]->(co:Conference),\r\n" + //
		"      (sd)-[:AS_BEING_FROM]->(cLoc:Locality)\r\n" + //
		"WHERE id(co) = $idConference\r\n" + //
		"\r\n" + //
		"// Presença: Manual via Login OU Automática via Check-in\r\n" + //
		"AND (\r\n" + //
		"  EXISTS {\r\n" + //
		"    MATCH (p)-[:MADE]->(l:Login)-[:TO]->(co)<-[:OCCURS_IN]-(m:Meeting)\r\n" + //
		"    WHERE m.attendanceListMode = 'MANUAL'\r\n" + //
		"      AND l.time >= m.beginDate AND l.time <= m.endDate\r\n" + //
		"      AND ($meetings IS NULL OR id(m) IN $meetings)\r\n" + //
		"  }\r\n" + //
		"  OR EXISTS {\r\n" + //
		"    MATCH (co)<-[:OCCURS_IN]-(m:Meeting)<-[:CHECKED_IN_AT]-(p)\r\n" + //
		"    WHERE m.attendanceListMode = 'AUTO'\r\n" + //
		"      AND ($meetings IS NULL OR id(m) IN $meetings)\r\n" + //
		"  }\r\n" + //
		")\r\n" + //
		"\r\n" + //
		"// Localidade e Microregião\r\n" + //
		"OPTIONAL MATCH (plt:LocalityType)<-[:OF_TYPE]-(loc:Locality)<-[:IS_LOCATED_IN *0..]-(cLoc),\r\n" + //
		"               (loc)-[:IS_LOCATED_IN]->(pLoc)\r\n" + //
		"WHERE id(plt) = $microregionChartAgroup\r\n" + //
		"  AND ($microregionLocalitySelected IS NULL OR id(pLoc) = $microregionLocalitySelected)\r\n" + //
		"\r\n" + //
		"// Planejamento\r\n" + //
		"OPTIONAL MATCH planned = (p)<-[:MADE_BY|LIKED_BY]-(a:Attend)-[:ABOUT]->(planItem:PlanItem)-[:COMPOSES *0..]->(parentPlanItem:PlanItem),\r\n" + //
		"                       (a)-[:WHILE_IN]->(me)\r\n" + //
		"WHERE id(parentPlanItem) = $structureItemPlanSelected\r\n" + //
		"\r\n" + //
		"OPTIONAL MATCH (pp:Person)\r\n" + //
		"WHERE pp IN nodes(planned)\r\n" + //
		"\r\n" + //
		"WITH pp, p, loc, $structureItemPlanSelected AS SelectedPlanItem_Id\r\n" + //
		"RETURN \r\n" + //
		"  id(loc) AS id,\r\n" + //
		"  loc.latitudeLongitude AS latitudeLongitude,\r\n" + //
		"  loc.name AS name,\r\n" + //
		"  CASE WHEN SelectedPlanItem_Id IS NULL \r\n" + //
		"       THEN count(DISTINCT p)\r\n" + //
		"       ELSE count(DISTINCT pp)\r\n" + //
		"  END AS quantityParticipation") 
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationPresenteAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);

@Query(	" match (p:Person)-[:MADE]->(l:Login)-[:TO]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting) ,  " +
		" (p)-[:MADE]->(sd:SelfDeclaration)-[:TO]->(co),(sd)-[:AS_BEING_FROM]->(cLoc:Locality) " +
		" where id(co) = $idConference " +
		" AND m.attendanceListMode = 'MANUAL' " +
		" AND l.time >= m.beginDate and l.time <= m.endDate   " +
		" AND (($meetings IS NULL) OR (id(m) IN $meetings))   " +
		" WITH collect(p) as plogged  " +
		" match (co:Conference)<-[:OCCURS_IN]-(m:Meeting)<-[:CHECKED_IN_AT]-(p:Person), " +
		" (p)-[:MADE]->(sd:SelfDeclaration)-[:TO]->(co),(sd)-[:AS_BEING_FROM]->(cLoc:Locality)  " +
		" where id(co) = $idConference  and m.attendanceListMode = 'AUTO' " +
		" AND (($meetings IS NULL) OR (id(m) IN $meetings))   " +
		" WITH plogged + collect(p) as allp  " +
		" MATCH (p:Person)<-[:MADE_BY|:LIKED_BY]-(a:Attend)-[:ABOUT]->(cPI:PlanItem), " +
		" (cPI)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(conf:Conference)<-[:OCCURS_IN]-(me:Meeting), " +
		" (p)-[:MADE]->(sd:SelfDeclaration)-[:AS_BEING_FROM]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality) " +
		" WHERE ID(conf) = $idConference AND a.from = 'pres'  " +
		" WITH allp + collect(p) as allp1  " +
		" unwind allp1 as np " +
		" MATCH(np)<-[:MADE_BY|:LIKED_BY]-(a:Attend)-[:ABOUT]->(cPI:PlanItem), " +
		" (cPI)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(co)<-[:OCCURS_IN]-(m), " +
		" (np)-[:MADE]->(sd)-[:AS_BEING_FROM]->(cLoc)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality) " +
		" WHERE ID(co) = $idConference AND a.from = 'pres' AND ( $meetings IS NULL OR id(m) IN $meetings) " +
		" AND ($structureItemPlanSelected  IS NULL OR (id(planItem) = $structureItemPlanSelected  and id(cPI) <> $structureItemPlanSelected ) ) " +
		" AND (id(parentLoc) = $microregionLocalitySelected OR id(cLoc) = $microregionLocalitySelected OR $microregionLocalitySelected IS NULL) " +
		" RETURN " +
		" CASE $structureItemPlanSelected WHEN NULL THEN id(planItem) ELSE id(cPI) END as idPlanItem, " +
		" CASE $structureItemPlanSelected WHEN NULL THEN planItem.name ELSE cPI.name END as planItemName, " +
		" count(distinct np) as quantityParticipation ")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationPresentePlanItemAgroup(
		@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);


		


	@Query(" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionChartAgroup AS LocalityTypeGrouping_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +

			" MATCH" +
			" (conf:Conference)<-[:ABOUT]-(a:Highlight)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem),"
			+
			" (a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)-[:OF_TYPE *0..]->(plt:LocalityType)"
			+

			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND id(plt) = LocalityTypeGrouping_Id" +
			" AND (SelectedLocality_Id IS NULL" +
			" OR id(parentLoc) = SelectedLocality_Id" +
			" OR id(loc) = SelectedLocality_Id)" +
			" AND (SelectedPlanItem_Id IS NULL" +
			" OR id(cPI) = SelectedPlanItem_Id" +
			" OR id(planItem) = SelectedPlanItem_Id)" +

			" RETURN" +
			" id(parentLoc) as id," +
			" parentLoc.latitudeLongitude as latitudeLongitude," +
			" parentLoc.name as name," +
			" count(distinct a) as quantityHighlight")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceHighlightAllAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected);

	@Query(" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionChartAgroup AS LocalityTypeGrouping_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +

			" MATCH" +
			" (conf:Conference)<-[:ABOUT]-(a:Highlight)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem),"
			+
			" (a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)-[:OF_TYPE *0..]->(plt:LocalityType)"
			+

			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND a.from = 'rem'" +
			" AND id(plt) = LocalityTypeGrouping_Id" +
			" AND (SelectedLocality_Id IS NULL" +
			" OR id(parentLoc) = SelectedLocality_Id" +
			" OR id(loc) = SelectedLocality_Id)" +
			" AND (SelectedPlanItem_Id IS NULL" +
			" OR id(cPI) = SelectedPlanItem_Id" +
			" OR id(planItem) = SelectedPlanItem_Id)" +

			" RETURN" +
			" id(parentLoc) as id," +
			" parentLoc.latitudeLongitude as latitudeLongitude," +
			" parentLoc.name as name," +
			" count(distinct a) as quantityHighlight")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceHighlightRemotoAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected);


		/*
	@Query(

	" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionChartAgroup AS LocalityTypeGrouping_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id," +
			" $meetings AS Meeting_List" +

			" MATCH" +
			" (conf:Conference)<-[:ABOUT]-(a:Highlight)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)"
			+
			" ,(a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)-[:OF_TYPE *0..]->(plt:LocalityType)"
			+
			" ,(conf)<-[:OCCURS_IN]-(me:Meeting)<-[:DURING]-(a)" +

			" WHERE" +
			" id(conf) = Conference_Id" +
			" AND a.from = 'pres'" +
			" AND (Meeting_List IS NULL OR id(me) IN Meeting_List)" +
			" AND id(plt) = LocalityTypeGrouping_Id" +
			" AND (SelectedLocality_Id IS NULL" +
			" OR id(parentLoc) = SelectedLocality_Id" +
			" OR id(loc) = SelectedLocality_Id)" +
			" AND (SelectedPlanItem_Id IS NULL" +
			" OR id(cPI) = SelectedPlanItem_Id" +
			" OR id(planItem) = SelectedPlanItem_Id)" +

			" RETURN" +
			" id(parentLoc) as id," +
			" parentLoc.latitudeLongitude as latitudeLongitude," +
			" parentLoc.name as name," +
			" count(distinct a) as quantityHighlight"

	)
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceHighlightPresenteAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);

 */

@Query( " optional match (h:Highlight)-[:ABOUT]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting) " +
		" where id(co) = $idConference AND  " +
		" m.attendanceListMode = 'MANUAL'  AND  " +
		" h.time >= m.beginDate and h.time <= m.endDate  " +
		" AND (($meetings IS NULL) OR (id(m) IN $meetings)) " +
		" with collect(h) as plogged  " +
		" optional match(h:Highlight)-[:ABOUT]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting) " +
		" where id(co) = $idConference AND m.attendanceListMode = 'AUTO' AND  h.from='pres' and (m)<-[:DURING]-(h) " +
		" AND (($meetings IS NULL) OR (id(m) IN $meetings)) " +
		" with plogged + collect(h) as allp  " +
		" unwind allp as np " +
		" MATCH (co:Conference)<-[:ABOUT]-(np)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem) " +
		" ,(np)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)-[:OF_TYPE *0..]->(plt:LocalityType) " +
		" ,(co)<-[:OCCURS_IN]-(m) " +
		" WHERE id(co) = $idConference " +
		" AND case when m.attendanceListMode = 'AUTO' then np.from = 'pres' and (m)<-[:DURING]-(np) else np.from <> 'pres'  end " +
		" AND ($meetings IS NULL OR id(m) IN $meetings) " +
		" AND id(plt) = $microregionChartAgroup " +
		" AND ($microregionLocalitySelected IS NULL " +
		" OR id(parentLoc) = $microregionLocalitySelected " +
		" OR id(loc) = $microregionLocalitySelected) " +
		" AND ($structureItemPlanSelected IS NULL " +
		" OR id(cPI) = $structureItemPlanSelected " +
		" OR id(planItem) = $structureItemPlanSelected) " +
		" RETURN " +
		" id(parentLoc) as id, " +
		" parentLoc.latitudeLongitude as latitudeLongitude, " +
		" parentLoc.name as name, " +
		" count(distinct np) as quantityHighlight")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceHighlightPresenteAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);



	@Query(" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +

			" MATCH" +
			" (a:Highlight)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(conf:Conference),"
			+
			" (a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)" +

			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND" +
			" (" +
			" SelectedPlanItem_Id IS NULL" +
			" OR" +
			" (id(planItem) = SelectedPlanItem_Id and id(cPI) <> SelectedPlanItem_Id)" +
			" )" +

			" AND (" +
			"   id(parentLoc) = SelectedLocality_Id" +
			"   OR" +
			"   id(loc) = SelectedLocality_Id" +
			"   OR" +
			"   SelectedLocality_Id IS NULL" +
			" )" +

			" WITH" +
			" a," +
			" planItem," +
			" cPI," +
			" SelectedPlanItem_Id" +

			" RETURN" +
			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN id(planItem)" +
			" ELSE id(cPI)" +
			" END as idPlanItem," +

			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN planItem.name" +
			" ELSE cPI.name" +
			" END as planItemName," +

			" count(distinct a) as quantityHighlight")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceHighlightAllPlanItemAgroup(
		@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected);

	@Query(" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +

			" MATCH" +
			" (a:Highlight)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(conf:Conference),"
			+
			" (a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)" +
			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND a.from = 'rem' " +
			" AND (" +
			" SelectedPlanItem_Id IS NULL" +
			" OR" +
			" (id(planItem) = SelectedPlanItem_Id and id(cPI) <> SelectedPlanItem_Id)" +
			" )" +
			" AND (" +
			"   id(parentLoc) = SelectedLocality_Id OR id(loc) = SelectedLocality_Id OR SelectedLocality_Id IS NULL" +
			" )" +

			" WITH" +
			" a," +
			" planItem," +
			" cPI," +
			" SelectedPlanItem_Id" +

			" RETURN" +
			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN id(planItem)" +
			" ELSE id(cPI)" +
			" END as idPlanItem," +

			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN planItem.name" +
			" ELSE cPI.name" +
			" END as planItemName," +

			" count(distinct a) as quantityHighlight"

	)
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceHighlightRemotoPlanItemAgroup(
		@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected);


		/* 
	@Query(" WITH" +
			" $idConference as Conference_Id," +
			" $microregionLocalitySelected as SelectedLocality_Id," +
			" $structureItemPlanSelected as SelectedPlanItem_Id," +
			" $meetings as Meeting_List" +

			" MATCH" +
			" (a:Highlight)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(conf:Conference)<-[:OCCURS_IN]-(me:Meeting),"
			+
			" (me)<-[:DURING]-(a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)" +
	

			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND (" +
			" SelectedPlanItem_Id IS NULL" +
			" OR" +
			" (id(planItem) = SelectedPlanItem_Id and id(cPI) <> SelectedPlanItem_Id)" +
			" )" +
			" AND a.from = 'pres' " +
			" AND (" +
			" Meeting_List IS NULL" +
			" OR" +
			" id(me) IN Meeting_List" +
			" )" +
			" AND (" +
			"   id(parentLoc) = SelectedLocality_Id" +
			"   OR" +
			"   id(loc) = SelectedLocality_Id" +
			"   OR" +
			"   SelectedLocality_Id IS NULL" +
			" )" +

			" WITH" +
			" a," +
			" planItem," +
			" cPI," +
			" SelectedPlanItem_Id" +

			" RETURN" +
			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN id(planItem)" +
			" ELSE id(cPI)" +
			" END as idPlanItem," +

			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN planItem.name" +
			" ELSE cPI.name" +
			" END as planItemName," +

			" count(distinct a) as quantityHighlight")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceHighlightPresentePlanItemAgroup(
		@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);

	*/
		@Query( " optional match (h:Highlight)-[:ABOUT]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting)  " +
				" where id(co) = $idConference AND " +
				" m.attendanceListMode = 'MANUAL'  AND  " +
				" h.time >= m.beginDate and h.time <= m.endDate  " +
				" AND (($meetings IS NULL) OR (id(m) IN $meetings)) " +
				" with collect(h) as plogged  " +
				" optional match(h:Highlight)-[:ABOUT]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting) " +
				" where id(co) = $idConference AND m.attendanceListMode = 'AUTO' AND  h.from='pres' and (m)<-[:DURING]-(h)  " +
				" AND (($meetings IS NULL) OR (id(m) IN $meetings)) " +
				" with plogged + collect(h) as allp  " +
				" unwind allp as np " +
				" MATCH(np)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(co)<-[:OCCURS_IN]-(m), " +
				" (np)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality) " +
				" WHERE ID(co) = $idConference " +
				" AND case when m.attendanceListMode = 'AUTO' then np.from = 'pres' and (m)<-[:DURING]-(np) else np.from <> 'pres'  end " +
				" AND ($structureItemPlanSelected IS NULL " +
				" OR (id(planItem) = $structureItemPlanSelected and id(cPI) <> $structureItemPlanSelected)) " +
				" AND ($meetings IS NULL OR id(m) IN $meetings) " +
				" AND (id(parentLoc) = $microregionLocalitySelected OR id(loc) = $microregionLocalitySelected " +
				" OR $microregionLocalitySelected IS NULL) " +
				" WITH np,planItem,cPI,$structureItemPlanSelected as SelectedPlanItem_Id " +
				" RETURN " +
				" CASE SelectedPlanItem_Id " +
				" WHEN NULL THEN id(planItem) " +
				" ELSE id(cPI) " +
				" END as idPlanItem, " +
				" CASE SelectedPlanItem_Id " +
				" WHEN NULL THEN planItem.name " +
				" ELSE cPI.name " +
				" END as planItemName, " +
				" count(distinct np) as quantityHighlight")
List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceHighlightPresentePlanItemAgroup(
	@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);

	
	
	
	
	
	
	
		@Query(

	" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionChartAgroup AS LocalityTypeGrouping_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +

			" MATCH" +
			" (conf:Conference)<-[:ABOUT]-(a:Comment)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)," +
			" (a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)-[:OF_TYPE *0..]->(plt:LocalityType)"
			+

			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND (NOT a.status IN ['rem' , 'pen' ])" +
			" AND id(plt) = LocalityTypeGrouping_Id" +
			" AND (SelectedLocality_Id IS NULL" +
			" OR id(parentLoc) = SelectedLocality_Id" +
			" OR id(loc) = SelectedLocality_Id)" +
			" AND (SelectedPlanItem_Id IS NULL" +
			" OR id(cPI) = SelectedPlanItem_Id" +
			" OR id(planItem) = SelectedPlanItem_Id)" +

			" RETURN" +
			" id(parentLoc) as id," +
			" parentLoc.latitudeLongitude as latitudeLongitude," +
			" parentLoc.name as name," +
			" count(distinct a) as quantityComment"

	)
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceProposalsAllAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected);

	@Query(

	" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +

			" MATCH" +
			" (a:Comment)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(conf:Conference),"
			+
			" (a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)" +
			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND (NOT a.status IN ['rem', 'pen'])" +
			" AND (" +
			" SelectedPlanItem_Id IS NULL " +
			" OR" +
			" (id(planItem) = SelectedPlanItem_Id and id(cPI) <> SelectedPlanItem_Id)" +
			" )" +
			" AND (" +
			"   id(parentLoc) = SelectedLocality_Id" +
			"   OR" +
			"   id(loc) = SelectedLocality_Id" +
			"   OR" +
			"   SelectedLocality_Id IS NULL" +
			")" +

			" WITH" +
			" a," +
			" planItem," +
			" cPI," +
			" SelectedPlanItem_Id" +

			" RETURN" +
			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN id(planItem)" +
			" ELSE id(cPI)" +
			" END as idPlanItem," +

			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN planItem.name" +
			" ELSE cPI.name" +
			" END as planItemName," +

			" count(distinct a) as quantityComment"

	)
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceProposalsAllPlanItemAgroup(
		@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected);

	@Query(" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionChartAgroup AS LocalityTypeGrouping_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id" +

			" MATCH" +
			" (conf:Conference)<-[:ABOUT]-(a:Comment)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)," +
			" (a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)-[:OF_TYPE *0..]->(plt:LocalityType)"
			+

			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND (NOT a.status IN ['rem' , 'pen' ])" +
			" AND a.from = 'rem'" +
			" AND id(plt) = LocalityTypeGrouping_Id" +
			" AND (SelectedLocality_Id IS NULL" +
			" OR id(parentLoc) = SelectedLocality_Id" +
			" OR id(loc) = SelectedLocality_Id)" +
			" AND (SelectedPlanItem_Id IS NULL" +
			" OR id(cPI) = SelectedPlanItem_Id" +
			" OR id(planItem) = SelectedPlanItem_Id)" +

			" RETURN" +
			" id(parentLoc) as id," +
			" parentLoc.latitudeLongitude as latitudeLongitude," +
			" parentLoc.name as name," +
			" count(distinct a) as quantityComment")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceProposalsRemotoAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected);

	@Query(

	"WITH " +
			"$idConference AS Conference_Id, " +
			"$microregionLocalitySelected AS SelectedLocality_Id, " +
			"$structureItemPlanSelected AS SelectedPlanItem_Id " +

			" MATCH" +
			" (a:Comment)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(conf:Conference),"
			+
			" (a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)" +

			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND (NOT a.status IN ['rem', 'pen'])" +
			" AND	a.from = 'rem' " +
			" AND (" +
			" SelectedPlanItem_Id IS NULL" +
			" OR" +
			" (id(planItem) = SelectedPlanItem_Id and id(cPI) <> SelectedPlanItem_Id)" +
			" )" +
			" AND (" +
			"   id(parentLoc) = SelectedLocality_Id" +
			"   OR id(loc) = SelectedLocality_Id" +
			"   OR SelectedLocality_Id IS NULL " +
			" )" +

			"WITH " +
			"a, " +
			"planItem, " +
			"cPI, " +
			"SelectedPlanItem_Id " +

			"RETURN " +
			"CASE SelectedPlanItem_Id " +
			"WHEN NULL THEN id(planItem) " +
			"ELSE id(cPI) " +
			"END as idPlanItem, " +

			"CASE SelectedPlanItem_Id " +
			"WHEN NULL THEN planItem.name " +
			"ELSE cPI.name " +
			"END as planItemName, " +

			"count(distinct a) as quantityComment ")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceProposalsRemotoPlanItemAgroup(
		@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected);


		/* 
	@Query(" WITH" +
			" $idConference AS Conference_Id," +
			" $microregionChartAgroup AS LocalityTypeGrouping_Id," +
			" $microregionLocalitySelected AS SelectedLocality_Id," +
			" $structureItemPlanSelected AS SelectedPlanItem_Id," +
			" $meetings AS Meeting_List" +

			" MATCH" +
			" (conf:Conference)<-[:ABOUT]-(a:Comment)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)" +
			" ,(a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)-[:OF_TYPE *0..]->(plt:LocalityType)"
			+
			" ,(conf)<-[:OCCURS_IN]-(me:Meeting)<-[:DURING]-(a)" +

			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND a.from = 'pres'" +
			" AND (NOT a.status IN ['rem' , 'pen' ])" +
			" AND (Meeting_List IS NULL OR id(me) IN Meeting_List)" +
			" AND id(plt) = LocalityTypeGrouping_Id" +
			" AND (SelectedLocality_Id IS NULL" +
			" OR id(parentLoc) = SelectedLocality_Id" +
			" OR id(loc) = SelectedLocality_Id)" +
			" AND (SelectedPlanItem_Id IS NULL" +
			" OR id(cPI) = SelectedPlanItem_Id" +
			" OR id(planItem) = SelectedPlanItem_Id)" +

			" RETURN" +
			" id(parentLoc) as id," +
			" parentLoc.latitudeLongitude as latitudeLongitude," +
			" parentLoc.name as name," +
			" count(distinct a) as quantityComment")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceProposalsPresenteAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
		@Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);

		*/

		@Query( " optional match (p:Person)<-[:MADE_BY]-(c:Comment)-[:ABOUT]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting)  " +
				" where id(co) = $idConference AND  " +
				" m.attendanceListMode = 'MANUAL'  AND   " +
				" c.time >= m.beginDate and c.time <= m.endDate AND (($meetings IS NULL) OR (id(m) IN $meetings))  " +
				" AND c.status IN ['pub', 'arq']  " +
				" with collect(c) as plogged " +
				" optional match (p:Person)<-[:MADE_BY]-(c:Comment)-[:ABOUT]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting)  " +
				" where id(co) = $idConference AND m.attendanceListMode = 'AUTO' AND  c.from='pres'  and (m)<-[:DURING]-(c) " +
				" AND c.status IN ['pub', 'arq']   " +
				" AND (($meetings IS NULL) OR (id(m) IN $meetings))  " +
				" with plogged + collect(c) as allp   " +
				" unwind allp as np " +
				" MATCH(co)<-[:ABOUT]-(np)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem) " +
				" ,(np)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)-[:OF_TYPE *0..]->(plt:LocalityType) " +
				" ,(co)<-[:OCCURS_IN]-(m) " +
				" WHERE ID(co) = $idConference " +
				" AND case when m.attendanceListMode = 'AUTO' then np.from = 'pres' and (m)<-[:DURING]-(np) else np.from <> 'pres'  end " +
				" AND (NOT np.status IN ['rem' , 'pen' ]) " +
				" AND ($meetings IS NULL OR id(m) IN $meetings) " +
				" AND id(plt) = $microregionChartAgroup  " +
				" AND ($microregionLocalitySelected IS NULL " +
				" OR id(parentLoc) = $microregionLocalitySelected " +
				" OR id(loc) = $microregionLocalitySelected) " +
				" AND ($structureItemPlanSelected IS NULL " +
				" OR id(cPI) = $structureItemPlanSelected " +
				" OR id(planItem) = $structureItemPlanSelected) " +
				" RETURN " +
				" id(parentLoc) as id, " +
				" parentLoc.latitudeLongitude as latitudeLongitude, " +
				" parentLoc.name as name, " +
				" count(distinct np ) as quantityComment")
List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceProposalsPresenteAgroup(
	@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("microregionLocalitySelected") Long microregionLocalitySelected,
	@Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);




/* 
	@Query(" WITH" +
			" $idConference as Conference_Id," +
			" $microregionLocalitySelected as SelectedLocality_Id," +
			" $structureItemPlanSelected as SelectedPlanItem_Id," +
			" $meetings as Meeting_List" +

			" MATCH" +
			" (a:Comment)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(conf:Conference)<-[:OCCURS_IN]-(me:Meeting),"
			+
			" (a)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)" +

			" WHERE" +
			" ID(conf) = Conference_Id" +
			" AND (NOT a.status IN ['rem', 'pen'])" +
			" AND (" +
			" SelectedPlanItem_Id IS NULL" +
			" OR" +
			" (id(planItem) = SelectedPlanItem_Id and id(cPI) <> SelectedPlanItem_Id)" +
			" )" +
			" AND a.from = 'pres' " +
			" AND (" +
			" Meeting_List IS NULL" +
			" OR" +
			" id(me) IN Meeting_List" +
			" )" +
			" AND (" +
			"   id(parentLoc) = SelectedLocality_Id" +
			"   OR id(loc) = SelectedLocality_Id" +
			"   OR SelectedLocality_Id IS NULL" +
			" )" +

			" WITH" +
			" a," +
			" planItem," +
			" cPI," +
			" SelectedPlanItem_Id" +

			" RETURN" +
			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN id(planItem)" +
			" ELSE id(cPI)" +
			" END as idPlanItem," +

			" CASE SelectedPlanItem_Id" +
			" WHEN NULL THEN planItem.name" +
			" ELSE cPI.name" +
			" END as planItemName," +
			" count(distinct a) as quantityComment")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceProposalsPresentePlanItemAgroup(
		@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);
*/

@Query( " optional match (p:Person)<-[:MADE_BY]-(c:Comment)-[:ABOUT]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting) " +
		" where id(co) = $idConference AND  " +
		" m.attendanceListMode = 'MANUAL'  AND   " +
		" c.time >= m.beginDate and c.time <= m.endDate AND (($meetings IS NULL) OR (id(m) IN $meetings))  " +
		" AND c.status IN ['pub', 'arq']  " +
		" with collect(c) as plogged " +
		" optional match (p:Person)<-[:MADE_BY]-(c:Comment)-[:ABOUT]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting)  " +
		" where id(co) = $idConference AND m.attendanceListMode = 'AUTO' AND  c.from='pres' and (m)<-[:DURING]-(c) " +
		" AND c.status IN ['pub', 'arq']   " +
		" AND (($meetings IS NULL) OR (id(m) IN $meetings))  " +
		" with plogged + collect(c) as allp   " +
		" unwind allp as np " +
		" MATCH(np)-[:ABOUT]->(cPI:PlanItem)-[:COMPOSES *0..]->(planItem:PlanItem)-[:COMPOSES]->(plan:Plan)<-[:TARGETS]-(co)<-[:OCCURS_IN]-(m), " +
		" (np)-[:ABOUT *0..]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality) " +
		" WHERE " +
		" ID(co) = $idConference " +
		" AND case when m.attendanceListMode = 'AUTO' then np.from = 'pres' and (m)<-[:DURING]-(np) else np.from <> 'pres'  end " +
		" AND (NOT np.status IN ['rem', 'pen']) " +
		" AND ($structureItemPlanSelected IS NULL " +
		" OR(id(planItem) = $structureItemPlanSelected " +
		" and id(cPI) <> $structureItemPlanSelected)) " +
		" AND ($meetings IS NULL OR id(m) IN $meetings) " +
		" AND (id(parentLoc) = $microregionLocalitySelected " +
		" OR id(loc) = $microregionLocalitySelected " +
		" OR $microregionLocalitySelected IS NULL) " +
		" WITH np,planItem,cPI,$structureItemPlanSelected as SelectedPlanItem_Id " +
		" RETURN " +
		" CASE SelectedPlanItem_Id " +
		" WHEN NULL THEN id(planItem) " +
		" ELSE id(cPI) " +
		" END as idPlanItem,  " +
		" CASE SelectedPlanItem_Id " +
		" WHEN NULL THEN planItem.name " +
		" ELSE cPI.name " +
		" END as planItemName, " +
		" count(distinct np) as quantityComment")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceProposalsPresentePlanItemAgroup(
		@Param("idConference") Long idConference, @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("meetings") List<Long> meetings);



	@Query(" match (p:Person)-[:MADE]->(lo:Login)-[:TO]->(co:Conference),(p)-[:MADE]->(s:SelfDeclaration)-[:TO]->(co), "
			+
			" (s)-[:AS_BEING_FROM]->()-[:IS_LOCATED_IN *0..]->(l:Locality)-[:OF_TYPE]->(lt:LocalityType) " +
			" WHERE id(co)=$idConference and id(lt)= $microregionChartAgroup " +
			" RETURN id(l) as id, l.latitudeLongitude as latitudeLongitude, l.name as name, " +
			" count (distinct p) as quantityParticipation")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationAllAgroupt(
		@Param("idConference") Long idConference, @Param("idConference") Long microregionChartAgroup);

	@Query("  match (lo:Login)<-[:MADE]-(p)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference) " +
			"  where id(co) = 102 AND lo.time > m.beginDate AND lo.time < m.endDate " +
			"	 with collect(lo) as prLogin " +
			"	 match (p:Person)-[:MADE]->(log:Login)-[:TO]->(co:Conference),(s)-[:AS_BEING_FROM]->()-[:IS_LOCATED_IN *0..]->(l:Locality)-[:OF_TYPE]->(lt:LocalityType)"
			+
			"	 WHERE id(co)=102 AND id(lt)= 3 and NOT log IN prLogin " +
			"	 RETURN id(l) as id,l.latitudeLongitude as latitudeLongitude,l.name as name, " +
			"    count(DISTINCT p) ")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationRemoteAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup);

	@Query(" match (p:Person)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference), " +
			"  (p)-[:MADE]->(s:SelfDeclaration)-[:TO]->(co), " +
			"  (s)-[:AS_BEING_FROM]->()-[:IS_LOCATED_IN *0..]->(l:Locality)-[:OF_TYPE]->(lt:LocalityType) " +
			"  where id(co) = $idConference and id(lt) = $microregionChartAgroup AND (($meetings IS NULL) OR (id(m) IN $meetings)) " +
			"  return id(l) as id, l.latitudeLongitude as latitudeLongitude,l.name as name, " +
			"  p.name as cidadao, " +
			"  count (distinct p) as quantityParticipation")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipationPresentialAgroup(
		@Param("idConference") Long idConference, @Param("microregionChartAgroup") Long microregionChartAgroup, @Param("meetings") List<Long> meetings);

	
	@Query("MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), "
			+ "(nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p) "
			+ "WHERE id(ld)= $microregionLocalitySelected AND id(n)= $idConference "
			+ "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
			+ "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in $meetings OR $meetings is null) "
			+ "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
			+ "OPTIONAL MATCH (n)<-[t1:TO]-(self1:SelfDeclaration)<-[ma1:MADE]-(p1:Person), (self1)-[bg1:AS_BEING_FROM]->(nf)  WHERE ((p1)-[:CHECKED_IN_AT]->(me) OR $meetings is null)"
			+ "OPTIONAL MATCH (n)<-[t2:TO]-(self2:SelfDeclaration)<-[ma2:MADE]-(p2:Person), (self2)-[bg2:AS_BEING_FROM]->(l)   WHERE ((p2)-[:CHECKED_IN_AT]->(me) OR $meetings is null)"
			+ "return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, nf.name as name, "
			+ "(COUNT(distinct self1) + COUNT(distinct self2)) as quantityParticipation")
	List<MicroregionChartQueryDto> findDataMicroregionMapDashboardFromIdConferenceParticipation( @Param("idConference") Long idConference,
	@Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("meetings") List<Long> meetings);

	@Query("MATCH (lt:LocalityType)<-[ot:OF_TYPE]-(nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality)-[il:IS_LOCATED_IN]->(d:Domain) "
			+ " WHERE id(lt)= $idTypeLocality AND id(d)= $idDomain " + " OPTIONAL MATCH (l:Locality)<-[ins:IS_LOCATED_IN*]-(nf) "
			+ " OPTIONAL MATCH (lt2:LocalityType)<-[ot2:OF_TYPE]-(l) " + "return distinct id(lt2) as id, "
			+ "lt2.name as name")
	List<LocalityTypeDto> findDataTypeLocality( @Param("idDomain") Long idDomain, @Param("idTypeLocality") Long idTypeLocality);

	@Query(" WITH $idConference as Conference, " +
			"		 $microregionChartAgroup as LocalityTypeGrouping, " +
			"		 $structureItemSelected as StructureItemGrouping, " +
			"		 $origin as Origin, " +
			"		 $meetings as Meetings " +
			" MATCH (conf:Conference)<-[:ABOUT]-(a:Attend)-[:ABOUT]->(planItem:PlanItem) " +
			" WHERE id(conf)=Conference AND	((a:Comment AND NOT a.status IN ['rem' , 'pen' ]) or a:Highlight) " +
			" OPTIONAL MATCH (conf)<-[:OCCURS_IN]-(me:Meeting) " +
			" WHERE (ID(me) in Meetings OR Meetings is null) AND (Origin is null or (Origin = 'rem'  and NOT (a)-[:WHILE_IN]->()) or "
			+
			" (Origin = 'pre'  and (a)-[:WHILE_IN]->(me))) " +
			" OPTIONAL MATCH (planItem)-[:COMPOSES *0..]->(parentPI:PlanItem)-[:OBEYS]->(parentSt:StructureItem) " +
			" where (id(parentSt) = StructureItemGrouping) " +
			" OPTIONAL MATCH(a)-[:ABOUT]->(loc:Locality)-[:IS_LOCATED_IN *0..]->(parentLoc:Locality)-[:OF_TYPE]->(parentLocType:LocalityType) "
			+
			" where (id(parentLocType) = LocalityTypeGrouping) " +
			" with " +
			"	conf, " +
			"	collect(a) as attends, " +
			"	parentLoc, " +
			"	parentPI, " +
			"	planItem " +
			" MATCH (conf)<-[:ABOUT]-(com:Comment)-[:ABOUT]->(planItem) where com IN attends " +
			" MATCH (conf)<-[:ABOUT]-(hl:Highlight)-[:ABOUT]->(planItem) where hl IN attends " +
			" with parentLoc, parentPI, com, hl " +
			" return id(parentLoc) as id, " +
			"        parentLoc.latitudeLongitude as latitudeLongitude, " +
			"        parentLoc.name as name, " +
			"        count(DISTINCT com) as quantityComment, " +
			"        count(DISTINCT hl) as quantityHighlight, " +
			"        id(parentPI) as idPlanItem, " +
			"        parentPI.name as planItemName ")
	List<MicroregionChartQueryDto> findDataCommentHighlightWitoutFilter(@Param("idConference") Long idConference,
	@Param("microregionChartAgroup") Long microregionChartAgroup, @Param("structureItemSelected") Long structureItemSelected, @Param("origin") String origin, @Param("meetings")List<Long> meetings);


	@Query(" MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), (pl:PlanItem)<-[:COMPOSES]-(plParent:PlanItem)"
			+ ", (lt:LocalityType)<-[ot:OF_TYPE]-(nf:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p) "
			+ "WHERE id(n)=$idConference AND id(lt)= $microregionChartAgroup AND id(pl)= $structureItemPlanSelected"
			+ "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in $meetings OR $meetings is null) "
			+ "OPTIONAL MATCH (plParent)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
			+ "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(nf), (co1)-[:ABOUT]->(plParent) where NOT co1.status IN ['rem' , 'pen' ] AND ((co1.type = $origin) OR ($origin IS NULL)) AND ((co1)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co:Comment)-[:ABOUT]->(l), (co)-[:ABOUT]->(plParent) where NOT co.status IN ['rem' , 'pen' ] AND ((co.type = $origin) OR ($origin IS NULL)) AND ((co)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(nf), (co2)-[:ABOUT]->(plChildren) where NOT co2.status IN ['rem' , 'pen' ] AND ((co2.type = $origin) OR ($origin IS NULL)) AND ((co2)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co3:Comment)-[:ABOUT]->(l),(co3)-[:ABOUT]->(plChildren) where NOT co3.status IN ['rem' , 'pen' ] AND ((co3.type = $origin) OR ($origin IS NULL)) AND ((co3)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(nf), (ho1)-[:ABOUT]->(plParent) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho:Highlight)-[:ABOUT]->(l),(ho)-[:ABOUT]->(plParent) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(nf),(ho2)-[:ABOUT]->(plChildren) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho3:Highlight)-[:ABOUT]->(l),(ho3)-[:ABOUT]->(plChildren) "
			+ "return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, "
			+ "nf.name as name, (count(DISTINCT co) + count(DISTINCT co1) + count(DISTINCT co2) + count(DISTINCT co3)) as quantityComment, "
			+ "(count(DISTINCT ho) + count(DISTINCT ho1) + count(DISTINCT ho2) + count(DISTINCT ho3)) as quantityHighlight, "
			+ "id(plParent) as idPlanItem, plParent.name as planItemName")
	List<MicroregionChartQueryDto> findDataCommentHighlightStructureItemPlanSelected( @Param("idConference") Long idConference,
	@Param("microregionChartAgroup") Long microregionChartAgroup, @Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("origin") String origin, @Param("meetings") List<Long> meetings);

	@Query(" MATCH (n:Conference)-[ta:TARGETS]->(p:Plan)<-[te:COMPOSES]-(pl:PlanItem), "
			+ " (nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p), (pl)-[ob:OBEYS]->(st:StructureItem) "
			+ "WHERE id(n)=$idConference AND id(ld)= $microregionLocalitySelected AND id(st) = $structureItemSelected"
			+ "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in $meetings OR $meetings is null) "
			+ "OPTIONAL MATCH (pl)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
			+ "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(nf), (co1)-[:ABOUT]->(pl) where NOT co1.status IN ['rem' , 'pen' ] AND ((co1.type = $origin) OR ($origin IS NULL)) AND ((co1)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co:Comment)-[:ABOUT]->(l), (co)-[:ABOUT]->(pl) where NOT co.status IN ['rem' , 'pen' ] AND ((co.type = $origin) OR ($origin IS NULL)) AND ((co)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(nf), (co2)-[:ABOUT]->(plChildren) where NOT co2.status IN ['rem' , 'pen' ] AND ((co2.type = $origin) OR ($origin IS NULL)) AND ((co2)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co3:Comment)-[:ABOUT]->(l),(co3)-[:ABOUT]->(plChildren)  where NOT co3.status IN ['rem' , 'pen' ] AND ((co3.type = $origin) OR ($origin IS NULL)) AND ((co3)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(nf), (ho1)-[:ABOUT]->(pl) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho:Highlight)-[:ABOUT]->(l),(ho)-[:ABOUT]->(pl) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(nf),(ho2)-[:ABOUT]->(plChildren) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho3:Highlight)-[:ABOUT]->(l),(ho3)-[:ABOUT]->(plChildren) "
			+ "return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, "
			+ "nf.name as name, (count(DISTINCT co) + count(DISTINCT co1) + count(DISTINCT co2) + count(DISTINCT co3)) as quantityComment, "
			+ "(count(DISTINCT ho) + count(DISTINCT ho1) + count(DISTINCT ho2) + count(DISTINCT ho3)) as quantityHighlight, "
			+ "id(pl) as idPlanItem, pl.name as planItemName")
	List<MicroregionChartQueryDto> findDataCommentHighlightLocalitySelected( @Param("idConference") Long idConference,
	 @Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemSelected") Long structureItemSelected, @Param("origin") String origin, @Param("meetings") List<Long> meetings);

	@Query(" MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), (pl:PlanItem)<-[:COMPOSES]-(plParent:PlanItem), "
			+ " (nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p) "
			+ "WHERE id(n)=$idConference AND id(ld)= $microregionLocalitySelected AND id(pl) = $structureItemPlanSelected"
			+ "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in $meetings OR $meetings is null) "
			+ "OPTIONAL MATCH (plParent)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
			+ "OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(nf), (co1)-[:ABOUT]->(plParent) where NOT co1.status IN ['rem' , 'pen' ] AND ((co1.type = $origin) OR ($origin IS NULL)) AND ((co1)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co:Comment)-[:ABOUT]->(l), (co)-[:ABOUT]->(plParent) where NOT co.status IN ['rem' , 'pen' ] AND ((co.type = $origin) OR ($origin IS NULL)) AND ((co)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(nf), (co2)-[:ABOUT]->(plChildren) where NOT co2.status IN ['rem' , 'pen' ] AND ((co2.type = $origin) OR ($origin IS NULL)) AND ((co2)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co3:Comment)-[:ABOUT]->(l),(co3)-[:ABOUT]->(plChildren)  where NOT co3.status IN ['rem' , 'pen' ] AND ((co3.type = $origin) OR ($origin IS NULL)) AND ((co3)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(nf), (ho1)-[:ABOUT]->(plParent) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho:Highlight)-[:ABOUT]->(l),(ho)-[:ABOUT]->(plParent) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(nf),(ho2)-[:ABOUT]->(plChildren) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho3:Highlight)-[:ABOUT]->(l),(ho3)-[:ABOUT]->(plChildren) "
			+ "return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, "
			+ "nf.name as name, (count(DISTINCT co) + count(DISTINCT co1) + count(DISTINCT co2) + count(DISTINCT co3)) as quantityComment, "
			+ "(count(DISTINCT ho) + count(DISTINCT ho1) + count(DISTINCT ho2) + count(DISTINCT ho3)) as quantityHighlight, "
			+ "id(plParent) as idPlanItem, plParent.name as planItemName")
	List<MicroregionChartQueryDto> findDataCommentHighlightAllFilter( @Param("idConference") Long idConference,
	@Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("origin") String origin,
	@Param("meetings") List<Long> meetings);

	@Query("MATCH (n:Conference)-[ta:TARGETS]->(p:Plan)<-[te:COMPOSES]-(pl:PlanItem), "
			+ " (nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality), (d:Domain)<-[at:APPLIES_TO]-(p), (pl)-[ob:OBEYS]->(st:StructureItem)"
			+ " WHERE id(n)=$idConference AND id(ld)= $microregionLocalitySelected AND id(st)= $structureItemPlanSelected "
			+ "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in $meetings OR $meetings is null) "
			+ "OPTIONAL MATCH (pl)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(ld), (co1)-[:ABOUT]->(pl) where NOT co1.status IN ['rem' , 'pen' ] AND ((co1.type = $origin) OR ($origin IS NULL)) AND ((co1)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(ld),(co2)-[:ABOUT]->(plChildren)  where NOT co2.status IN ['rem' , 'pen' ] AND ((co2.type = $origin) OR ($origin IS NULL)) AND ((co2)-[:DURING]->(me) OR $meetings is null)  "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(ld),(ho1)-[:ABOUT]->(pl) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(ld),(ho2)-[:ABOUT]->(plChildren) "
			+ "return id(ld) as id, ld.latitudeLongitude as latitudeLongitude, ld.name as name, "
			+ "(count(DISTINCT co1) + count(DISTINCT co2)) as quantityComment, (count(DISTINCT ho1) + count(DISTINCT ho2)) as quantityHighlight, "
			+ " id(pl) as idPlanItem, pl.name as planItemName")
	List<MicroregionChartQueryDto> findDataCommentHighlightLocalitySelectedLastLevel( @Param("idConference") Long idConference,
	@Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("origin") String origin,
	@Param("meetings") List<Long> meetings);

	@Query(" MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), (pl:PlanItem)<-[:COMPOSES]-(plParent:PlanItem), "
			+ " (nf:Locality)-[ili:IS_LOCATED_IN]->(ld:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p) "
			+ "WHERE id(n)=$idConference AND id(ld)= $microregionLocalitySelected AND id(pl) = $structureItemPlanSelected"
			+ "OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in $meetings OR $meetings is null) "
			+ "OPTIONAL MATCH (plParent)<-[com2:COMPOSES*]-(plChildren:PlanItem) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(ld), (co1)-[:ABOUT]->(plParent) where NOT co1.status IN ['rem' , 'pen' ] AND ((co1.type = $origin) OR ($origin IS NULL)) AND ((co1)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(co2:Comment)-[:ABOUT]->(ld),(co2)-[:ABOUT]->(plChildren) where NOT co2.status IN ['rem' , 'pen' ] AND ((co2.type = $origin) OR ($origin IS NULL)) AND ((co2)-[:DURING]->(me) OR $meetings is null) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(ld),(ho1)-[:ABOUT]->(plParent) "
			+ "OPTIONAL MATCH (n)<-[:ABOUT]-(ho2:Highlight)-[:ABOUT]->(ld),(ho2)-[:ABOUT]->(plChildren) "
			+ "return id(ld) as id, ld.latitudeLongitude as latitudeLongitude, "
			+ "ld.name as name, (count(DISTINCT co1) + count(DISTINCT co2)) as quantityComment, "
			+ "(count(DISTINCT ho1) + count(DISTINCT ho2)) as quantityHighlight, "
			+ "id(plParent) as idPlanItem, plParent.name as planItemName")
	List<MicroregionChartQueryDto> findDataCommentHighlightAllFilterLastLevel( @Param("idConference") Long idConference,
	@Param("microregionLocalitySelected") Long microregionLocalitySelected, @Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("origin") String origin,
	@Param("meetings") List<Long> meetings);

	@Query("MATCH (n:Conference)-[ta:TARGETS]->(p:Plan), (pl:PlanItem), (lt:LocalityType)<-[ot:OF_TYPE]-(nf:Locality)-[il:IS_LOCATED_IN]->(d:Domain)<-[at:APPLIES_TO]-(p)"
			+ " WHERE id(n)=$idConference AND id(lt)= $microregionChartAgroup AND id(pl) = $structureItemPlanSelected"
			+ " OPTIONAL MATCH (l:Locality)-[in:IS_LOCATED_IN*]->(nf) "
			+ " OPTIONAL MATCH (n)<-[:OCCURS_IN]-(me:Meeting) where (ID(me) in $meetings OR $meetings is null) "
			+ " OPTIONAL MATCH (n)<-[:ABOUT]-(co1:Comment)-[:ABOUT]->(nf), (co1)-[:ABOUT]->(pl) where NOT co1.status IN ['rem' , 'pen' ] AND ((co1.type = 'pre' ) OR ('pre'  IS NULL)) AND ((co1)-[:DURING]->(me) OR $meetings is null) "
			+ " OPTIONAL MATCH (n)<-[:ABOUT]-(co:Comment)-[:ABOUT]->(l), (co)-[:ABOUT]->(pl) where NOT co.status IN ['rem' , 'pen' ] AND ((co.type = 'pre' ) OR ('pre'  IS NULL)) AND ((co)-[:DURING]->(me) OR $meetings is null) "
			+ " OPTIONAL MATCH (n)<-[:ABOUT]-(ho1:Highlight)-[:ABOUT]->(nf), (ho1)-[:ABOUT]->(pl) "
			+ " OPTIONAL MATCH (n)<-[:ABOUT]-(ho:Highlight)-[:ABOUT]->(l),(ho)-[:ABOUT]->(pl) "
			+ " return id(nf) as id, nf.latitudeLongitude as latitudeLongitude, nf.name as name, (count(DISTINCT co) + count(DISTINCT co1)) as quantityComment, (count(DISTINCT ho) + count(DISTINCT ho1)) as quantityHighlight, id(pl) as idPlanItem, pl.name as planItemName")
	List<MicroregionChartQueryDto> findDataCommentHighlightStructureItemPlanSelectedLastLevel( @Param("idConference") Long idConference,
	@Param("microregionChartAgroup") Long microregionChartAgroup, @Param("structureItemPlanSelected") Long structureItemPlanSelected, @Param("origin") String origin, @Param("meetings") List<Long> meetings);

}



