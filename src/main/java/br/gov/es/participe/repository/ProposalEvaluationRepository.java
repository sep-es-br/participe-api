package br.gov.es.participe.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.controller.dto.ProposalEvaluationDto;
import br.gov.es.participe.model.ProposalEvaluation;

public interface ProposalEvaluationRepository extends Neo4jRepository<ProposalEvaluation, Long> {

    @Query(
        "MATCH (comment:Comment) " +
        "WHERE id(comment)=$commentId " +
        "RETURN exists((comment)<-[:EVALUATES]-())"
    )
    Boolean existsRelationshipByCommentId(@Param("commentId") Long commentId);

    // Mudar retorno
    @Query(
        "MATCH (person:Person)-[eval:EVALUATES]->(comment:Comment)-[:ABOUT]->(conference:Conference) " +
        "WHERE id(conference)=$conferenceId " +
        "RETURN person.name AS personName, comment.text AS description " +
        "LIMIT 20"
    )
    List<ProposalEvaluationDto> findAllByConferenceId(@Param("conferenceId") Long conferenceId);
}

/*
 "MATCH (comment: Comment)-[:ABOUT]->(conference:Conference), " + 
        "(comment)-[:ABOUT]->(planItem:PlanItem), (comment)-[:ABOUT]->(locality:Locality), " +
        "(planItem)-[:COMPOSES]->(area: PlanItem) " +
        "WHERE comment.type = 'prop' " +
        "AND comment.status IN ['pub', 'pen'] " +
        "AND id(conference)=$conferenceId " +
        "RETURN locality AS locality, " + 
        "area AS planItem, " +
        "comment.text AS description " +
        "LIMIT 1"
 */



/*
ids   conferencias
664	  "Audiências Públicas do Orçamento - 2021" -> 1
13743 "Audiências Públicas do Orçamento 2022" -> 0
18563 "Audiências Públicas do Orçamento - 2023" -> 1665
29312 "Audiências Públicas do Orçamento - 2024" -> 2314

 */

/*
areaEstrategicaId: 541
citizenName: "DOUGLAS GONCALVES JACOB"
commentId: 10342
conferenceId: 13743
disableModerate: false
from: "Remote"
localityId: 63
localityName: "Metropolitana"
nameAreaEstrategica: "03. Saúde Integral"
planItemId: 479
planItemName: "Modelo da atenção e de vigilância em saúde"
status: "Published"
structureItemId: 448
structureItemName: "Desafio"
text: "Financiar a construção de Centros de Atenção Psicossoial (CAPS) em todos os municípios da Região Metropolitana, a fim de garantir um espaço adequado para o cuidado em Saúde Mental da população."
time: "2021-06-01T17:29:47.790Z"
type: "Comment" 
*/
