package br.gov.es.participe.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.model.Organization;
import br.gov.es.participe.model.Person;

public interface EvaluationSectionsRepository extends Neo4jRepository<Organization, Long> {
    

    @Query(
        "MATCH (person: Person) " +
        "WHERE apoc.text.toUpperCase(person.name) CONTAINS $personName " +
        "RETURN person"
    )
    Person findPersonByName(@Param("personName") String personName);

}
