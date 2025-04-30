package br.gov.es.participe.controller;

import br.gov.es.participe.service.ReportService;
import java.util.Arrays;
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
        
        
        Resource resource = reportService.generateProposeReport(idConference);
            
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ProposalReport_" + idConference + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
        
        
    }
    

}
