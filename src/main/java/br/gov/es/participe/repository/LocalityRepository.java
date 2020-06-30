package br.gov.es.participe.repository;

import br.gov.es.participe.model.Locality;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface LocalityRepository extends Neo4jRepository<Locality, Long> {

    @Query("MATCH (l:Locality)-[:OF_TYPE]->(t:LocalityType) "
               + " WHERE ext.translate(l.name) CONTAINS ext.translate({0}) "
               + " AND id(t) = {1} "
               + " RETURN l, [(l)-[lt:OF_TYPE]->(t:LocalityType) | [ lt, t ] ] ORDER BY l.name ")
    List<Locality> search(String quey, Long typeId);

    @Query("MATCH (domain:Domain)<-[ili:IS_LOCATED_IN]-(l:Locality)-[:OF_TYPE]->(t:LocalityType) "
               + " WHERE ext.translate(l.name) = ext.translate({0}) "
               + " AND id(t) = {1} "
               + " RETURN domain, ili, l, [(l)-[lt:OF_TYPE]->(t:LocalityType) | [ lt, t ] ] ORDER BY l.name ")
    List<Locality> findByNameAndType(String name, Long typeId);

    @Query("MATCH (domain:Domain)<-[:IS_LOCATED_IN]-(child:Locality)-[:IS_LOCATED_IN]->(parent:Locality) WHERE id(domain) = {0} AND ID(parent) = {1} RETURN child")
    List<Locality> findChildren(Long idDomain, Long idParent);

    @Query("MATCH (d:Domain)<-[:IS_LOCATED_IN]-(l:Locality)"+
    			" WHERE id(d) = {0} RETURN l, "+
                " [ [(l)<-[btl:IS_LOCATED_IN]-(lc:Locality)-[:IS_LOCATED_IN]->(d) | [btl, lc] ], "+
    			"	[(l)-[lt:OF_TYPE]->(t:LocalityType) | [lt, t]]"+
    			"]")
    List<Locality> findByDomain(Long idDomain);
    
    @Query("MATCH (c:Conference)-[t:TARGETS]-(p:Plan)-[a:APPLIES_TO]->(d:Domain)"
    		+" OPTIONAL MATCH (d)<-[i1:IS_LOCATED_IN]-(child:Locality)-[o:OF_TYPE]->(lt:LocalityType) "
    		+" WHERE id(c)={0} "
    		+" RETURN i1, child, [(child)-[i:IS_LOCATED_IN]->(children:Locality) | [ i, children ] ]")
    List<Locality> findCardsByIdConference(Long idConference);
    
    @Query("MATCH(l:Locality) "
    		+" WHERE id(l) = {0} "
    		+" RETURN l "
    		+" ,[ "
    		+"		[(l)<-[a:AS_BEING_FROM]-(s:SelfDeclaration) | [a,s]] "
    		+" ]")
    Locality findSelfDeclarationById(Long id);
    
    @Query(" MATCH (c:Conference)<-[t:TO]-(s:SelfDeclaration)<-[m:MADE]-(p:Person) "
    		+" OPTIONAL MATCH (s)-[a:AS_BEGIN_FROM]->(l:Locality) "
    		+" WHERE id(c)={0} AND id(p)={1} "
    		+" RETURN l")
    Locality findByIdConferenceAndIdPerson(Long idConference, Long idPerson);
    
    @Query(" MATCH (lt:LocalityType)<-[lc:LOCALIZES_CITIZEN_BY]-(c:Conference)-[t:TARGETS]->(p:Plan)"
    		+" OPTIONAL MATCH (p)-[a:APPLIES_TO]->(d:Domain)<-[i:IS_LOCATED_IN]-(l:Locality)-[o:OF_TYPE]->(lt) "
    		+" WHERE id(c)={0} "
    		+" RETURN l")
    List<Locality> findLocalitiesToComplement(Long idConference);
    
    @Query("MATCH (l:Locality)<-[a:AS_BEING_FROM]-(s:SelfDeclaration)-[t:TO]->(c:Conference) WHERE id(c)= {0} RETURN COUNT(DISTINCT l)")
    Integer countLocalitiesParticipation(Long idConference);

    @Query("MATCH (l:Locality) DETACH DELETE l")
    void deleteAll();
}
