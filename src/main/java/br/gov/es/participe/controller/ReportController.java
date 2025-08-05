package br.gov.es.participe.controller;

import br.gov.es.participe.service.ReportService;
import br.gov.es.participe.util.dto.MessageDto;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin
@RequestMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
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
