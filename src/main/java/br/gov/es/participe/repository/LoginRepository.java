package br.gov.es.participe.repository;

import br.gov.es.participe.model.Login;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface LoginRepository extends Neo4jRepository<Login, Long> {

  @Query("MATCH (person:Person)-[made:MADE]->(login:Login) "
         + "WHERE id(person)={0} "
         + "RETURN login, made, person"
  )
  List<Login> findAllByPerson(Long idPerson);
}
