/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.service;

import br.gov.es.participe.util.domain.report.RootFolderRepositoryService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.repo.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

/**
 *
 * @author gean.carneiro
 */
@Service
public class ReportService {
        
    
    @Value("${spring.data.neo4j.uri}")
    private String urlConnection;

    @Value("${spring.data.neo4j.username}")
    private String userName;

    @Value("${spring.data.neo4j.password}")
    private String passwordNeo4j;
    
    @Value("${app.pathImagens}")
    private String imgPath;
    
    @Autowired
    private ResourcePatternResolver resourceResolver;
        
    
    public Resource generateProposeReport(int idConference) {
        
        try {
            Path tempDir = Files.createTempDirectory("ProposeReport_");
            Path tempImgDir = tempDir.resolve("imgs");
            Files.createDirectory(tempImgDir);
                        
            Resource[] imgResources = resourceResolver.getResources("classpath:/jasper/ProposeReport/imgs/*");
            
            for(Resource resource : imgResources) {
                try (InputStream is = resource.getInputStream()) {
                    Files.copy(is, tempImgDir.resolve(resource.getFilename()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
                      
            Resource[] jrxmlResources = resourceResolver.getResources("classpath:/jasper/ProposeReport/*.jasper");
            
            for(Resource resource : jrxmlResources) {
                try (InputStream is = resource.getInputStream()) {
                    Files.copy(is, tempDir.resolve(resource.getFilename()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            
            Class.forName("org.neo4j.jdbc.bolt.BoltDriver");
                                    
            try(Connection connection = DriverManager.getConnection(
                    "jdbc:neo4j:" + this.urlConnection,
                    this.userName,
                    this.passwordNeo4j)){
                
                
                JasperReport report = (JasperReport) JRLoader.loadObject(tempDir.resolve("ProposeReport_main.jasper").toFile());
            
                SimpleJasperReportsContext ctx = new SimpleJasperReportsContext();

                RootFolderRepositoryService fileRepositoryService = new RootFolderRepositoryService( ctx, tempDir);
                ctx.setExtensions(RepositoryService.class, Collections.singletonList(fileRepositoryService));
                report.setJasperReportsContext(ctx);


                Map<String, Object> params = new HashMap<>();
                params.put("ID_CONFERENCE", idConference);
                params.put("REPORT_CONNECTION", connection);
                params.put("IMG_PATH", imgPath);

                JasperPrint print = JasperFillManager.getInstance(ctx).fill(report, params, connection);
                
                
                try (ByteArrayInputStream pdfIs = new ByteArrayInputStream(
                    JasperExportManager.exportReportToPdf(print)
                )) {
                    return new InputStreamResource(pdfIs);
                }

                
            }

        } catch (JRException | SQLException | IOException e) {
            
            throw new RuntimeException("Erro ao gerar Relatório", e);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Erro ao gerar Relatório", ex);
        }
        
    }
    
    
}
