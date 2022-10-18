package br.gov.es.participe.repository;

import br.gov.es.participe.model.SelfDeclaration;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SelfDeclarationRepository extends Neo4jRepository<SelfDeclaration, Long> {

  @Query("MATCH (s:SelfDeclaration)-[m:MADE]-(p:Person) "
      + "WHERE id(p) = $id "
      + "RETURN s, m, p"
  )
  List<SelfDeclaration> findAllByIdPerson( @Param("id") Long id);

  @Query("MATCH (c:Conference)<-[t:TO]-(s:SelfDeclaration)<-[m:MADE]-(p:Person) "
      + "WHERE id(c)=$idConference AND id(p)=$idPerson "
      + "RETURN s, t, c, m, p, ["
      + " [(s)-[a:AS_BEING_FROM]->(l:Locality) | [a,l]] "
      + "] ")
  SelfDeclaration findByConferenceIdAndPersonId( @Param("idConference") Long idConference, @Param("idPerson") Long idPerson);

  @Query("MATCH (s:SelfDeclaration) "
      + "WHERE id(s)=$id "
      + "OPTIONAL MATCH (s)-[a:AS_BEING_FROM]-(l:Locality) "
      + "OPTIONAL MATCH (s)-[t:TO]-(c:Conference) "
      + "OPTIONAL MATCH (s)-[m:MADE]-(p:Person) "
      + "RETURN s, c, p, l")
  SelfDeclaration find( @Param("id") Long id);
}
