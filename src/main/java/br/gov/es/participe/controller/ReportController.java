package br.gov.es.participe.controller;

import br.gov.es.participe.service.ReportService;
import br.gov.es.participe.util.domain.report.JobStatus;
import br.gov.es.participe.util.domain.report.ReportJobManager;
import br.gov.es.participe.util.dto.MessageDto;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin
@RequestMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private ReportJobManager jobManager;
    
    
    @PostMapping("/proposeReport")
    public ResponseEntity<?> startProposeReport(@RequestParam int idConference) {
        UUID jobId = reportService.startReportJob(idConference);
        
        return ResponseEntity.ok(Map.of("uuid", jobId.toString()));
    }
    
    
    @GetMapping("/proposeReport/{jobId}/status")
    public ResponseEntity<?> getProposeReportStatus(@PathVariable String jobId){
        JobStatus status = jobManager.getStatus(UUID.fromString(jobId));
        if(status == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("status", status.toString()));
    }
    
    @GetMapping("/proposeReport/{jobId}/download")
    public ResponseEntity<?> downloadProposeReport(@PathVariable String jobId){
        Resource resource = jobManager.getReport(UUID.fromString(jobId));

        if(resource == null) return ResponseEntity.notFound().build();
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ProposalReport_" + jobId + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }
    
    @GetMapping("/proposeReport")
    public ResponseEntity<?> getProposeReport(@RequestParam int idConference) {
        
        try {
            Resource resource = reportService.generateProposeReport(idConference);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ProposalReport_" + idConference + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

        } catch (Exception ex) {
            // Log do erro
            UUID uuid = UUID.randomUUID();
            
            Logger.getGlobal().log(Level.SEVERE, "Erro: " + uuid, ex);

            // Retorna JSON com erro e content-type correto
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MessageDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro: " + uuid));
            
            
        }
        
        
    }
    

}
