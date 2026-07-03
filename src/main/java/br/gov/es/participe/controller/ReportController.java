package br.gov.es.participe.controller;

import br.gov.es.participe.service.ReportService;
import br.gov.es.participe.util.domain.report.ReportJobManager;
import br.gov.es.participe.util.dto.MessageDto;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
@RequestMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private ReportJobManager jobManager;
            
    private final Logger log = LoggerFactory.getLogger(ReportController.class);
            
    @PostMapping("/proposeReport/{idConference}")
    public ResponseEntity<?> createOrDownloadProposeReport(
            @PathVariable Integer idConference
    ) {
        
        try {
            CompletableFuture<Resource> job = jobManager.getOrCreateJob(idConference, 
                                                            () -> reportService.generateProposeReport(idConference));
            
            Resource resource = job.get(55, TimeUnit.SECONDS);
                            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ProposalReport_" + idConference + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (TimeoutException ex) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of("status", "PROCESSING"));
        } catch (ExecutionException ex) {
            // O processamento em background (o Supplier) FALHOU de verdade.
            Throwable causaReal = ex.getCause(); // JRException, SQLException, etc.

            UUID errorLogId = UUID.randomUUID();
            log.error("Erro interno no processamento do Report do conferencia " + idConference + " (Log ID: " + errorLogId + ")", causaReal);

            // Opcional: Se falhou, remove do mapa para que o cliente não fique lendo uma falha em cache
            // e force-o a gerar um novo UUID se tentar de novo.
            jobManager.removeJob(idConference); 

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new MessageDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro no processamento do relatório. ID do log: " + errorLogId));

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); // Boa prática de concorrência
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException ex) {
            
            // Opcional: Se falhou, remove do mapa para que o cliente não fique lendo uma falha em cache
            // e force-o a gerar um novo UUID se tentar de novo.
            jobManager.removeJob(idConference); 

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new MessageDto(HttpStatus.NOT_FOUND.value(), "Job (" + idConference + ") não existe!!!"));
        }
        
        
    }
    

}
