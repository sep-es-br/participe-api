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
import net.sf.jasperreports.repo.RepositoryService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author gean.carneiro
 */
@Service
public class ReportService {
        
    @Value("${report.proposeReportFolder}")
    private String proposeReportFolder;
    
    
    @Value("${spring.data.neo4j.uri}")
    private String urlConnection;

    @Value("${spring.data.neo4j.username}")
    private String userName;

    @Value("${spring.data.neo4j.password}")
    private String passwordNeo4j;

    
    public Resource generateProposeReport(int idConference) {
        
        try {
            File reportFolder = new File(proposeReportFolder);
            
            File reportConfigFile = new File(reportFolder, "reportConfig.json");
            ReportConfig reportConfig;
            try (FileInputStream fis = new FileInputStream(reportConfigFile)) {
                reportConfig = new ObjectMapper().readValue(
                    fis,
                    ReportConfig.class);
            }
            
                        
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            
            List<Callable<Void>> taskList = new ArrayList<>();
                        
            for (File fileJrxml : Arrays.stream(reportFolder.listFiles())
                                            .filter(file -> file.getName().endsWith(".jrxml") || file.getName().endsWith(".jxml") )
                                            .collect(Collectors.toList())) {
                
                final String reportName = fileJrxml.getName().split("\\.")[0];
                final File outFile = new File(reportFolder, reportName + ".jasper");
                
                final String hash;
                try (FileInputStream fis = new FileInputStream(fileJrxml)) {
                    hash = DigestUtils.sha256Hex(fis);
                }
                final File hashFile = new File(reportFolder, reportName + ".hash");
                if(
                    !outFile.exists() ||
                    !hashFile.exists() ||
                    !hash.equals(Files.readString(hashFile.toPath()))
                ){
                    taskList.add(() -> {
                        
                        try (
                            FileInputStream fileIs = new FileInputStream(fileJrxml);
                            FileOutputStream fileOs = new FileOutputStream(outFile)
                        ) {
                            JasperCompileManager.compileReportToStream(fileIs, fileOs);

                            Files.writeString(hashFile.toPath(), hash, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        } catch (JRException | IOException ex){
                            throw new RuntimeException(fileJrxml.getName() + ": " + ex.getLocalizedMessage(), ex);
                        }
                        
                        return null;

                    });
                }
                    
                   
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
            
            File reportMain = new File(proposeReportFolder + "/" + reportConfig.getMainReport() + ".jasper");
            RootFolderRepositoryService repositoryService = new RootFolderRepositoryService(reportFolder);
            
            SimpleJasperReportsContext ctx = new SimpleJasperReportsContext();
            
            ctx.setExtensions(RepositoryService.class, Collections.singletonList(repositoryService));
            
            
            JasperReport report = (JasperReport) JRLoader.loadObject(ctx, reportMain);
            
            Map<String, Object> params = new HashMap<>();
            params.put("ID_CONFERENCE", idConference);
            params.put("REPORT_CONNECTION", connection);
            params.put("ROOT", proposeReportFolder);
            
            JasperPrint print = JasperFillManager.fillReport(
                report, 
                params, connection);

            try (ByteArrayInputStream pdfIs = new ByteArrayInputStream(
                    JasperExportManager.exportReportToPdf(print)
            )) {
                return new InputStreamResource(pdfIs);
            }
            

        } catch (JRException | SQLException | IOException e) {
            throw new RuntimeException("Erro ao gerar Relatório", e);
        }
        
    }
}
