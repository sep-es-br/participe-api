/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.service;

import Report.ReportConfig;
import br.gov.es.participe.util.domain.report.RootFolderRepositoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.repo.FileRepositoryService;
import net.sf.jasperreports.repo.RepositoryService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
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
                
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            
            List<Callable<Void>> taskList = new ArrayList<>();
            
            Resource[] jrxmlResources = resourceResolver.getResources("classpath:/jasper/ProposeReport/*.jrxml");
            
            for(Resource resource : jrxmlResources) {
                taskList.add(createJasperCompileTask(resource, tempDir));
            }
            
            try {
                List<Future<Void>> returns = executor.invokeAll(taskList);
                
                for(Future<Void> future : returns) {
                    try {
                        future.get();
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e.getCause());
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                executor.shutdown();
            }
            
            Connection connection = DriverManager.getConnection(
            "jdbc:neo4j:" + this.urlConnection,
            this.userName,
            this.passwordNeo4j);
            
            
            JasperReport report = (JasperReport) JRLoader.loadObject(tempDir.resolve("ProposeReport_main.jasper").toFile());
            
           SimpleJasperReportsContext ctx = new SimpleJasperReportsContext();
            
            RootFolderRepositoryService fileRepositoryService = new RootFolderRepositoryService( ctx, tempDir);
            ctx.setExtensions(RepositoryService.class, Collections.singletonList(fileRepositoryService));
            report.setJasperReportsContext(ctx);
            
            
            Map<String, Object> params = new HashMap<>();
            params.put("ID_CONFERENCE", idConference);
            params.put("REPORT_CONNECTION", connection);
            
            JasperPrint print = JasperFillManager.getInstance(ctx).fill(report, params, connection);

            
            try (ByteArrayInputStream pdfIs = new ByteArrayInputStream(
                    JasperExportManager.exportReportToPdf(print)
            )) {
                return new InputStreamResource(pdfIs);
            }
            

        } catch (JRException | SQLException | IOException e) {
            
            throw new RuntimeException("Erro ao gerar Relatório", e);
        }
        
    }
    
    private Callable<Void> createJasperCompileTask(Resource jrxmlResource, Path tempDir){
        return () -> {
                
            try(InputStream is = jrxmlResource.getInputStream()){
                JasperReport jasperReport = JasperCompileManager.compileReport(is);

                File jasperFile = tempDir.resolve(jrxmlResource.getFilename().replace(".jrxml", ".jasper")).toFile();
                try (FileOutputStream fos = new FileOutputStream(jasperFile)) {
                    JRSaver.saveObject(jasperReport, fos);
                }
            } catch (IOException | JRException ex) {
                Logger.getGlobal().log(Level.SEVERE, jrxmlResource.getFilename() + ": " + ex.getLocalizedMessage(), ex);
                throw new RuntimeException(ex);
            }

            return null;

        };
    }
    
    
}
