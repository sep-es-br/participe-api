package br.gov.es.participe.repository;

import br.gov.es.participe.model.SelfDeclaration;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface SelfDeclarationRepository extends Neo4jRepository<SelfDeclaration, Long> {

  @Query("MATCH (s:SelfDeclaration)-[m:MADE]-(p:Person) "
      + "WHERE id(p) = {0} "
      + "RETURN s, m, p"
  )
  List<SelfDeclaration> findAllByIdPerson(Long id);

  @Query("MATCH (c:Conference)<-[t:TO]-(s:SelfDeclaration)<-[m:MADE]-(p:Person) "
      + "WHERE id(c)={0} AND id(p)={1} "
      + "RETURN s, t, c, m, p, ["
      + " [(s)-[a:AS_BEING_FROM]->(l:Locality) | [a,l]] "
      + "] ")
  SelfDeclaration findByConferenceIdAndPersonId(Long idConference, Long idPerson);

  @Query("MATCH (s:SelfDeclaration) "
      + "WHERE id(s)={0} "
      + "OPTIONAL MATCH (s)-[a:AS_BEING_FROM]-(l:Locality) "
      + "OPTIONAL MATCH (s)-[t:TO]-(c:Conference) "
      + "OPTIONAL MATCH (s)-[m:MADE]-(p:Person) "
      + "RETURN s, c, p, l")
  SelfDeclaration find(Long id);
}
