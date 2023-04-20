package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.LocalityCitizenSelectDto;
import br.gov.es.participe.model.Locality;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocalityRepository extends Neo4jRepository<Locality, Long> {

    @Query("MATCH (l:Locality)-[:OF_TYPE]->(t:LocalityType) "
    + " WHERE apoc.text.clean(l.name) CONTAINS apoc.text.clean($quey) "
    + " AND id(t) = $typeId "
    + " RETURN l, [(l)-[lt:OF_TYPE]->(t:LocalityType) | [ lt, t ] ] ORDER BY l.name ")
List<Locality> search( @Param("quey") String quey, @Param("typeId") Long typeId);

@Query("MATCH (domain:Domain)<-[ili:IS_LOCATED_IN]-(l:Locality)-[:OF_TYPE]->(t:LocalityType) "
    + " WHERE apoc.text.clean(l.name) = apoc.text.clean($name) "
    + " AND id(t) = $typeId "
    + " RETURN domain, ili, l, [(l)-[lt:OF_TYPE]->(t:LocalityType) | [ lt, t ] ] ORDER BY l.name ")
List<Locality> findByNameAndType( @Param("name") String name, @Param("typeId") Long typeId);

@Query("MATCH (domain:Domain)<-[:IS_LOCATED_IN]-(child:Locality)-[:IS_LOCATED_IN]->(parent:Locality) WHERE id(domain) = $idDomain AND id(parent) = $idParent RETURN child")
List<Locality> findChildren( @Param("idDomain") Long idDomain, @Param("idParent") Long idParent);

@Query("MATCH (d:Domain)<-[:IS_LOCATED_IN]-(l:Locality)" +
    " WHERE id(d) = $idDomain RETURN l, " +
    " [ [(l)<-[btl:IS_LOCATED_IN]-(lc:Locality)-[:IS_LOCATED_IN]->(d) | [btl, lc] ], " +
    "	[(l)-[lt:OF_TYPE]->(t:LocalityType) | [lt, t]]" +
    "]")
List<Locality> findByDomain( @Param("idDomain") Long idDomain);

@Query("MATCH (c:Conference)-[t:TARGETS]-(p:Plan)-[a:APPLIES_TO]->(d:Domain)"
    + " OPTIONAL MATCH (d)<-[i1:IS_LOCATED_IN]-(child:Locality)-[o:OF_TYPE]->(lt:LocalityType) "
    + " WHERE id(c)=$idConference "
    + " RETURN i1, child, [(child)-[i:IS_LOCATED_IN]->(children:Locality) | [ i, children ] ]")
List<Locality> findCardsByIdConference( @Param("idConference") Long idConference);

@Query(" MATCH (lt:LocalityType)<-[lc:LOCALIZES_CITIZEN_BY]-(c:Conference)-[t:TARGETS]->(p:Plan)"
    + " OPTIONAL MATCH (p)-[a:APPLIES_TO]->(d:Domain)<-[i:IS_LOCATED_IN]-(l:Locality)-[o:OF_TYPE]->(lt) "
    + " WHERE id(c)=$idConference "
    + " RETURN l")
List<Locality> findLocalitiesToComplement( @Param("idConference") Long idConference);




@Query(" match (p:Person)-[:MADE]->(lo:Login)-[:TO]->(co:Conference)<-[:TO]-(s:SelfDeclaration)<-[m:MADE]-(p), (s)-[:AS_BEING_FROM]->(loc:Locality) " + 
       " WHERE id(co)=$idConference RETURN count(DISTINCT loc) ")
Integer countLocalitiesParticipation( @Param("idConference") Long idConference);




@Query("MATCH (l:Locality) DETACH DELETE l")
void deleteAll();

@Query(
    "MATCH (c:Conference)-[t:TARGETS]-(p:Plan)-[a:APPLIES_TO]->(d:Domain) " +
        "MATCH (p)-[reg:REGIONALIZABLE]->(lt:LocalityType) " +
        "MATCH (d)<-[i1:IS_LOCATED_IN]-(loc:Locality)-[o:OF_TYPE]->(lt) " +
        "WHERE id(c)=$idConference " +
        "AND (loc.name IS NULL OR loc.name CONTAINS ($name)) " +
        "WITH id(lt) AS localityTypeId, lt.name AS localityTypeName, id(loc) AS localityId, " +
        "loc.name AS localityName " +
        "ORDER BY localityName " +
        "RETURN localityTypeId, localityTypeName, " +
        "collect({localityId: localityId, localityName: localityName}) AS localities"
)
LocalityCitizenSelectDto getLocalitiesToDisplay( @Param("idConference") Long idConference, @Param("name") String name);
}
